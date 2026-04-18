package main.states;

import main.DatabaseManager;
import main.PropertiesManager;
import main.utils.Input;
import main.utils.PressKey;

public class DBConnectionState extends State {
    private PropertiesManager props;

    @Override
    public void render() {
        System.out.println("--- DATABASE ACCESS CONGURATION ---");
        System.out.println("No properties file found. Please enter the following database connection details:");
    }

    @Override
    public void handleInput() {
        String connect = Input.getInput("Connection URL");
        if (connect == null) System.exit(0);

        String namespace = Input.getInput("Namespace");
        if (namespace == null) System.exit(0);

        String database = Input.getInput("Database");
        if (database == null) System.exit(0);

        String username = Input.getInput("Username");
        if (username == null) System.exit(0);

        String password = Input.getInput("Password");
        if (password == null) System.exit(0);

        props = new PropertiesManager();
        props.setProperty("connect", connect);
        props.setProperty("namespace", namespace);
        props.setProperty("database", database);
        props.setProperty("username", username);
        props.setProperty("password", password);

        if (props.saveFile()) {
            System.out.println("Database configuration saved successfully.");
            PressKey.enter();
        } else {
            System.out.println("Error saving database configuration.");
            PressKey.enter();
        }

        DatabaseManager.getInstance().connect();

        new SignInUp().enter();
    }
}