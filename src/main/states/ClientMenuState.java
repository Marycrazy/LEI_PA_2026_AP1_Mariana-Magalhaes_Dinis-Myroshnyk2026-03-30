package main.states;

import main.DatabaseManager;
import main.utils.Input;
import main.utils.PressKey;

public class ClientMenuState extends State {
    @Override
    public void render() {
        long notifications = DatabaseManager.getInstance().getUnreadNotifications(user);

        System.out.println("--- CLIENT DASHBOARD [user: " + user.getUsername() + "] ---");
        System.out.println("Notifications [" + notifications + " pending]\n");
        System.out.println("1. My equipment");
        System.out.println("2. My repairs [TODO]");
        System.out.println("3. Notifications");
        System.out.println("4. Change profile");
        System.out.println("0. Back");
        System.out.print("Choice: ");
    }

    @Override
    public void handleInput() {
        String input = Input.getScanner().nextLine();

        switch (input) {
            case "1": next(new EquipmentMenuState()); break;
            case "2": break;
            case "3": new NotificationMenuState().enter(); break;
            case "4": new EditUserState(user); break;
            case "0": this.back(); this.back(); break;
            default:
                System.out.println("Invalid option!");
                PressKey.enter();
                break;
        }
    }
}