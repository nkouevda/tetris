package tetris.gui;

import javax.swing.JApplet;

import tetris.game.TetrisGame;

public class TetrisApplet extends JApplet {
    private TetrisPanel panel;

    private TetrisGame game;

    @Override
    public void init() {
        super.init();

        panel = new TetrisPanel();
        setContentPane(panel);
        game = panel.getGame();

        addKeyListener(new TetrisKeyListener(this, game, true));
        setFocusable(true);
    }
}
