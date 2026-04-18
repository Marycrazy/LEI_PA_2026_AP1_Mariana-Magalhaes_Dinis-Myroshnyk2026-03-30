package main.states;

import java.util.Collections;
import java.util.Map;
import main.DatabaseManager;
import main.models.Notification;
import main.utils.Text;

public class NotificationDetailState extends DetailState<Notification> {
    private final Notification notification;
    private static final int MAX_WIDTH = 50;

    public NotificationDetailState(Notification notification) {
        this.notification = notification;
    }

    @Override
    protected String getTitle() { return "NOTIFICATION DETAILS"; }

    @Override
    protected void onEnter() {
        DatabaseManager.getInstance().markAsRead(user, notification);
    }

    @Override
    protected void renderFields() {
        System.out.println("ID:         " + notification.getId());
        System.out.println("Created at: " + notification.getCreatedAt());
        System.out.println("Target:     " + notification.getTarget());
        System.out.println("\nContent:");
        System.out.println("\"" + Text.wrap(notification.getContent(), MAX_WIDTH) + "\"");
    }

    @Override
    protected Map<String, String> getActions() {
        return Collections.emptyMap();
    }

    @Override
    protected void handleAction(String key) {}
}