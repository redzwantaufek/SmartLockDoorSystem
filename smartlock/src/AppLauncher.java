import gui.DoorLockFrame;
import controller.DoorLockController;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main Application Launcher for FSM Door Lock (Milestone 4).
 * Boots up the Swing GUI skeleton and attaches the defensive Controller logic.
 */
public class AppLauncher {

    public static void main(String[] args) {
        // Set System Look and Feel for cleaner native appearance (Constraint 4 compliant)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to cross-platform Look and Feel silently
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DoorLockFrame frame = new DoorLockFrame();
                new DoorLockController(frame);
                frame.setVisible(true);
            }
        });
    }
}
