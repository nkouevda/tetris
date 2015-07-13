package tetris.game;

import java.util.ArrayList;

import javax.swing.Timer;

import tetris.game.TetrisGrid.SquareType;

public class TetrisGame {
    public enum GameState {
        OFF, ON, PAUSED
    }

    public static final int MAX_LEVEL = 20, SMALL_GRID_SIZE = 4;

    private static final int DEFAULT_BASKET_COLS = 10,
            DEFAULT_BASKET_ROWS = 20, LINES_PER_LEVEL = 10,
            MILLIS_PER_LEVEL = 50;

    private Timer stepTimer;

    private TetrisGrid basketGrid, nextGrid, holdGrid;

    private TypeGenerator typeGenerator;

    private Tetromino currentTetromino, nextTetromino, holdTetromino;

    private GameState gameState;

    private int score, lines, level, initialLevel;

    private boolean rotateClockwise, moveAfterDrop, displayShadow, holdUsed;

    public TetrisGame(Timer timer) {
        stepTimer = timer;

        // 2 extra spaces above, for basket only
        basketGrid =
            new TetrisGrid(DEFAULT_BASKET_COLS, DEFAULT_BASKET_ROWS + 2);
        nextGrid = new TetrisGrid(SMALL_GRID_SIZE, SMALL_GRID_SIZE);
        holdGrid = new TetrisGrid(SMALL_GRID_SIZE, SMALL_GRID_SIZE);

        typeGenerator = new TypeGenerator();

        gameState = GameState.OFF;
        score = lines = 0;
        initialLevel = 1;
    }

    public TetrisGrid getBasketGrid() {
        return basketGrid;
    }

    public TetrisGrid getNextGrid() {
        return nextGrid;
    }

    public TetrisGrid getHoldGrid() {
        return holdGrid;
    }

    public GameState getState() {
        return gameState;
    }

    public int getScore() {
        return score;
    }

    public int getLines() {
        return lines;
    }

    public int getLevel() {
        return level;
    }

    public boolean isRotateClockwise() {
        return rotateClockwise;
    }

    public boolean isMoveAfterDrop() {
        return moveAfterDrop;
    }

    public boolean isDisplayShadow() {
        return displayShadow;
    }

    public void setRotateClockwise(boolean rotateClockwise) {
        this.rotateClockwise = rotateClockwise;
    }

    public void setMoveAfterDrop(boolean moveAfterDrop) {
        this.moveAfterDrop = moveAfterDrop;
    }

    public void setDisplayShadow(boolean displayShadow) {
        this.displayShadow = displayShadow;

        if (gameState != GameState.OFF) {
            currentTetromino.setDisplayShadow(displayShadow);
        }
    }

    public void setInitialLevel(int initialLevel) {
        endGame();
        this.initialLevel = initialLevel;
    }

    public void setBasketSize(int cols, int rows) {
        endGame();
        basketGrid = new TetrisGrid(cols, rows + 2);
    }

    public void startGame() {
        basketGrid.clear();
        nextGrid.clear();
        holdGrid.clear();

        gameState = GameState.ON;
        score = lines = 0;
        level = initialLevel;
        holdUsed = false;

        typeGenerator.reset();

        currentTetromino =
            new Tetromino(typeGenerator.getNextType(), basketGrid,
                displayShadow);

        // Move down once for visibility
        currentTetromino.moveDown();

        nextTetromino =
            new Tetromino(typeGenerator.getNextType(), nextGrid, false);
        nextTetromino.moveDown();

        holdTetromino = null;

        stepTimer.setInitialDelay(1000 - (initialLevel - 1) * MILLIS_PER_LEVEL);
        stepTimer.setDelay(stepTimer.getInitialDelay());
        stepTimer.restart();
    }

    public void pauseGame() {
        if (gameState == GameState.ON) {
            gameState = GameState.PAUSED;
            stepTimer.stop();
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.ON;
            stepTimer.start();
        }
    }

    public void endGame() {
        gameState = GameState.OFF;
        stepTimer.stop();
    }

    public void moveTetrominoLeft() {
        if (gameState == GameState.ON) {
            currentTetromino.moveLeft();
        }
    }

    public void moveTetrominoRight() {
        if (gameState == GameState.ON) {
            currentTetromino.moveRight();
        }
    }

    public void moveTetrominoDown() {
        if (gameState != GameState.ON) {
            return;
        }

        if (currentTetromino.moveDown()) {
            ++score;

            stepTimer.restart();
        }
    }

    public void moveTetrominoDownTimer() {
        if (gameState == GameState.ON && !currentTetromino.moveDown()) {
            nextTetromino();
        }
    }

    public void dropTetromino(boolean modifyDrop) {
        if (gameState == GameState.ON) {
            int linesMoved = currentTetromino.drop();

            score += linesMoved;

            if (linesMoved == 0 || moveAfterDrop == modifyDrop) {
                nextTetromino();
            } else {
                stepTimer.restart();
            }
        }
    }

    public void rotateTetromino(boolean switchRotate) {
        if (gameState == GameState.ON) {
            currentTetromino.rotate(rotateClockwise ^ switchRotate);
        }
    }

    public void holdTetromino() {
        if (gameState != GameState.ON) {
            return;
        }

        // Disallow multiple holds per piece
        if (holdUsed) {
            return;
        } else {
            holdUsed = true;
        }

        SquareType currentType = currentTetromino.getType();

        if (holdTetromino == null) {
            // Transfer the current tetromino to the hold grid
            currentTetromino.removeFromGrid();
            holdTetromino = new Tetromino(currentType, holdGrid, false);
            holdTetromino.moveDown();

            // End the game if next tetromino cannot spawn in the basket
            if (!nextTetromino.isLegalSpawn(basketGrid)) {
                endGame();
            }

            // Transfer next tetromino to the basket
            currentTetromino =
                new Tetromino(nextTetromino.getType(), basketGrid,
                    displayShadow);

            // Move down once for visibility
            currentTetromino.moveDown();

            nextTetromino.removeFromGrid();
            nextTetromino =
                new Tetromino(typeGenerator.getNextType(), nextGrid, false);
            nextTetromino.moveDown();
        } else {
            // Remove the old hold tetromino and store its type
            holdTetromino.removeFromGrid();
            SquareType holdType = holdTetromino.getType();

            // Transfer the current tetromino to the hold grid
            currentTetromino.removeFromGrid();
            holdTetromino = new Tetromino(currentType, holdGrid, false);
            holdTetromino.moveDown();

            // End the game if next tetromino cannot spawn in the basket
            if (!nextTetromino.isLegalSpawn(basketGrid)) {
                endGame();
            }

            // Transfer the hold tetromino back into the basket
            currentTetromino =
                new Tetromino(holdType, basketGrid, displayShadow);

            // Move down once for visibility
            currentTetromino.moveDown();
        }

        stepTimer.restart();
    }

    private void nextTetromino() {
        // End the game if the current tetromino locked too high
        if (currentTetromino.isIllegalLock()) {
            endGame();
        }

        removeLines();
        holdUsed = false;

        // End the game if next tetromino cannot spawn in the basket
        if (!nextTetromino.isLegalSpawn(basketGrid)) {
            endGame();
        }

        // Increase level and set timer's delay accordingly if necessary
        if (lines / LINES_PER_LEVEL == (level - initialLevel + 1)) {
            if (++level <= MAX_LEVEL) {
                stepTimer.setInitialDelay(stepTimer.getInitialDelay()
                    - MILLIS_PER_LEVEL);
                stepTimer.setDelay(stepTimer.getInitialDelay());
            }
        }

        // Transfer next tetromino to current tetromino
        currentTetromino =
            new Tetromino(nextTetromino.getType(), basketGrid, displayShadow);

        // Move down once for visibility
        currentTetromino.moveDown();

        nextTetromino.removeFromGrid();
        nextTetromino =
            new Tetromino(typeGenerator.getNextType(), nextGrid, false);
        nextTetromino.moveDown();

        stepTimer.restart();
    }

    private void removeLines() {
        int linesCleared = 0;

        for (int row = basketGrid.getNumRows() - 1; row >= 0;) {
            boolean lineFull = true;

            // Continue to next iteration if any square is empty
            for (int col = 0; col < basketGrid.getNumCols(); ++col) {
                if (!basketGrid.isOccupied(col, row)) {
                    lineFull = false;
                    break;
                }
            }

            if (lineFull) {
                linesCleared++;

                // Shift basket down one row to clear the filled row
                for (int j = 0; j < basketGrid.getNumCols(); ++j) {
                    for (int i = row; i < basketGrid.getNumRows() - 2; ++i) {
                        basketGrid.set(j, i, basketGrid.get(j, i + 1));
                    }

                    basketGrid.set(j, basketGrid.getNumRows() - 1,
                        SquareType.EMPTY);
                }
            } else {
                --row;
            }
        }

        if (linesCleared > 0) {
            // Add the number of lines removed this time to the total
            lines += linesCleared;

            // (40, 100, 300, 1200)[linesCleared] * level
            score +=
                (linesCleared == 1 ? 40 : linesCleared == 2 ? 100
                    : linesCleared == 3 ? 300 : 1200) * level;
        }
    }

    private class TypeGenerator {
        private final SquareType[] SQUARE_TYPES;

        private ArrayList<SquareType> typeList;

        private TypeGenerator() {
            SQUARE_TYPES =
                new SquareType[] {SquareType.I, SquareType.J, SquareType.L,
                    SquareType.O, SquareType.S, SquareType.T, SquareType.Z};

            typeList = new ArrayList<SquareType>(SQUARE_TYPES.length);
        }

        private SquareType getNextType() {
            // Refill the list if necessary
            if (typeList.isEmpty()) {
                for (SquareType type : SQUARE_TYPES) {
                    typeList.add(type);
                }
            }

            // Remove and return a random member of the list
            return typeList.remove((int)(typeList.size() * Math.random()));
        }

        private void reset() {
            typeList.clear();

            for (SquareType type : SQUARE_TYPES) {
                typeList.add(type);
            }
        }
    }
}
