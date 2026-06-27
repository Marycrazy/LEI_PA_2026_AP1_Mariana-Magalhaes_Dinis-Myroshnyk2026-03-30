package main.states;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import main.DatabaseManager;
import main.models.Notification;
import main.utils.FormBuilder;

public class NotificationDetailState extends DetailState<Notification> {
    private final Notification notification;

    public NotificationDetailState(Notification notification) {
        this.notification = notification;
    }

    @Override
    protected String getTitle() {
        return "Notification Details";
    }

    @Override
    protected void onEnter() {
        DatabaseManager.getInstance().markAsRead(user, notification);
    }

    @Override
    protected void renderFields(FormBuilder form) {
        JTextField txtId = new JTextField(notification.getId().toString(), textFieldCols);
        txtId.setEditable(false);

        JTextField txtCreatedAt = new JTextField(notification.getCreatedAt().toString(), textFieldCols);
        txtCreatedAt.setEditable(false);

        JTextField txtTarget = new JTextField(String.valueOf(notification.getTarget()), textFieldCols);
        txtTarget.setEditable(false);

        JTextArea txtContent = new JTextArea(notification.getContent(), 4, textFieldCols);
        txtContent.setEditable(false);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);

        form.addField("ID:", txtId)
            .addField("Created at:", txtCreatedAt)
            .addField("Target:", txtTarget)
            .addField("Content:", new javax.swing.JScrollPane(txtContent));
    }

    @Override
    protected List<JButton> getActions() {
        return List.of();
    }
}