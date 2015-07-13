package tetris.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;

import javax.swing.JPanel;
import javax.swing.Timer;

import tetris.game.TetrisGame;
import tetris.game.TetrisGame.GameState;
import tetris.game.TetrisGrid.SquareType;

public class TetrisPanel extends JPanel {
    private static final int GAP = 1;

    private static final EnumMap<SquareType, Color> COLORS;

    private TetrisGame game;

    private Timer timer;

    private int squareWidth, basketWidth, basketHeight, smallDimension;

    static {
        COLORS = new EnumMap<SquareType, Color>(SquareType.class);
        COLORS.put(SquareType.I, new Color(0, 230, 230));
        COLORS.put(SquareType.J, new Color(0, 0, 230));
        COLORS.put(SquareType.L, new Color(250, 167, 0));
        COLORS.put(SquareType.O, new Color(250, 250, 0));
        COLORS.put(SquareType.S, new Color(0, 230, 0));
        COLORS.put(SquareType.T, new Color(115, 0, 230));
        COLORS.put(SquareType.Z, new Color(230, 0, 0));
        COLORS.put(SquareType.EMPTY, Color.WHITE);
        COLORS.put(SquareType.SHADOW, Color.LIGHT_GRAY);
    }

    public TetrisPanel() {
        // Call the super constructor with true in order to double buffer
        super(true);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.moveTetrominoDownTimer();
                repaint();
            }
        });

        game = new TetrisGame(timer);
    }

    public TetrisGame getGame() {
        return game;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(0xEEEEEE));

        // Define the constants relative to the bounds of this panel
        squareWidth =
            Math.min((int)((getWidth() - (game.getBasketGrid().getNumCols()
                + game.getNextGrid().getNumCols()
                + game.getHoldGrid().getNumCols() + 3)
                * GAP) / (game.getBasketGrid().getNumCols()
                + game.getNextGrid().getNumCols()
                + game.getHoldGrid().getNumCols() + 4.5)), (getHeight() - (game
                .getBasketGrid().getNumRows() - 1) * GAP)
                / (game.getBasketGrid().getNumRows()));
        basketWidth =
            game.getBasketGrid().getNumCols() * (squareWidth + GAP) + GAP;
        basketHeight =
            (game.getBasketGrid().getNumRows() - 2) * (squareWidth + GAP) + GAP;
        smallDimension =
            game.getNextGrid().getNumCols() * (squareWidth + GAP) + GAP;

        // Translate the origin to the top left corner
        g.translate(
            (getWidth() - (basketWidth + 5 * squareWidth / 2 + 2 * smallDimension)) / 2,
            (getHeight() - basketHeight) / 2);

        // Paint the rectangles around the basket and next areas
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(smallDimension + 5 * squareWidth / 4, 0, basketWidth - 1,
            basketHeight - 1);
        g.drawRect(smallDimension + basketWidth + 5 * squareWidth / 2, 0,
            smallDimension - 1, smallDimension - 1);
        g.drawRect(0, 0, smallDimension - 1, smallDimension - 1);

        // Paint each of the squares of the hold grid
        for (int col = 0; col < game.getHoldGrid().getNumCols(); ++col) {
            for (int row = 0; row < game.getHoldGrid().getNumRows(); ++row) {
                paintSquare(g, game.getHoldGrid().get(col, row), col
                    * (squareWidth + GAP) + GAP, (game.getHoldGrid()
                    .getNumRows() - row - 1)
                    * (squareWidth + GAP) + GAP);
            }
        }

        // Paint each of the squares of the basket grid
        for (int col = 0; col < game.getBasketGrid().getNumCols(); ++col) {
            for (int row = 0; row < game.getBasketGrid().getNumRows() - 2; ++row) {
                paintSquare(g, game.getBasketGrid().get(col, row),
                    smallDimension + 5 * squareWidth / 4 + col
                        * (squareWidth + GAP) + GAP, (game.getBasketGrid()
                        .getNumRows() - row - 3)
                        * (squareWidth + GAP) + GAP);
            }
        }

        // Paint each of the squares of the next grid
        for (int col = 0; col < game.getNextGrid().getNumCols(); ++col) {
            for (int row = 0; row < game.getNextGrid().getNumRows(); ++row) {
                paintSquare(g, game.getNextGrid().get(col, row), smallDimension
                    + basketWidth + 5 * squareWidth / 2 + col
                    * (squareWidth + GAP) + GAP, (game.getNextGrid()
                    .getNumRows() - row - 1)
                    * (squareWidth + GAP) + GAP);
            }
        }

        // Translate the origin to the top left corner of the statistics
        g.translate(0, smallDimension + 7 * (squareWidth + GAP) / 4);

        // Set the text color and size
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", 0, (squareWidth + GAP) / 2 + 3));

        // Translate the origin to the top left corner of the basic info

        // Paint the score, the number of lines removed, and the level
        g.drawString("Score: " + game.getScore(), 0, 0);
        g.drawString("Lines: " + game.getLines(), 0,
            3 * (squareWidth + GAP) / 2);
        g.drawString("Level: " + game.getLevel(), 0, 3 * (squareWidth + GAP));

        // Paint "Paused" or "Game Over" if necessary
        if (game.getState() != GameState.ON) {
            g.setColor(Color.RED);

            g.drawString(game.getState() == GameState.PAUSED ? "Paused"
                : "Game Over", 0, 9 * (squareWidth + GAP) / 2);
        }
    }

    private void paintSquare(Graphics g, SquareType type, int x, int y) {
        // Paint shadow squares only if the shadow option is selected
        g.setColor(COLORS.get(type != SquareType.SHADOW
            || game.isDisplayShadow() ? type : SquareType.EMPTY));
        g.fillRect(x, y, squareWidth, squareWidth);
    }
}
