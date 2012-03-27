/**
 * @author Nikita Kouevda
 * @date 2012/03/27
 */

package tetris.game;

import java.util.EnumMap;
import tetris.game.TetrisGrid.SquareType;

public class Tetromino {
    // -----------------------------------------------------------------
    // Enumerations
    // -----------------------------------------------------------------
    
    private static enum RotationState {
        UP, RIGHT, DOWN, LEFT
    }
    
    // -----------------------------------------------------------------
    // Fields
    // -----------------------------------------------------------------
    
    private final static EnumMap<SquareType, int[][]> DEFAULT_REL_LOCS;
    
    private final static EnumMap<RotationState, int[][]> I_ROTATION_REL_LOCS,
            ROTATION_REL_LOCS;
    
    private TetrisGrid myGrid;
    
    private SquareType myType;
    
    private boolean myDisplayShadow;
    
    private int[][] myRelLocs;
    
    private int myRow, myCol, myShadowDistance;
    
    private RotationState myRotationState;
    
    // -----------------------------------------------------------------
    // Initializers
    // -----------------------------------------------------------------
    
    static {
        // Initialize the relative locations in the default rotation
        DEFAULT_REL_LOCS =
                new EnumMap<SquareType, int[][]>(SquareType.class);
        DEFAULT_REL_LOCS.put(SquareType.I, new int[][]{{0, 0}, {-1, 0},
                {1, 0}, {2, 0}});
        DEFAULT_REL_LOCS.put(SquareType.J, new int[][]{{0, 0}, {-1, 1},
                {-1, 0}, {1, 0}});
        DEFAULT_REL_LOCS.put(SquareType.L, new int[][]{{0, 0}, {-1, 0},
                {1, 1}, {1, 0}});
        DEFAULT_REL_LOCS.put(SquareType.O, new int[][]{{0, 0}, {0, 1},
                {1, 1}, {1, 0}});
        DEFAULT_REL_LOCS.put(SquareType.S, new int[][]{{0, 0}, {-1, 0},
                {0, 1}, {1, 1}});
        DEFAULT_REL_LOCS.put(SquareType.T, new int[][]{{0, 0}, {-1, 0},
                {0, 1}, {1, 0}});
        DEFAULT_REL_LOCS.put(SquareType.Z, new int[][]{{0, 0}, {-1, 1},
                {0, 1}, {1, 0}});
        
        // Initialize the relative locations for kicks and twists
        ROTATION_REL_LOCS =
                new EnumMap<RotationState, int[][]>(RotationState.class);
        ROTATION_REL_LOCS.put(RotationState.UP, new int[][]{{0, 0},
                {-1, 0}, {-1, 1}, {0, -2}, {-1, -2}});
        ROTATION_REL_LOCS.put(RotationState.RIGHT, new int[][]{{0, 0},
                {1, 0}, {1, -1}, {0, 2}, {1, 2}});
        ROTATION_REL_LOCS.put(RotationState.DOWN, new int[][]{{0, 0},
                {1, 0}, {1, 1}, {0, -2}, {1, -2}});
        ROTATION_REL_LOCS.put(RotationState.LEFT, new int[][]{{0, 0},
                {-1, 0}, {-1, -1}, {0, 2}, {-1, 2}});
        
        // Initialize the relative locations for I for kicks and twists
        I_ROTATION_REL_LOCS =
                new EnumMap<RotationState, int[][]>(RotationState.class);
        I_ROTATION_REL_LOCS.put(RotationState.UP, new int[][]{{0, 0},
                {-2, 0}, {1, 0}, {-2, -1}, {1, 2}});
        I_ROTATION_REL_LOCS.put(RotationState.RIGHT, new int[][]{
                {0, 0}, {-1, 0}, {2, 0}, {-1, 2}, {2, -1}});
        I_ROTATION_REL_LOCS.put(RotationState.DOWN, new int[][]{{0, 0},
                {2, 0}, {-1, 0}, {2, 1}, {-1, -2}});
        I_ROTATION_REL_LOCS.put(RotationState.LEFT, new int[][]{{0, 0},
                {1, 0}, {-2, 0}, {1, -2}, {-2, 1}});
    }
    
    // -----------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------
    
    public Tetromino(SquareType type, TetrisGrid grid,
            boolean displayShadow) {
        // Initialize the basic fields
        myGrid = grid;
        myType = type;
        myDisplayShadow = displayShadow;
        
        // Initialize the array of relative locations of squares
        myRelLocs =
                new int[][]{DEFAULT_REL_LOCS.get(myType)[0].clone(),
                        DEFAULT_REL_LOCS.get(myType)[1].clone(),
                        DEFAULT_REL_LOCS.get(myType)[2].clone(),
                        DEFAULT_REL_LOCS.get(myType)[3].clone()};
        
        // Initialize the current location and shadow distance
        myRow = myGrid.getNumRows() - 2;
        myCol = (myGrid.getNumCols() - 1) / 2;
        myRotationState = RotationState.UP;
        myShadowDistance = 0;
        
        // Update the grid
        updateGrid();
    }
    
    // -----------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------
    
    public SquareType getType() {
        return myType;
    }
    
    public void setDisplayShadow(boolean displayShadow) {
        myDisplayShadow = displayShadow;
        
        // Remove the shadow if it was enabled
        if (!myDisplayShadow)
            removeShadow();
        
        // Update the grid
        updateGrid();
    }
    
    public boolean isIllegalLock() {
        boolean isCompletelyAbove = true;
        
        // Override if any individual square is below the threshold
        for (int[] relLoc : myRelLocs)
            if (relLoc[1] + myRow < myGrid.getNumRows() - 2) {
                isCompletelyAbove = false;
                break;
            }
        
        return isCompletelyAbove;
    }
    
    public boolean isLegalSpawn(TetrisGrid destinationGrid) {
        boolean canSpawn = true;
        
        int destinationRow = destinationGrid.getNumRows() - 2, destinationCol =
                (destinationGrid.getNumCols() - 1) / 2;
        
        // Override if any individual square cannot spawn
        for (int[] relLoc : myRelLocs)
            if (destinationGrid.isOccupied(relLoc[0] + destinationCol,
                    relLoc[1] + destinationRow)) {
                canSpawn = false;
                break;
            }
        
        return canSpawn;
    }
    
    public boolean moveLeft() {
        // Return false if the tetromino cannot move left
        for (int[] relLoc : myRelLocs)
            if (relLoc[0] + myCol <= 0
                    || myGrid.isOccupied(relLoc[0] + myCol - 1,
                            relLoc[1] + myRow)
                    && !isSelfOccupied(relLoc[0] + myCol - 1, relLoc[1]
                            + myRow))
                return false;
        
        // Empty the old locations (including shadow)
        removeFromGrid();
        
        // Move the tetromino left once
        --myCol;
        
        // Update the grid
        updateGrid();
        
        return true;
    }
    
    public boolean moveRight() {
        // Return false if the tetromino cannot move right
        for (int[] relLoc : myRelLocs)
            if (relLoc[0] + myCol >= myGrid.getNumCols() - 1
                    || myGrid.isOccupied(relLoc[0] + myCol + 1,
                            relLoc[1] + myRow)
                    && !isSelfOccupied(relLoc[0] + myCol + 1, relLoc[1]
                            + myRow))
                return false;
        
        // Empty the old locations (including shadow)
        removeFromGrid();
        
        // Move the tetromino right once
        ++myCol;
        
        // Update the grid
        updateGrid();
        
        return true;
    }
    
    public boolean moveDown() {
        // Return false if the tetromino cannot move down
        for (int[] relLoc : myRelLocs)
            if (relLoc[1] + myRow <= 0
                    || myGrid.isOccupied(relLoc[0] + myCol, relLoc[1]
                            + myRow - 1)
                    && !isSelfOccupied(relLoc[0] + myCol, relLoc[1]
                            + myRow - 1))
                return false;
        
        // Empty the old locations (including shadow)
        removeFromGrid();
        
        // Move the tetromino down once
        --myRow;
        
        // Update the grid
        updateGrid();
        
        return true;
    }
    
    public int drop() {
        int rowsDropped = 0;
        
        while (moveDown())
            ++rowsDropped;
        
        return rowsDropped;
    }
    
    public boolean rotate(boolean rotateClockwise) {
        // Return false if this tetromino's type is O
        if (myType == SquareType.O)
            return false;
        
        // Determine the destination rotation
        RotationState stateRotatingTo =
                rotateClockwise ? getNextStateCW(myRotationState)
                        : getNextStateCCW(myRotationState);
        
        // Array of default relative locations for rotation
        int[][] relLocsTo =
                new int[][]{myRelLocs[0].clone(), myRelLocs[1].clone(),
                        myRelLocs[2].clone(), myRelLocs[3].clone()};
        
        // Adjust center for type I tetromino
        int defaultColTo = myCol, defaultRowTo = myRow;
        
        // Move the I tetromino out if rotating clockwise
        if (myType == SquareType.I && rotateClockwise) {
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
        if (rotateClockwise)
            for (int[] relLoc : relLocsTo)
                relLoc[1] = -relLoc[1];
        else
            for (int[] relLoc : relLocsTo)
                relLoc[0] = -relLoc[0];
        
        // Move the I tetromino in if rotating counterclockwise
        if (myType == SquareType.I && !rotateClockwise) {
            defaultColTo -= relLocsTo[2][0];
            defaultRowTo -= relLocsTo[2][1];
        }
        
        // Adjustments for kick states
        int[][] relLocsToAdjustment =
                rotateClockwise ? relLocsToAdjustment =
                        myType == SquareType.I ? I_ROTATION_REL_LOCS
                                .get(myRotationState)
                                : ROTATION_REL_LOCS
                                        .get(myRotationState)
                        : myType == SquareType.I ? I_ROTATION_REL_LOCS
                                .get(stateRotatingTo)
                                : ROTATION_REL_LOCS
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
                                + (rotateClockwise
                                        ? relLocsToAdjustment[i][0]
                                        : -relLocsToAdjustment[i][0]);
                rowTo =
                        defaultRowTo
                                + (rotateClockwise
                                        ? relLocsToAdjustment[i][1]
                                        : -relLocsToAdjustment[i][1]);
            }
            
            // Check whether the tetromino can be rotated
            for (int[] relLoc : relLocsTo)
                if (relLoc[0] + colTo < 0
                        || relLoc[0] + colTo > myGrid.getNumCols() - 1
                        || relLoc[1] + rowTo < 0
                        || relLoc[1] + rowTo > myGrid.getNumRows() - 1
                        || myGrid.isOccupied(relLoc[0] + colTo,
                                relLoc[1] + rowTo)
                        && !isSelfOccupied(relLoc[0] + colTo, relLoc[1]
                                + rowTo)) {
                    canRotate = false;
                    break;
                }
            
            if (canRotate)
                break;
        }
        
        // Return false if rotation is impossible
        if (!canRotate)
            return false;
        
        // Empty the old locations (including shadow)
        removeFromGrid();
        
        // Update the current relative locations
        for (int i = 0; i < myRelLocs.length; ++i) {
            myRelLocs[i][0] = relLocsTo[i][0];
            myRelLocs[i][1] = relLocsTo[i][1];
        }
        
        // Update the new row and column
        myCol = colTo;
        myRow = rowTo;
        myRotationState = stateRotatingTo;
        
        // Update the grid
        updateGrid();
        
        return true;
    }
    
    public void removeFromGrid() {
        for (int[] relLoc : myRelLocs)
            myGrid.set(relLoc[0] + myCol, relLoc[1] + myRow,
                    SquareType.EMPTY);
        
        if (myDisplayShadow)
            removeShadow();
    }
    
    private void removeShadow() {
        for (int[] relLoc : myRelLocs)
            myGrid.set(relLoc[0] + myCol, relLoc[1] + myRow
                    - myShadowDistance, SquareType.EMPTY);
    }
    
    private boolean isSelfOccupied(int col, int row) {
        for (int[] ownLoc : myRelLocs)
            if (col == ownLoc[0] + myCol && row == ownLoc[1] + myRow)
                return true;
        
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
        if (myDisplayShadow) {
            myShadowDistance = 0;
            
            boolean canMoveDown = true;
            
            // Drop the shadow to calculate new distance
            while (true) {
                for (int[] relLoc : myRelLocs)
                    if (relLoc[1] + myRow - myShadowDistance <= 0
                            || myGrid.isOccupied(relLoc[0] + myCol,
                                    relLoc[1] + myRow
                                            - myShadowDistance - 1)
                            && !isSelfOccupied(relLoc[0] + myCol,
                                    relLoc[1] + myRow
                                            - myShadowDistance - 1)) {
                        canMoveDown = false;
                        break;
                    }
                
                if (!canMoveDown)
                    break;
                
                ++myShadowDistance;
            }
            
            for (int[] relLoc : myRelLocs)
                myGrid.set(relLoc[0] + myCol, relLoc[1] + myRow
                        - myShadowDistance, SquareType.SHADOW);
        }
        
        // Place this tetromino in the grid (overlaps shadow)
        for (int[] relLoc : myRelLocs)
            myGrid.set(relLoc[0] + myCol, relLoc[1] + myRow, myType);
    }
}
