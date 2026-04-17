package main.states;

import main.utils.Input;
import main.utils.PressKey;

public class NotificationMenuState extends State {
    @Override
    public void render() {
        System.out.println("--- LIST NOTIFICATIONS ---");
        System.out.println("1. List new notifications");
        System.out.println("2. List all notifications");
        System.out.println("0. Back");
        System.out.print("Choice: ");
    }

    @Override
    public void handleInput() {
        String input = Input.getScanner().nextLine();

        switch (input) {
            case "1": new ListNotificationState(false).enter(); break;
            case "2": new ListNotificationState(true).enter(); break;
            case "0": this.back(); break;
            default:
                System.out.println("Invalid option!");
                PressKey.enter();
                break;
        }
    }
}