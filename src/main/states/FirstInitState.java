package main.states;

import main.Admin;
import main.DatabaseManager;
import main.utils.Input;
import main.utils.PressKey;

public class FirstInitState extends State {
    @Override
    public void render() {
        System.out.println("--- INITIAL SYSTEM SETUP ---");
        System.out.println("No active admin user found. Creating one...");
    }

    @Override
    public void handleInput() {
        System.out.print("Name: ");
        String name = Input.getScanner().nextLine();
        System.out.print("Username: ");
        String username = Input.getScanner().nextLine();
        System.out.print("Password: ");
        String password = Input.getScanner().nextLine();
        System.out.print("Email: ");
        String email = Input.getScanner().nextLine();

        Admin admin = (Admin) new Admin.Builder()
            .setName(name)
            .setUsername(username)
            .setPassword(password)
            .setEmail(email)
            .build();

        DatabaseManager.getInstance().saveUser(admin);
        System.out.println("Admin user created!");
        PressKey.enter();
        this.back();
        new TestState().enter();
    }
}