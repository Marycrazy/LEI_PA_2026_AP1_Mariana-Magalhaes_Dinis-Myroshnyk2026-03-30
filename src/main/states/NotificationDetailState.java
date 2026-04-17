package main.states;

import main.DatabaseManager;
import main.models.Notification;
import main.utils.Input;
import main.utils.PressKey;
import main.utils.Text;

public class NotificationDetailState extends State {
    private Notification notification;
    private final int max_message_width = 50;

    public NotificationDetailState(Notification notification) {
        this.notification = notification;
    }

    @Override
    public void render() {
        DatabaseManager.getInstance().markAsRead(user, notification);

        System.out.println("--- NOTIFICATION DETAILS ---");
        System.out.println("ID: " + notification.getId() + "\n");
        System.out.println("Content:");
        System.out.println("\"" + Text.wrap(notification.getContent(), max_message_width) + "\" \n");
        System.out.println("Target: " + notification.getTarget() + "\n");
        System.out.println("Created at: " + notification.getCreatedAt() + "\n");
        System.out.println("0. Back");
        System.out.print("Choice: ");
    }

    @Override
    public void handleInput() {
        String input = Input.getScanner().nextLine();

        switch (input) {
            case "0": this.back(); break;
            default:
                System.out.println("Invalid option!");
                PressKey.enter();
                break;
        }
    }
}