import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ui.MainFrame;

public class Main {

    // Starts the Swing application on the event dispatch thread.
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Could not load the system look and feel.");
        }

        SwingUtilities.invokeLater(new Runnable() {
            // Creates and shows the main window.
            @Override
            public void run() {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            }
        });
    }
}
