package tetris.game;

import java.util.Arrays;

public class TetrisGrid {
    public static enum SquareType {
        I, J, L, O, S, T, Z, SHADOW, EMPTY
    }

    private SquareType[][] grid;

    public TetrisGrid(int cols, int rows) {
        grid = new SquareType[cols][rows];
        clear();
    }

    public int getNumCols() {
        return grid.length;
    }

    public int getNumRows() {
        return grid[0].length;
    }

    public SquareType get(int col, int row) {
        return grid[col][row];
    }

    public void set(int col, int row, SquareType type) {
        grid[col][row] = type;
    }

    public boolean isOccupied(int col, int row) {
        return grid[col][row] != SquareType.EMPTY
            && grid[col][row] != SquareType.SHADOW;
    }

    public void clear() {
        for (SquareType[] col : grid) {
            Arrays.fill(col, SquareType.EMPTY);
        }
    }
}
