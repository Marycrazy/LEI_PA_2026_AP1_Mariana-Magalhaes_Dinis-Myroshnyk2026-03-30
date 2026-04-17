package main.states;

import java.util.List;

import main.DatabaseManager;
import main.models.Notification;
import main.utils.Input;
import main.utils.PressKey;

public class ListNotificationState extends State {
    private boolean listAll;
    private int currPage = 0;
    private final int pageSize = 5;
    private final int truncateSize = 20;

    public ListNotificationState(boolean listAll) {
        this.listAll = listAll;
    }

    @Override
    public void render() {
        List<Notification> notifications = DatabaseManager.getInstance().getNotifications(user, listAll);
        int total = notifications.size();
        int pages = (int) Math.ceil((double) total / pageSize);

        System.out.println("--- " + (listAll ? "ALL" : "NEW") + " NOTIFICATIONS ---");

        if (notifications.isEmpty()) System.out.println("No notifications found.");
        else {
            int start = currPage * pageSize;
            int end = Math.min(start + pageSize, total);

            for (int i = start; i < end; i++) {
                Notification notification = notifications.get(i);
                String content = notification.getContent();

                if (content.length() > truncateSize) {
                    content = content.substring(0, truncateSize - 3) + "...";
                }

                System.out.println("[" + (i + 1) + "] " + notification.getCreatedAt().toLocalDate() + " | " + content);
            }

            System.out.println("Page " + (currPage + 1) + "/" + pages + "\n");
        }

        System.out.println("H. Previous page | J. Next page | 0. Back");
        System.out.print("Choice: ");
    }

    @Override
    public void handleInput() {
        String input = Input.getScanner().nextLine().toUpperCase();

        List<Notification> notifications = DatabaseManager.getInstance().getNotifications(user, listAll);
        int total = notifications.size();
        int pages = (int) Math.ceil((double) total / pageSize);

        switch (input) {
            case "H": if (currPage > 0) currPage--; break;
            case "J": if (currPage < pages - 1) currPage++; break;
            case "0": this.back(); break;
            default:
                handleNumberInput(input, notifications);
                break;
        }
    }

    private void handleNumberInput(String input, List<Notification> notifications) {
        try {
            int index = Integer.parseInt(input) - 1;
            int start = currPage * pageSize;
            int end = Math.min(start + pageSize, notifications.size());

            if (index >= start && index < end) {
                Notification selected = notifications.get(index);
                new NotificationDetailState(selected).enter();
            } else {
                System.out.println("Selected index out of range!");
                PressKey.enter();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid option!");
            PressKey.enter();
        }
    }
}