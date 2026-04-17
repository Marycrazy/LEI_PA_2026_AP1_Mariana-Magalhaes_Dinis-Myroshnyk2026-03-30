package main.states;

import main.DatabaseManager;
import main.utils.Input;
import main.utils.PressKey;

public class AdminMenuState extends State {
    @Override
    public void render() {
        long notifications = DatabaseManager.getInstance().getUnreadNotifications(user);

        System.out.println("--- ADMIN MENU [" + user.getUsername() + "] ---");
        System.out.println("1. Notifications [" + notifications + " new]");
        System.out.println("2. Manage user requests");
        System.out.println("0. Back");
        System.out.print("Choice: ");
    }

    @Override
    public void handleInput() {
        String input = Input.getScanner().nextLine();

        switch (input) {
            case "1": new NotificationMenuState().enter(); break;
            case "2": break;
            case "0": this.back(); this.back(); break;
            default:
                System.out.println("Invalid option!");
                PressKey.enter();
                break;
        }
    }
}