package tetris.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TetrisFrame extends JFrame {
    private TetrisPanel panel;

    public TetrisFrame() {
        super("Tetris");

        panel = new TetrisPanel();
        panel.addKeyListener(new TetrisKeyListener(panel, panel.getGame(),
            false));
        panel.setFocusable(true);

        setJMenuBar(new TetrisMenuBar(panel));
        setContentPane(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String... args) {
        try {
            // Use system-specific UI if possible
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Proceed without system-specific UI
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TetrisFrame();
            }
        });
    }
}
