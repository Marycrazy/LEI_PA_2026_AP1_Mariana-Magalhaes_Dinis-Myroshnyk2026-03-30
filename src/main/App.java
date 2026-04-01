package main;

import main.states.FirstInitState;
import main.states.State;
import main.states.LoginState;

public class App {
    public static void main(String[] args) {
        try {
            DatabaseManager.getInstance().connect();
            System.out.println("Connected to the database!");
            sysInit();
        } catch (Exception e) {
            System.err.println("Operation failed!");
            e.printStackTrace();
        } finally {
            DatabaseManager.getInstance().close();
            State.exit();
        }
    }

    private static void sysInit() {
        if (!DatabaseManager.getInstance().hasAdmin()) State.start(new FirstInitState());
        else State.start(new LoginState());
    }
}