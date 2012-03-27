/**
 * @author Nikita Kouevda
 * @date 2012/03/27
 */

package tetris.game;

import java.util.ArrayList;
import javax.swing.Timer;
import tetris.game.TetrisGrid.SquareType;

public class TetrisGame {
    // -----------------------------------------------------------------
    // Enumerations
    // -----------------------------------------------------------------
    
    public enum GameState {
        OFF, ON, PAUSED
    }
    
    // -----------------------------------------------------------------
    // Fields
    // -----------------------------------------------------------------
    
    public static final int MAX_LEVEL = 20, SMALL_GRID_SIZE = 4;
    
    private static final int DEFAULT_BASKET_COLS = 10,
            DEFAULT_BASKET_ROWS = 20, LINES_PER_LEVEL = 10,
            MILLIS_PER_LEVEL = 50;
    
    private Timer myStepTimer;
    
    private TetrisGrid myBasketGrid, myNextGrid, myHoldGrid;
    
    private TypeGenerator myTypeGenerator;
    
    private Tetromino myCurrentTetromino, myNextTetromino,
            myHoldTetromino;
    
    private GameState myGameState;
    
    private int myScore, myLines, myLevel, myInitialLevel;
    
    private boolean myRotateClockwise, myMoveAfterDrop,
            myDisplayShadow, myHoldUsed;
    
    // -----------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------
    
    public TetrisGame(Timer timer) {
        myStepTimer = timer;
        
        // 2 extra spaces above, for basket only
        myBasketGrid =
                new TetrisGrid(DEFAULT_BASKET_COLS,
                        DEFAULT_BASKET_ROWS + 2);
        myNextGrid = new TetrisGrid(SMALL_GRID_SIZE, SMALL_GRID_SIZE);
        myHoldGrid = new TetrisGrid(SMALL_GRID_SIZE, SMALL_GRID_SIZE);
        
        // Construct the type generator
        myTypeGenerator = new TypeGenerator();
        
        // Initialize other fields
        myGameState = GameState.OFF;
        myScore = myLines = 0;
        myInitialLevel = 1;
    }
    
    // -----------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------
    
    public TetrisGrid getBasketGrid() {
        return myBasketGrid;
    }
    
    public TetrisGrid getNextGrid() {
        return myNextGrid;
    }
    
    public TetrisGrid getHoldGrid() {
        return myHoldGrid;
    }
    
    public GameState getState() {
        return myGameState;
    }
    
    public int getScore() {
        return myScore;
    }
    
    public int getLines() {
        return myLines;
    }
    
    public int getLevel() {
        return myLevel;
    }
    
    public boolean isRotateClockwise() {
        return myRotateClockwise;
    }
    
    public boolean isMoveAfterDrop() {
        return myMoveAfterDrop;
    }
    
    public boolean isDisplayShadow() {
        return myDisplayShadow;
    }
    
    public void setRotateClockwise(boolean rotateClockwise) {
        myRotateClockwise = rotateClockwise;
    }
    
    public void setMoveAfterDrop(boolean moveAfterDrop) {
        myMoveAfterDrop = moveAfterDrop;
    }
    
    public void setDisplayShadow(boolean displayShadow) {
        myDisplayShadow = displayShadow;
        
        if (myGameState != GameState.OFF)
            myCurrentTetromino.setDisplayShadow(myDisplayShadow);
    }
    
    public void setInitialLevel(int initialLevel) {
        endGame();
        myInitialLevel = initialLevel;
    }
    
    public void setBasketSize(int cols, int rows) {
        endGame();
        myBasketGrid = new TetrisGrid(cols, rows + 2);
    }
    
    public void startGame() {
        // Clear all grids
        myBasketGrid.clear();
        myNextGrid.clear();
        myHoldGrid.clear();
        
        // Set fields to initial values
        myGameState = GameState.ON;
        myScore = myLines = 0;
        myLevel = myInitialLevel;
        myHoldUsed = false;
        
        // Reset the type generator
        myTypeGenerator.reset();
        
        // Construct the current tetromino
        myCurrentTetromino =
                new Tetromino(myTypeGenerator.getNextType(),
                        myBasketGrid, myDisplayShadow);
        
        // Move down once for visibility (optional)
        myCurrentTetromino.moveDown();
        
        // Construct the next tetromino
        myNextTetromino =
                new Tetromino(myTypeGenerator.getNextType(),
                        myNextGrid, false);
        myNextTetromino.moveDown();
        
        // Initialize the hold tetromino to null for now
        myHoldTetromino = null;
        
        // Set the delay for the timer and restart it
        myStepTimer.setInitialDelay(1000 - (myInitialLevel - 1)
                * MILLIS_PER_LEVEL);
        myStepTimer.setDelay(myStepTimer.getInitialDelay());
        myStepTimer.restart();
    }
    
    public void pauseGame() {
        if (myGameState == GameState.ON) {
            myGameState = GameState.PAUSED;
            myStepTimer.stop();
        } else if (myGameState == GameState.PAUSED) {
            myGameState = GameState.ON;
            myStepTimer.start();
        }
    }
    
    public void endGame() {
        myGameState = GameState.OFF;
        myStepTimer.stop();
    }
    
    public void moveTetrominoLeft() {
        if (myGameState == GameState.ON)
            myCurrentTetromino.moveLeft();
    }
    
    public void moveTetrominoRight() {
        if (myGameState == GameState.ON)
            myCurrentTetromino.moveRight();
    }
    
    public void moveTetrominoDown() {
        if (myGameState != GameState.ON)
            return;
        
        if (myCurrentTetromino.moveDown()) {
            ++myScore;
            
            myStepTimer.restart();
        }
    }
    
    public void moveTetrominoDownTimer() {
        if (myGameState == GameState.ON
                && !myCurrentTetromino.moveDown())
            nextTetromino();
    }
    
    public void dropTetromino(boolean modifyDrop) {
        if (myGameState == GameState.ON) {
            int linesMoved = myCurrentTetromino.drop();
            
            myScore += linesMoved;
            
            // Dropped tetromino is negated by both setting and modifier
            if (linesMoved == 0 || myMoveAfterDrop == modifyDrop)
                nextTetromino();
            else
                myStepTimer.restart();
        }
    }
    
    public void rotateTetromino(boolean switchRotate) {
        if (myGameState == GameState.ON)
            myCurrentTetromino.rotate(myRotateClockwise ^ switchRotate);
    }
    
    public void holdTetromino() {
        if (myGameState != GameState.ON)
            return;
        
        // Disallow multiple holds per piece
        if (myHoldUsed)
            return;
        else
            myHoldUsed = true;
        
        SquareType currentType = myCurrentTetromino.getType();
        
        if (myHoldTetromino == null) {
            // Transfer the current tetromino to the hold grid
            myCurrentTetromino.removeFromGrid();
            myHoldTetromino =
                    new Tetromino(currentType, myHoldGrid, false);
            myHoldTetromino.moveDown();
            
            // End the game if next tetromino cannot spawn in the basket
            if (!myNextTetromino.isLegalSpawn(myBasketGrid))
                endGame();
            
            // Transfer next tetromino to the basket
            myCurrentTetromino =
                    new Tetromino(myNextTetromino.getType(),
                            myBasketGrid, myDisplayShadow);
            
            // Move down once for visibility (optional)
            myCurrentTetromino.moveDown();
            
            // Reset preview area and construct new next tetromino
            myNextTetromino.removeFromGrid();
            myNextTetromino =
                    new Tetromino(myTypeGenerator.getNextType(),
                            myNextGrid, false);
            myNextTetromino.moveDown();
        } else {
            // Remove the old hold tetromino and store its type
            myHoldTetromino.removeFromGrid();
            SquareType holdType = myHoldTetromino.getType();
            
            // Transfer the current tetromino to the hold grid
            myCurrentTetromino.removeFromGrid();
            myHoldTetromino =
                    new Tetromino(currentType, myHoldGrid, false);
            myHoldTetromino.moveDown();
            
            // End the game if next tetromino cannot spawn in the basket
            if (!myNextTetromino.isLegalSpawn(myBasketGrid))
                endGame();
            
            // Transfer the hold tetromino back into the basket
            myCurrentTetromino =
                    new Tetromino(holdType, myBasketGrid,
                            myDisplayShadow);
            
            // Move down once for visibility (optional)
            myCurrentTetromino.moveDown();
        }
        
        myStepTimer.restart();
    }
    
    private void nextTetromino() {
        // End the game if the current tetromino locked too high
        if (myCurrentTetromino.isIllegalLock())
            endGame();
        
        removeLines();
        myHoldUsed = false;
        
        // End the game if next tetromino cannot spawn in the basket
        if (!myNextTetromino.isLegalSpawn(myBasketGrid))
            endGame();
        
        // Increase level and set timer's delay accordingly if necessary
        if (myLines / LINES_PER_LEVEL == (myLevel - myInitialLevel + 1))
            if (++myLevel <= MAX_LEVEL) {
                myStepTimer.setInitialDelay(myStepTimer
                        .getInitialDelay() - MILLIS_PER_LEVEL);
                myStepTimer.setDelay(myStepTimer.getInitialDelay());
            }
        
        // Transfer next tetromino to current tetromino
        myCurrentTetromino =
                new Tetromino(myNextTetromino.getType(), myBasketGrid,
                        myDisplayShadow);
        
        // Move down once for visibility (optional)
        myCurrentTetromino.moveDown();
        
        // Reset preview area and construct new next tetromino
        myNextTetromino.removeFromGrid();
        myNextTetromino =
                new Tetromino(myTypeGenerator.getNextType(),
                        myNextGrid, false);
        myNextTetromino.moveDown();
        
        myStepTimer.restart();
    }
    
    private void removeLines() {
        int linesCleared = 0;
        
        for (int row = myBasketGrid.getNumRows() - 1; row >= 0;) {
            boolean lineFull = true;
            
            // Continue to next iteration if any square is empty
            for (int col = 0; col < myBasketGrid.getNumCols(); ++col)
                if (!myBasketGrid.isOccupied(col, row)) {
                    lineFull = false;
                    break;
                }
            
            if (lineFull) {
                linesCleared++;
                
                // Shift basket down one row to clear the filled row
                for (int j = 0; j < myBasketGrid.getNumCols(); ++j) {
                    for (int i = row; i < myBasketGrid.getNumRows() - 2; ++i)
                        myBasketGrid.set(j, i,
                                myBasketGrid.get(j, i + 1));
                    
                    myBasketGrid.set(j, myBasketGrid.getNumRows() - 1,
                            SquareType.EMPTY);
                }
            } else
                --row;
        }
        
        if (linesCleared > 0) {
            // Add the number of lines removed this time to the total
            myLines += linesCleared;
            
            // (40, 100, 300, 1200) * level
            myScore +=
                    (linesCleared == 1 ? 40 : linesCleared == 2 ? 100
                            : linesCleared == 3 ? 300 : 1200) * myLevel;
        }
    }
    
    // -----------------------------------------------------------------
    // Classes
    // -----------------------------------------------------------------
    
    private class TypeGenerator {
        // -------------------------------------------------------------
        // Fields
        // -------------------------------------------------------------
        
        private final SquareType[] SQUARE_TYPES;
        
        private ArrayList<SquareType> myTypeList;
        
        // -------------------------------------------------------------
        // Construtors
        // -------------------------------------------------------------
        
        private TypeGenerator() {
            SQUARE_TYPES =
                    new SquareType[]{SquareType.I, SquareType.J,
                            SquareType.L, SquareType.O, SquareType.S,
                            SquareType.T, SquareType.Z};
            
            myTypeList = new ArrayList<SquareType>(SQUARE_TYPES.length);
        }
        
        // -------------------------------------------------------------
        // Methods
        // -------------------------------------------------------------
        
        private SquareType getNextType() {
            // Refill the list if necessary
            if (myTypeList.isEmpty())
                for (SquareType type : SQUARE_TYPES)
                    myTypeList.add(type);
            
            // Remove and return a random member of the list
            return myTypeList.remove((int)(myTypeList.size() * Math
                    .random()));
        }
        
        private void reset() {
            myTypeList.clear();
            
            for (SquareType type : SQUARE_TYPES)
                myTypeList.add(type);
        }
    }
}
