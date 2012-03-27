/**
 * @author Nikita Kouevda
 * @date 2012/03/27
 */

package tetris.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TetrisFrame extends JFrame {
    // -----------------------------------------------------------------
    // Fields
    // -----------------------------------------------------------------
    
    private TetrisPanel myPanel;
    
    // -----------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------
    
    public TetrisFrame() {
        super("Tetris");
        
        // Construct the panel and configure it properly
        myPanel = new TetrisPanel();
        myPanel.addKeyListener(new TetrisKeyListener(myPanel, myPanel
                .getGame(), false));
        myPanel.setFocusable(true);
        
        // Set the menu bar and override the content pane with the panel
        setJMenuBar(new TetrisMenuBar(myPanel));
        setContentPane(myPanel);
        
        // Set basic properties and center the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    // -----------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------
    
    public static void main(String[] args) {
        // Use system-specific UI if possible
        try {
            UIManager.setLookAndFeel(UIManager
                    .getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Ignore exception
        }
        
        // Launch via swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TetrisFrame();
            }
        });
    }
}
