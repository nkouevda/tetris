/**
 * @author Nikita Kouevda
 * @date 2012/03/27
 */

package tetris.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import tetris.game.TetrisGame;

public class TetrisMenuBar extends JMenuBar {
    // -----------------------------------------------------------------
    // Fields
    // -----------------------------------------------------------------
    
    private TetrisGame myGame;
    
    private TetrisPanel myPanel;
    
    private JMenuItem newGame, pause, quit, customInitialLevel,
            customSize, howToPlay, about;
    
    private JCheckBoxMenuItem rotateClockwise, moveAfterDrop,
            displayShadow;
    
    // -----------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------
    
    public TetrisMenuBar(TetrisPanel panel) {
        super();
        
        myPanel = panel;
        myGame = myPanel.getGame();
        
        // Construct the menu bar listener
        MenuBarListener myMenuBarListener = new MenuBarListener();
        
        // Constrcut the menus and set their properties
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setMnemonic('G');
        
        JMenu settingsMenu = new JMenu("Settings");
        settingsMenu.setMnemonic('S');
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        
        // Initialize the menu items and set their properties
        newGame = new JMenuItem("New Game", 'N');
        newGame.addActionListener(myMenuBarListener);
        newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                KeyEvent.VK_ALT)); // VK_ALT = Ctrl key
        
        pause = new JMenuItem("Pause", 'P');
        pause.addActionListener(myMenuBarListener);
        pause.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                KeyEvent.VK_ALT)); // VK_ALT = Ctrl key
        
        quit = new JMenuItem("Quit", 'Q');
        quit.addActionListener(myMenuBarListener);
        quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                KeyEvent.VK_ALT)); // VK_ALT = Ctrl key
        
        rotateClockwise = new JCheckBoxMenuItem("Rotate Clockwise");
        rotateClockwise.setMnemonic('R');
        rotateClockwise.addActionListener(myMenuBarListener);
        rotateClockwise.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_R, KeyEvent.VK_ALT)); // VK_ALT = Ctrl key
        
        moveAfterDrop = new JCheckBoxMenuItem("Move After Drop");
        moveAfterDrop.setMnemonic('D');
        moveAfterDrop.addActionListener(myMenuBarListener);
        moveAfterDrop.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_D, KeyEvent.VK_ALT)); // VK_ALT = Ctrl key
        
        displayShadow = new JCheckBoxMenuItem("Display Shadow");
        displayShadow.setMnemonic('S');
        displayShadow.addActionListener(myMenuBarListener);
        displayShadow.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, KeyEvent.VK_ALT)); // VK_ALT = Ctrl key
        
        customInitialLevel = new JMenuItem("Custom Initial Level", 'C');
        customInitialLevel.addActionListener(myMenuBarListener);
        
        customSize = new JMenuItem("Custom Size", 'U');
        customSize.addActionListener(myMenuBarListener);
        
        howToPlay = new JMenuItem("How to Play", 'H');
        howToPlay.addActionListener(myMenuBarListener);
        howToPlay.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,
                0));
        
        about = new JMenuItem("About", 'A');
        about.addActionListener(myMenuBarListener);
        
        // Add the menu items to the menus
        gameMenu.add(newGame);
        gameMenu.add(pause);
        gameMenu.add(new JSeparator());
        gameMenu.add(quit);
        
        settingsMenu.add(rotateClockwise);
        settingsMenu.add(moveAfterDrop);
        settingsMenu.add(displayShadow);
        settingsMenu.add(new JSeparator());
        settingsMenu.add(customInitialLevel);
        settingsMenu.add(customSize);
        
        helpMenu.add(howToPlay);
        helpMenu.add(new JSeparator());
        helpMenu.add(about);
        
        // Add the menus to the menu bar
        add(gameMenu);
        add(settingsMenu);
        add(helpMenu);
    }
    
    // -----------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------
    
    public void pause() {
        pause.setText("Resume");
    }
    
    public void resume() {
        pause.setText("Pause");
    }
    
    private static void showHowToPlay() {
        JOptionPane
                .showMessageDialog(
                        null,
                        "Your goal is to earn as many points as possible:\n"
                                + " - Clear rows by filling entire rows with tetrominos\n"
                                + " - Clear multiple rows at once for more points\n"
                                + " - Drop tetrominos before they fall by themselves\n"
                                + "\nControls:\n"
                                + " - Ctrl+N to start a new game, Ctrl+P to pause the game\n"
                                + " - Left, Right, and Down arrow keys to move the current tetromino\n"
                                + " - Up arrow key to rotate the current tetromino\n"
                                + " - Modifier+Up to rotate the current tetromino the other way\n"
                                + " - Space or Enter to drop the current tetromino\n"
                                + " - Modifier+Space or Modifier+Enter to soft drop the current tetromino\n"
                                + " - Shift to hold the current tetromino",
                        "How to Play", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private static void showAbout() {
        JOptionPane.showMessageDialog(null,
                "Created by Nikita Kouevda\nMarch 27, 2012", "About",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    // -----------------------------------------------------------------
    // Classes
    // -----------------------------------------------------------------
    
    private class MenuBarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem source = (JMenuItem)e.getSource();
            
            // React to the event appropriately
            if (source == newGame)
                myGame.startGame();
            else if (source == pause)
                myGame.pauseGame();
            else if (source == quit)
                System.exit(0);
            else if (source == rotateClockwise)
                myGame.setRotateClockwise(rotateClockwise.isSelected());
            else if (source == moveAfterDrop)
                myGame.setMoveAfterDrop(moveAfterDrop.isSelected());
            else if (source == displayShadow)
                myGame.setDisplayShadow(displayShadow.isSelected());
            else if (source == customInitialLevel) {
                int initialLevel = 0;
                
                try {
                    initialLevel =
                            Integer.parseInt(JOptionPane
                                    .showInputDialog("Initial Level (1-"
                                            + TetrisGame.MAX_LEVEL
                                            + "):"));
                    
                    if (initialLevel < 1
                            || initialLevel > TetrisGame.MAX_LEVEL)
                        throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Illegal level!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    
                    return;
                }
                
                myGame.setInitialLevel(initialLevel);
            } else if (source == customSize) {
                int rows = 0, cols = 0;
                
                try {
                    cols =
                            Integer.parseInt(JOptionPane
                                    .showInputDialog("Width (4-50):"));
                    rows =
                            Integer.parseInt(JOptionPane
                                    .showInputDialog("Height (4-50):"));
                    
                    if (rows < TetrisGame.SMALL_GRID_SIZE || rows > 50
                            || cols < TetrisGame.SMALL_GRID_SIZE
                            || cols > 50)
                        throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Illegal dimensions!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    
                    return;
                }
                
                myGame.setBasketSize(rows, cols);
            } else if (source == howToPlay)
                showHowToPlay();
            else if (source == about)
                showAbout();
            
            // Repaint the panel to reflect any changes
            myPanel.repaint();
        }
    }
}
