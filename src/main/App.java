package main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import main.states.DBConnectionState;
import main.states.FirstInitState;
import main.states.State;
import main.states.SignInState;

/**
 * Entry point of the application.
 * <p>
 * Responsible for initializing the main application window, ensuring the
 * database connection is configured, and routing to the appropriate initial
 * {@link State} depending on whether the system has been set up before.
 */
public class App {
    /**
     * Registers a shutdown hook to close the
     * database connection cleanly on exit, then starts the GUI on the
     * Swing event dispatch thread.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> DatabaseManager.getInstance().close())
        );

        SwingUtilities.invokeLater(App::start);
    }

    /**
     * Creates and displays the main application frame, then determines
     * whether to prompt for database connection details, perform first-time
     * setup, or proceed directly to sign-in, depending on the current
     * configuration and database state.
     */
    private static void start() {
        JFrame frame = new JFrame("Repair Management System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        State.init(frame);

        try {
            if (!DatabaseManager.getInstance().isConfigured()) {
                new DBConnectionState().enter();
                return;
            }
            DatabaseManager.getInstance().connect();
            sysInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Routes to the first-time admin setup screen if no admin user exists
     * yet, or to the sign-in screen otherwise.
     */
    private static void sysInit() {
        if (!DatabaseManager.getInstance().hasAdmin())
            new FirstInitState().enter();
        else new SignInState().enter();
    }
}