package main;

import main.states.DBConnectionState;
import main.states.FirstInitState;
import main.states.State;
import main.states.SignInUp;

public class App {
    public static void main(String[] args) {
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
        finally {
            DatabaseManager.getInstance().close();
            State.exit();
        }
    }

    private static void sysInit() {
        if (!DatabaseManager.getInstance().hasAdmin()) State.start(new FirstInitState());
        else State.start(new SignInUp());
    }
}