package main.states;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.DatabaseManager;
import main.models.Notification;

public class NotificationListState extends ListState<Notification> {
    private boolean listAll = true;
    private static final int TRUNCATE = 60;
    private static final String[] FILTER_OPTIONS = {"All notifications", "Unread only"};

    @Override
    protected String getTitle() {
        return "Notifications";
    }

    @Override
    protected List<Notification> fetchItems() {
        return DatabaseManager.getInstance().getNotifications(user, listAll);
    }

    @Override
    protected String[] getColumns() {
        return new String[]{"Date", "Content"};
    }

    @Override
    protected Object[] getRowValues(Notification notification) {
        String content = notification.getContent();
        if (content.length() > TRUNCATE) content = content.substring(0, TRUNCATE - 3) + "...";
        return new Object[]{notification.getCreatedAt().toLocalDate(), content};
    }

    @Override
    protected void onSelect(Notification notification) {
        next(new NotificationDetailState(notification));
    }

    @Override
    protected void renderExtras(JPanel extrasPanel) {
        JComboBox<String> filter = new JComboBox<>(FILTER_OPTIONS);
        filter.setSelectedIndex(listAll ? 0 : 1);

        filter.addActionListener(e -> {
            listAll = filter.getSelectedIndex() == 0;
            refresh();
        });

        extrasPanel.add(new JLabel("Show:"));
        extrasPanel.add(filter);
    }
}