package main.states;

import java.util.List;
import main.DatabaseManager;
import main.models.Notification;

public class ListNotificationState extends ListState<Notification> {
    private final boolean listAll;
    private static final int TRUNCATE = 20;

    public ListNotificationState(boolean listAll) {
        super(5);
        this.listAll = listAll;
    }

    @Override
    protected String getTitle() {
        return listAll ? "ALL NOTIFICATIONS" : "NEW NOTIFICATIONS";
    }

    @Override
    protected List<Notification> fetchItems() {
        return DatabaseManager.getInstance().getNotifications(user, listAll);
    }

    @Override
    protected String getRowLabel(Notification notification, int index) {
        String content = notification.getContent();
        if (content.length() > TRUNCATE) content = content.substring(0, TRUNCATE - 3) + "...";
        return notification.getCreatedAt().toLocalDate() + " | " + content;
    }

    @Override
    protected void onSelect(Notification notification) {
        next(new NotificationDetailState(notification));
    }
}