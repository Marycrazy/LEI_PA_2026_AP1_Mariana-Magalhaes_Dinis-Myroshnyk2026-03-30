package main.states;

import main.DatabaseManager;
import main.models.Admin;
import main.utils.PressKey;

public class FirstInitState extends State {
    @Override
    public void render() {
        System.out.println("--- INITIAL SYSTEM SETUP ---");
        System.out.println("No active admin user found. Creating one...");
    }

    @Override
    public void handleInput() {
        Admin admin = Admin.create();
        if (admin == null) System.exit(0);

        DatabaseManager.getInstance().saveUser(admin);
        State.user = admin;
        System.out.println("Admin user created!");
        PressKey.enter();

        new AdminMenuState().enter();
    }
}