package main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

// import main.states.DBConnectionState;
// import main.states.FirstInitState;
import main.states.State;
import main.states.SignInState;

public class App {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> DatabaseManager.getInstance().close())
        );

        SwingUtilities.invokeLater(App::start);
    }

    private static void start() {
        JFrame frame = new JFrame("Repair Management System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        State.init(frame);

        try {
            if (!DatabaseManager.getInstance().isConfigured()) {
                // new DBConnectionState().enter(); // TODO: uncomment
                return;
            }
            DatabaseManager.getInstance().connect();
            sysInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sysInit() {
        if (!DatabaseManager.getInstance().hasAdmin())
            // new FirstInitState().enter(); // TODO: uncomment
            return; //TODO: remove this line when uncommenting the above
        else new SignInState().enter();
    }
}