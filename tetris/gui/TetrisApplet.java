/**
 * @author Nikita Kouevda
 * @date 2012/06/02
 */

package tetris.gui;

import javax.swing.JApplet;
import tetris.game.TetrisGame;

public class TetrisApplet extends JApplet {
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private TetrisPanel myPanel;

    private TetrisGame myGame;

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    @Override
    public void init() {
        super.init();

        // Construct the panel and override the content pane with the it
        myPanel = new TetrisPanel();
        setContentPane(myPanel);

        // Get the game instance from the panel
        myGame = myPanel.getGame();

        // Add a key listener and allow this applet to be focusable
        addKeyListener(new TetrisKeyListener(this, myGame, true));
        setFocusable(true);
    }
}
