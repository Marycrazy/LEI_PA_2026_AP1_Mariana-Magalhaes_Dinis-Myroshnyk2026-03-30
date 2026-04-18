package main.states;

import main.DatabaseManager;
import main.enums.UserStatus;
import main.models.Admin;
import main.models.Client;
import main.models.Employee;
import main.models.User;
import main.utils.Input;
import main.utils.PressKey;

public class AdminCreateUserState extends State {
    @Override
    public void render() {
        System.out.println("--- CREATE USER ---");
        System.out.println("1. Admin");
        System.out.println("2. Employee");
        System.out.println("3. Client");
        System.out.println("0. Back");
    }

    @Override
    public void handleInput() {
        String input = Input.getScanner().nextLine();

        switch (input) {
            case "1": createUser("admin"); break;
            case "2": createUser("employee"); break;
            case "3": createUser("client"); break;
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

        if (type.equals("admin")) user = Admin.create();
        else if (type.equals("employee")) user = Employee.create();
        else if (type.equals("client")) user = Client.create();
        if (user == null) return;
        else user.setStatus(UserStatus.ACTIVE.toString());

        DatabaseManager.getInstance().saveUser(user);
        System.out.println(type.substring(0, 1).toUpperCase() + type.substring(1) + " created!");

        PressKey.enter();

        this.back();
    }
}