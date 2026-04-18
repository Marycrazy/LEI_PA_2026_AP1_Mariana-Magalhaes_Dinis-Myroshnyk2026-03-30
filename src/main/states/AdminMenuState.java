package main.states;

import main.DatabaseManager;
import main.utils.Input;
import main.utils.PressKey;

public class AdminMenuState extends State {
    @Override
    public void render() {
        long notifications = DatabaseManager.getInstance().getUnreadNotifications(user);

        System.out.println("--- ADMIN DASHBOARD [user: " + user.getUsername() + "] ---");
        System.out.println("Notifications [" + notifications + " pending]\n");
        System.out.println("1. Manage users");
        System.out.println("2. Manage repairs [TODO]");
        System.out.println("3. Manage parts [TODO]");
        System.out.println("4. Notifications");
        System.out.println("5. Action log [TODO]");
        System.out.println("0. Back");
        System.out.print("Choice: ");
    }

    @Override
    public void handleInput() {
        String input = Input.getScanner().nextLine();

        switch (input) {
            case "1": new ManageUsersMenuState().enter(); break;
            case "2": break;
            case "3": break;
            case "4": new NotificationMenuState().enter(); break;
            case "5": break;
            case "0": this.back(); this.back(); break;
            default:
                System.out.println("Invalid option!");
                PressKey.enter();
                break;
        }
    }
}