package main;

import javax.swing.SwingUtilities;

import main.states.DBConnectionState;
import main.states.FirstInitState;
import main.states.SignIn;
import main.states.State;
import main.states.SignInUp;

public class App {
    public static void main(String[] args) {
        //https://onyxwizard.medium.com/jvm-shutdown-hooks-in-java-graceful-cleanup-when-things-go-wrong-afa5e5ff8377
        // Após o encerramento da app fecha a conexão com o banco de dados
        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> DatabaseManager.getInstance().close())
        );
        SwingUtilities.invokeLater(() -> {
            try {
                if(!DatabaseManager.getInstance().isConfigured()) {
                    State.start(new DBConnectionState());
                    return;
                }
                DatabaseManager.getInstance().connect();
                System.out.println("Connected to the database!");
                sysInit();
            } catch (Exception e) {
                System.err.println("Operation failed! Network error or invalid database configuration.");
                e.printStackTrace();
            }
        });
    }

    private static void sysInit() {
        if (!DatabaseManager.getInstance().hasAdmin()) State.start(new FirstInitState());
        else new SignIn().setVisible(true);
    }
}