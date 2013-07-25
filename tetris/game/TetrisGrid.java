/**
 * @author Nikita Kouevda
 * @date 2012/06/02
 */

package tetris.game;

import java.util.Arrays;

public class TetrisGrid {
    // -------------------------------------------------------------------------
    // Enumerations
    // -------------------------------------------------------------------------

    public static enum SquareType {
        I, J, L, O, S, T, Z, SHADOW, EMPTY
    }

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private SquareType[][] myGrid;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public TetrisGrid(int cols, int rows) {
        myGrid = new SquareType[cols][rows];
        clear();
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    public int getNumCols() {
        return myGrid.length;
    }

    public int getNumRows() {
        return myGrid[0].length;
    }

    public SquareType get(int col, int row) {
        return myGrid[col][row];
    }

    public void set(int col, int row, SquareType type) {
        myGrid[col][row] = type;
    }

    public boolean isOccupied(int col, int row) {
        return myGrid[col][row] != SquareType.EMPTY
                && myGrid[col][row] != SquareType.SHADOW;
    }

    public void clear() {
        for (SquareType[] col : myGrid)
            Arrays.fill(col, SquareType.EMPTY);
    }
}
