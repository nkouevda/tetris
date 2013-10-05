/**
 * @author Nikita Kouevda
 * @date 2013/10/05
 */

package tetris.game;

import java.util.EnumMap;

import tetris.game.TetrisGrid.SquareType;

public class Tetromino {
    private static enum RotationState {
        UP, RIGHT, DOWN, LEFT
    }

    private final static EnumMap<SquareType, int[][]> DEFAULT_REL_LOCS;

    private final static EnumMap<RotationState, int[][]> I_ROTATION_REL_LOCS,
            ROTATION_REL_LOCS;

    private TetrisGrid grid;

    private SquareType type;

    private boolean displayShadow;

    private int[][] relLocs;

    private int row, col, shadowDistance;

    private RotationState rotationState;

    static {
        // Initialize the relative locations in the default rotation
        DEFAULT_REL_LOCS = new EnumMap<SquareType, int[][]>(SquareType.class);
        DEFAULT_REL_LOCS.put(SquareType.I, new int[][] { {0, 0}, {-1, 0},
            {1, 0}, {2, 0}});
        DEFAULT_REL_LOCS.put(SquareType.J, new int[][] { {0, 0}, {-1, 1},
            {-1, 0}, {1, 0}});
        DEFAULT_REL_LOCS.put(SquareType.L, new int[][] { {0, 0}, {-1, 0},
            {1, 1}, {1, 0}});
        DEFAULT_REL_LOCS.put(SquareType.O, new int[][] { {0, 0}, {0, 1},
            {1, 1}, {1, 0}});
        DEFAULT_REL_LOCS.put(SquareType.S, new int[][] { {0, 0}, {-1, 0},
            {0, 1}, {1, 1}});
        DEFAULT_REL_LOCS.put(SquareType.T, new int[][] { {0, 0}, {-1, 0},
            {0, 1}, {1, 0}});
        DEFAULT_REL_LOCS.put(SquareType.Z, new int[][] { {0, 0}, {-1, 1},
            {0, 1}, {1, 0}});

        // Initialize the relative locations for kicks and twists
        ROTATION_REL_LOCS =
            new EnumMap<RotationState, int[][]>(RotationState.class);
        ROTATION_REL_LOCS.put(RotationState.UP, new int[][] { {0, 0}, {-1, 0},
            {-1, 1}, {0, -2}, {-1, -2}});
        ROTATION_REL_LOCS.put(RotationState.RIGHT, new int[][] { {0, 0},
            {1, 0}, {1, -1}, {0, 2}, {1, 2}});
        ROTATION_REL_LOCS.put(RotationState.DOWN, new int[][] { {0, 0}, {1, 0},
            {1, 1}, {0, -2}, {1, -2}});
        ROTATION_REL_LOCS.put(RotationState.LEFT, new int[][] { {0, 0},
            {-1, 0}, {-1, -1}, {0, 2}, {-1, 2}});

        // Initialize the relative locations for I for kicks and twists
        I_ROTATION_REL_LOCS =
            new EnumMap<RotationState, int[][]>(RotationState.class);
        I_ROTATION_REL_LOCS.put(RotationState.UP, new int[][] { {0, 0},
            {-2, 0}, {1, 0}, {-2, -1}, {1, 2}});
        I_ROTATION_REL_LOCS.put(RotationState.RIGHT, new int[][] { {0, 0},
            {-1, 0}, {2, 0}, {-1, 2}, {2, -1}});
        I_ROTATION_REL_LOCS.put(RotationState.DOWN, new int[][] { {0, 0},
            {2, 0}, {-1, 0}, {2, 1}, {-1, -2}});
        I_ROTATION_REL_LOCS.put(RotationState.LEFT, new int[][] { {0, 0},
            {1, 0}, {-2, 0}, {1, -2}, {-2, 1}});
    }

    public Tetromino(SquareType type, TetrisGrid grid, boolean displayShadow) {
        this.grid = grid;
        this.type = type;
        this.displayShadow = displayShadow;

        relLocs =
            new int[][] {DEFAULT_REL_LOCS.get(this.type)[0].clone(),
                DEFAULT_REL_LOCS.get(this.type)[1].clone(),
                DEFAULT_REL_LOCS.get(this.type)[2].clone(),
                DEFAULT_REL_LOCS.get(this.type)[3].clone()};

        row = this.grid.getNumRows() - 2;
        col = (this.grid.getNumCols() - 1) / 2;
        rotationState = RotationState.UP;
        shadowDistance = 0;

        updateGrid();
    }

    public SquareType getType() {
        return type;
    }

    public void setDisplayShadow(boolean displayShadow) {
        this.displayShadow = displayShadow;

        // Remove the shadow if it was enabled
        if (!displayShadow) {
            removeShadow();
        }

        updateGrid();
    }

    public boolean isIllegalLock() {
        boolean isCompletelyAbove = true;

        // Override if any individual square is below the threshold
        for (int[] relLoc : relLocs) {
            if (relLoc[1] + row < grid.getNumRows() - 2) {
                isCompletelyAbove = false;
                break;
            }
        }

        return isCompletelyAbove;
    }

    public boolean isLegalSpawn(TetrisGrid destinationGrid) {
        boolean canSpawn = true;

        int destinationRow = destinationGrid.getNumRows() - 2, destinationCol =
            (destinationGrid.getNumCols() - 1) / 2;

        // Override if any individual square cannot spawn
        for (int[] relLoc : relLocs) {
            if (destinationGrid.isOccupied(relLoc[0] + destinationCol,
                relLoc[1] + destinationRow)) {
                canSpawn = false;
                break;
            }
        }

        return canSpawn;
    }

    public boolean moveLeft() {
        // Return false if the tetromino cannot move left
        for (int[] relLoc : relLocs) {
            if (relLoc[0] + col <= 0
                || grid.isOccupied(relLoc[0] + col - 1, relLoc[1] + row)
                && !isSelfOccupied(relLoc[0] + col - 1, relLoc[1] + row)) {
                return false;
            }
        }

        // Empty the old locations (including shadow)
        removeFromGrid();

        // Move the tetromino left once
        --col;

        // Update the grid
        updateGrid();

        return true;
    }

    public boolean moveRight() {
        // Return false if the tetromino cannot move right
        for (int[] relLoc : relLocs) {
            if (relLoc[0] + col >= grid.getNumCols() - 1
                || grid.isOccupied(relLoc[0] + col + 1, relLoc[1] + row)
                && !isSelfOccupied(relLoc[0] + col + 1, relLoc[1] + row)) {
                return false;
            }
        }

        // Empty the old locations (including shadow)
        removeFromGrid();

        // Move the tetromino right once
        ++col;

        // Update the grid
        updateGrid();

        return true;
    }

    public boolean moveDown() {
        // Return false if the tetromino cannot move down
        for (int[] relLoc : relLocs) {
            if (relLoc[1] + row <= 0
                || grid.isOccupied(relLoc[0] + col, relLoc[1] + row - 1)
                && !isSelfOccupied(relLoc[0] + col, relLoc[1] + row - 1)) {
                return false;
            }
        }

        // Empty the old locations (including shadow)
        removeFromGrid();

        // Move the tetromino down once
        --row;

        // Update the grid
        updateGrid();

        return true;
    }

    public int drop() {
        int rowsDropped = 0;

        while (moveDown()) {
            ++rowsDropped;
        }

        return rowsDropped;
    }

    public boolean rotate(boolean rotateClockwise) {
        // Return false if this tetromino's type is O
        if (type == SquareType.O) {
            return false;
        }

        // Determine the destination rotation
        RotationState stateRotatingTo =
            rotateClockwise ? getNextStateCW(rotationState)
                : getNextStateCCW(rotationState);

        // Array of default relative locations for rotation
        int[][] relLocsTo =
            new int[][] {relLocs[0].clone(), relLocs[1].clone(),
                relLocs[2].clone(), relLocs[3].clone()};

        // Adjust center for type I tetromino
        int defaultColTo = col, defaultRowTo = row;

        // Move the I tetromino out if rotating clockwise
        if (type == SquareType.I && rotateClockwise) {
            defaultColTo += relLocsTo[2][0];
            defaultRowTo += relLocsTo[2][1];
        }

        // Flip the pairs of coordinates over the y = x line
        for (int[] relLoc : relLocsTo) {
            int col = relLoc[0];
            relLoc[0] = relLoc[1];
            relLoc[1] = col;
        }

        // Negate the row or column depending on rotation setting
        if (rotateClockwise) {
            for (int[] relLoc : relLocsTo) {
                relLoc[1] = -relLoc[1];
            }
        } else {
            for (int[] relLoc : relLocsTo) {
                relLoc[0] = -relLoc[0];
            }
        }

        // Move the I tetromino in if rotating counterclockwise
        if (type == SquareType.I && !rotateClockwise) {
            defaultColTo -= relLocsTo[2][0];
            defaultRowTo -= relLocsTo[2][1];
        }

        // Adjustments for kick states
        int[][] relLocsToAdjustment =
            rotateClockwise ? relLocsToAdjustment =
                type == SquareType.I ? I_ROTATION_REL_LOCS.get(rotationState)
                    : ROTATION_REL_LOCS.get(rotationState)
                : type == SquareType.I ? I_ROTATION_REL_LOCS
                    .get(stateRotatingTo) : ROTATION_REL_LOCS
                    .get(stateRotatingTo);

        // Actual central column and row for rotation
        int colTo = defaultColTo, rowTo = defaultRowTo;

        // Control of inner loop
        boolean canRotate = false;

        // Iterate over the kick states
        for (int i = 0, j; i < relLocsToAdjustment.length; ++i) {
            canRotate = true;

            for (j = 0; j < relLocsTo.length; ++j) {
                colTo =
                    defaultColTo
                        + (rotateClockwise ? relLocsToAdjustment[i][0]
                            : -relLocsToAdjustment[i][0]);
                rowTo =
                    defaultRowTo
                        + (rotateClockwise ? relLocsToAdjustment[i][1]
                            : -relLocsToAdjustment[i][1]);
            }

            // Check whether the tetromino can be rotated
            for (int[] relLoc : relLocsTo) {
                if (relLoc[0] + colTo < 0
                    || relLoc[0] + colTo > grid.getNumCols() - 1
                    || relLoc[1] + rowTo < 0
                    || relLoc[1] + rowTo > grid.getNumRows() - 1
                    || grid.isOccupied(relLoc[0] + colTo, relLoc[1] + rowTo)
                    && !isSelfOccupied(relLoc[0] + colTo, relLoc[1] + rowTo)) {
                    canRotate = false;
                    break;
                }
            }

            if (canRotate) {
                break;
            }
        }

        // Return false if rotation is impossible
        if (!canRotate) {
            return false;
        }

        // Empty the old locations (including shadow)
        removeFromGrid();

        // Update the current relative locations
        for (int i = 0; i < relLocs.length; ++i) {
            relLocs[i][0] = relLocsTo[i][0];
            relLocs[i][1] = relLocsTo[i][1];
        }

        // Update the new row and column
        col = colTo;
        row = rowTo;
        rotationState = stateRotatingTo;

        // Update the grid
        updateGrid();

        return true;
    }

    public void removeFromGrid() {
        for (int[] relLoc : relLocs) {
            grid.set(relLoc[0] + col, relLoc[1] + row, SquareType.EMPTY);
        }

        if (displayShadow) {
            removeShadow();
        }
    }

    private void removeShadow() {
        for (int[] relLoc : relLocs) {
            grid.set(relLoc[0] + col, relLoc[1] + row - shadowDistance,
                SquareType.EMPTY);
        }
    }

    private boolean isSelfOccupied(int col, int row) {
        for (int[] ownLoc : relLocs) {
            if (col == ownLoc[0] + this.col && row == ownLoc[1] + this.row) {
                return true;
            }
        }

        return false;
    }

    private RotationState getNextStateCW(RotationState state) {
        switch (state) {
            case UP:
                return RotationState.RIGHT;
            case RIGHT:
                return RotationState.DOWN;
            case DOWN:
                return RotationState.LEFT;
            case LEFT:
                return RotationState.UP;
            default:
                return null;
        }
    }

    private RotationState getNextStateCCW(RotationState state) {
        switch (state) {
            case UP:
                return RotationState.LEFT;
            case RIGHT:
                return RotationState.UP;
            case DOWN:
                return RotationState.RIGHT;
            case LEFT:
                return RotationState.DOWN;
            default:
                return null;
        }
    }

    private void updateGrid() {
        // Draw the shadow (previous should already have been removed)
        if (displayShadow) {
            shadowDistance = 0;

            boolean canMoveDown = true;

            // Drop the shadow to calculate new distance
            while (true) {
                for (int[] relLoc : relLocs) {
                    if (relLoc[1] + row - shadowDistance <= 0
                        || grid.isOccupied(relLoc[0] + col, relLoc[1] + row
                            - shadowDistance - 1)
                        && !isSelfOccupied(relLoc[0] + col, relLoc[1] + row
                            - shadowDistance - 1)) {
                        canMoveDown = false;
                        break;
                    }
                }

                if (!canMoveDown) {
                    break;
                }

                ++shadowDistance;
            }

            for (int[] relLoc : relLocs) {
                grid.set(relLoc[0] + col, relLoc[1] + row - shadowDistance,
                    SquareType.SHADOW);
            }
        }

        // Place this tetromino in the grid (overlaps shadow)
        for (int[] relLoc : relLocs) {
            grid.set(relLoc[0] + col, relLoc[1] + row, type);
        }
    }
}
