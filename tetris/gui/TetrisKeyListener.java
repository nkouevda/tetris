/**
 * @author Nikita Kouevda
 * @date 2013/10/05
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
    private static final int DELAY_MILLIS = 150, REPEAT_MILLIS = 20;

    private Component component;

    private TetrisGame game;

    private Timer delayTimer, repeatTimer;

    private boolean runningAsApplet;

    private int currentRepeat;

    public TetrisKeyListener(final Component component, final TetrisGame game,
            boolean runningAsApplet) {
        this.component = component;
        this.game = game;
        this.runningAsApplet = runningAsApplet;
        currentRepeat = 0;

        // Initialize the delay timer with a listener
        delayTimer =
            new Timer(DELAY_MILLIS - REPEAT_MILLIS, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    repeatTimer.start();
                    delayTimer.stop();
                }
            });

        // Disble repeats for the delay timer
        delayTimer.setRepeats(false);

        // Initialize the repeat timer with a listener
        repeatTimer = new Timer(REPEAT_MILLIS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentRepeat == KeyEvent.VK_LEFT) {
                    game.moveTetrominoLeft();
                } else if (currentRepeat == KeyEvent.VK_RIGHT) {
                    game.moveTetrominoRight();
                } else if (currentRepeat == KeyEvent.VK_DOWN) {
                    game.moveTetrominoDown();
                } else {
                    repeatTimer.stop();
                }

                // Repaint the component to reflect changes
                component.repaint();
            }
        });
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_NUMPAD8:
                game.rotateTetromino(e.getModifiers() != 0);
                break;
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_ENTER:
                game.dropTetromino(e.getModifiers() != 0);
                break;
            case KeyEvent.VK_SHIFT:
                game.holdTetromino();
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_NUMPAD4:
                game.moveTetrominoLeft();
                currentRepeat = KeyEvent.VK_LEFT;

                if (!delayTimer.isRunning() && !repeatTimer.isRunning()) {
                    delayTimer.start();
                }

                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_NUMPAD6:
                game.moveTetrominoRight();
                currentRepeat = KeyEvent.VK_RIGHT;

                if (!delayTimer.isRunning() && !repeatTimer.isRunning()) {
                    delayTimer.start();
                }

                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_NUMPAD2:
                game.moveTetrominoDown();
                currentRepeat = KeyEvent.VK_DOWN;

                if (!repeatTimer.isRunning()) {
                    repeatTimer.start();
                }

                break;
        }

        // Check unmodified keys if running as applet
        if (runningAsApplet) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_N:
                    game.startGame();
                    break;
                case KeyEvent.VK_P:
                    game.pauseGame();
                    break;
                case KeyEvent.VK_R:
                    game.setRotateClockwise(!game.isRotateClockwise());
                    break;
                case KeyEvent.VK_D:
                    game.setMoveAfterDrop(!game.isMoveAfterDrop());
                    break;
                case KeyEvent.VK_S:
                    game.setDisplayShadow(!game.isDisplayShadow());
                    break;
            }
        }

        // Repaint the component to reflect changes
        component.repaint();
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
                delayTimer.stop();
                repeatTimer.stop();
                break;
        }
    }
}
