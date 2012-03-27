/**
 * @author Nikita Kouevda
 * @date 2012/03/27
 */

package tetris.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Timer;
import tetris.game.TetrisGame;

public class TetrisKeyListener extends KeyAdapter {
    // -----------------------------------------------------------------
    // Fields
    // -----------------------------------------------------------------
    
    private static final int DELAY_MILLIS = 150, REPEAT_MILLIS = 20;
    
    private Component myComponent;
    
    private TetrisGame myGame;
    
    private Timer myDelayTimer, myRepeatTimer;
    
    private boolean myRunningAsApplet;
    
    private int myCurrentRepeat;
    
    // -----------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------
    
    public TetrisKeyListener(Component component, TetrisGame game,
            boolean runningAsApplet) {
        myComponent = component;
        myGame = game;
        myRunningAsApplet = runningAsApplet;
        myCurrentRepeat = 0;
        
        // Initialize the delay timer with a listener
        myDelayTimer =
                new Timer(DELAY_MILLIS - REPEAT_MILLIS,
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                myRepeatTimer.start();
                                myDelayTimer.stop();
                            }
                        });
        
        // Disble repeats for the delay timer
        myDelayTimer.setRepeats(false);
        
        // Initialize the repeat timer with a listener
        myRepeatTimer = new Timer(REPEAT_MILLIS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (myCurrentRepeat == KeyEvent.VK_LEFT)
                    myGame.moveTetrominoLeft();
                else if (myCurrentRepeat == KeyEvent.VK_RIGHT)
                    myGame.moveTetrominoRight();
                else if (myCurrentRepeat == KeyEvent.VK_DOWN)
                    myGame.moveTetrominoDown();
                else
                    myRepeatTimer.stop();
                
                // Repaint the component to reflect changes
                myComponent.repaint();
            }
        });
    }
    
    // -----------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_NUMPAD8:
                myGame.rotateTetromino(e.getModifiers() != 0);
                break;
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_ENTER:
                myGame.dropTetromino(e.getModifiers() != 0);
                break;
            case KeyEvent.VK_SHIFT:
                myGame.holdTetromino();
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_NUMPAD4:
                myGame.moveTetrominoLeft();
                myCurrentRepeat = KeyEvent.VK_LEFT;
                
                if (!myDelayTimer.isRunning()
                        && !myRepeatTimer.isRunning())
                    myDelayTimer.start();
                
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_NUMPAD6:
                myGame.moveTetrominoRight();
                myCurrentRepeat = KeyEvent.VK_RIGHT;
                
                if (!myDelayTimer.isRunning()
                        && !myRepeatTimer.isRunning())
                    myDelayTimer.start();
                
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_NUMPAD2:
                myGame.moveTetrominoDown();
                myCurrentRepeat = KeyEvent.VK_DOWN;
                
                if (!myRepeatTimer.isRunning())
                    myRepeatTimer.start();
                
                break;
        }
        
        // Check unmodified keys if running as applet
        if (myRunningAsApplet)
            switch (e.getKeyCode()) {
                case KeyEvent.VK_N:
                    myGame.startGame();
                    break;
                case KeyEvent.VK_P:
                    myGame.pauseGame();
                    break;
                case KeyEvent.VK_R:
                    myGame.setRotateClockwise(!myGame
                            .isRotateClockwise());
                    break;
                case KeyEvent.VK_D:
                    myGame.setMoveAfterDrop(!myGame.isMoveAfterDrop());
                    break;
                case KeyEvent.VK_S:
                    myGame.setDisplayShadow(!myGame.isDisplayShadow());
                    break;
            }
        
        // Repaint the component to reflect changes
        myComponent.repaint();
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_NUMPAD4:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_NUMPAD6:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_NUMPAD2:
                myDelayTimer.stop();
                myRepeatTimer.stop();
                break;
        }
    }
}
