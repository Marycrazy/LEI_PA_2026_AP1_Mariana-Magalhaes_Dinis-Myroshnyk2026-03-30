package main.states;

import main.DatabaseManager;
import main.enums.UserType;
import main.models.Client;
import main.models.Employee;
import main.models.User;
import main.utils.Input;
import main.utils.PressKey;

public class SignUp extends State {
    @Override
    public void render() {
        System.out.println("--- SIGN UP ---");
        System.out.println("1. Create employee");
        System.out.println("2. Create client");
        System.out.println("0. Back");
        System.out.print("Choice: ");
    }

    @Override
    public void handleInput() {
        String input = Input.getScanner().nextLine();

        switch (input) {
            case "1": createUser("employee"); break;
            case "2": createUser("client"); break;
            case "0": this.back(); break;
            default:
                System.out.println("Invalid option!");
                PressKey.enter();
                break;
        }
    }

    private void createUser(String type) {
        User user = null;

        System.out.println("Creating " + type + "...");

        if (type.equals("employee")) user = Employee.create();
        else if (type.equals("client")) user = Client.create();
        if (user == null) return;

        DatabaseManager.getInstance().saveUser(user);
        DatabaseManager.getInstance().sendNotification("User '" + user.getUsername() + "' awaiting approval", UserType.ADMIN.toString());
        System.out.println(type.substring(0, 1).toUpperCase() + type.substring(1) + " created!");
        System.out.println("Please wait while an admin reviews your request...");
        PressKey.enter();

        this.back();
    }
}