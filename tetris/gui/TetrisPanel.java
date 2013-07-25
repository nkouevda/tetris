/**
 * @author Nikita Kouevda
 * @date 2012/06/02
 */

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
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private static final int GAP = 1;

    private static final EnumMap<SquareType, Color> COLORS;

    private TetrisGame myGame;

    private Timer myTimer;

    private int mySquareWidth, myBasketWidth, myBasketHeight, mySmallDimension;

    // -------------------------------------------------------------------------
    // Initializers
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public TetrisPanel() {
        // Call the super constructor with true to double buffer
        super(true);

        // Initialize the timer with a listener
        myTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myGame.moveTetrominoDownTimer();
                repaint();
            }
        });

        myGame = new TetrisGame(myTimer);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    public TetrisGame getGame() {
        return myGame;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(0xEEEEEE));

        // Define the constants relative to the bounds of this panel
        mySquareWidth =
                Math.min(
                        (int)((getWidth() - (myGame.getBasketGrid()
                                .getNumCols()
                                + myGame.getNextGrid().getNumCols()
                                + myGame.getHoldGrid().getNumCols() + 3)
                                * GAP) / (myGame.getBasketGrid().getNumCols()
                                + myGame.getNextGrid().getNumCols()
                                + myGame.getHoldGrid().getNumCols() + 4.5)),
                        (getHeight() - (myGame.getBasketGrid().getNumRows() - 1)
                                * GAP)
                                / (myGame.getBasketGrid().getNumRows()));
        myBasketWidth =
                myGame.getBasketGrid().getNumCols() * (mySquareWidth + GAP)
                        + GAP;
        myBasketHeight =
                (myGame.getBasketGrid().getNumRows() - 2)
                        * (mySquareWidth + GAP) + GAP;
        mySmallDimension =
                myGame.getNextGrid().getNumCols() * (mySquareWidth + GAP) + GAP;

        // Translate the origin to the top left corner
        g.translate((getWidth() - (myBasketWidth + 5 * mySquareWidth / 2 + 2
                * mySmallDimension)) / 2,(getHeight() - myBasketHeight) / 2);

        // Paint the rectangles around the basket and next areas
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(mySmallDimension + 5 * mySquareWidth / 4, 0,
                myBasketWidth - 1, myBasketHeight - 1);
        g.drawRect(mySmallDimension + myBasketWidth + 5 * mySquareWidth / 2, 0,
                mySmallDimension - 1, mySmallDimension - 1);
        g.drawRect(0, 0, mySmallDimension - 1, mySmallDimension - 1);

        // Paint each of the squares of the hold grid
        for (int col = 0; col < myGame.getHoldGrid().getNumCols(); ++col)
            for (int row = 0; row < myGame.getHoldGrid().getNumRows(); ++row)
                paintSquare(g, myGame.getHoldGrid().get(col, row), col
                        * (mySquareWidth + GAP) + GAP, (myGame.getHoldGrid()
                        .getNumRows()
                        - row - 1)
                        * (mySquareWidth + GAP) + GAP);

        // Paint each of the squares of the basket grid
        for (int col = 0; col < myGame.getBasketGrid().getNumCols(); ++col)
            for (int row = 0; row < myGame.getBasketGrid().getNumRows() - 2;
                    ++row)
                paintSquare(g, myGame.getBasketGrid().get(col, row),
                        mySmallDimension + 5 * mySquareWidth / 4 + col
                                * (mySquareWidth + GAP) + GAP, (myGame
                                .getBasketGrid().getNumRows()
                                - row - 3)
                                * (mySquareWidth + GAP) + GAP);

        // Paint each of the squares of the next grid
        for (int col = 0; col < myGame.getNextGrid().getNumCols(); ++col)
            for (int row = 0; row < myGame.getNextGrid().getNumRows(); ++row)

                paintSquare(g, myGame.getNextGrid().get(col, row),
                        mySmallDimension + myBasketWidth + 5 * mySquareWidth
                                / 2 + col * (mySquareWidth + GAP) + GAP,
                        (myGame.getNextGrid().getNumRows() - row - 1)
                                * (mySquareWidth + GAP) + GAP);

        // Translate the origin to the top left corner of the statistics
        g.translate(0, mySmallDimension + 7 * (mySquareWidth + GAP) / 4);

        // Set the text color and size
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", 0, (mySquareWidth + GAP) / 2 + 3));

        // Translate the origin to the top left corner of the basic info

        // Paint the score, the number of lines removed, and the level
        g.drawString("Score: " + myGame.getScore(), 0, 0);
        g.drawString("Lines: " + myGame.getLines(), 0,
                3 * (mySquareWidth + GAP) / 2);
        g.drawString("Level: " + myGame.getLevel(), 0,
                3 * (mySquareWidth + GAP));

        // Paint "Paused" or "Game Over" if necessary
        if (myGame.getState() != GameState.ON) {
            g.setColor(Color.RED);

            g.drawString(myGame.getState() == GameState.PAUSED ? "Paused"
                    : "Game Over", 0, 9 * (mySquareWidth + GAP) / 2);
        }
    }

    private void paintSquare(Graphics g, SquareType type, int x, int y) {
        // Paint shadow squares only if the shadow option is selected
        g.setColor(COLORS.get(type != SquareType.SHADOW
                || myGame.isDisplayShadow() ? type : SquareType.EMPTY));
        g.fillRect(x, y, mySquareWidth, mySquareWidth);
    }
}
