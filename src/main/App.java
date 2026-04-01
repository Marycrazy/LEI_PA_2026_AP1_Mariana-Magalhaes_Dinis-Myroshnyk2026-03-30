package main;

import main.states.FirstInitState;
import main.states.State;
import main.utils.Input;
import main.states.LoginState;

public class App {
    public static void main(String[] args) {
        try {
            if(!configureDatabase()) {
                System.err.println("Database configuration cancelled.");
                return;
            }
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

    private static boolean configureDatabase(){
        PropertiesManager props = new PropertiesManager();
        if (props.hasProperties()) {
            System.err.println("Database configuration found.");
            return true;
        }
        System.err.println("Database configuration not found. Please set the properties.");
        String[] requiredProps = {"connect", "username", "password", "namespace", "database"};
        for (String prop : requiredProps) {
            String input = Input.getInput(prop);
            if (input == null) return false;
            props.setProperty(prop, input);
        }
        if(!props.saveFile()) {
            System.err.println("Failed to save database configuration.");
            return false;
        }
        return true;
    }
}