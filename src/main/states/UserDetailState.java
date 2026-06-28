package main.states;

import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import main.DatabaseManager;
import main.enums.UserStatus;
import main.models.Client;
import main.models.Employee;
import main.models.RegistrableUser;
import main.models.User;
import main.utils.FormBuilder;
import main.utils.ImageService;

public class UserDetailState extends DetailState<User> {
    private User subject;
    private static final int PHOTO_SIZE = 150;

    public UserDetailState(User subject) {
        this.subject = subject;
    }

    @Override
    protected String getTitle() {
        return "User Details";
    }

    @Override
    protected void onEnter() {
        User fresh = DatabaseManager.getInstance().fetchUser(subject.getUsername());
        if (fresh != null) subject = fresh;
    }

    @Override
    protected void renderFields(FormBuilder form) {
        JLabel photoLabel = new JLabel("Loading...", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(PHOTO_SIZE, PHOTO_SIZE));

        form.addFullWidthRow(photoLabel);
        System.out.println(subject.getImage());
        loadProfilePicture(subject.getImage(), photoLabel);

        form.addField("Name:", readOnly(subject.getName()))
            .addField("Username:", readOnly(subject.getUsername()))
            .addField("Email:", readOnly(subject.getEmail()))
            .addField("Type:", readOnly(subject.getType()))
            .addField("Status:", readOnly(subject.getStatus()));

        if (subject instanceof RegistrableUser reg) {
            form.addField("NIF:", readOnly(reg.getNif()))
                .addField("Phone:", readOnly(reg.getPhone()))
                .addField("Address:", readOnly(reg.getAddress()));
        }

        if (subject instanceof Employee emp) {
            form.addField("Level:", readOnly(String.valueOf(emp.getSpecialization())))
                .addField("Since:", readOnly(emp.getStartDate().toLocalDate().toString()));
        } else if (subject instanceof Client client) {
            form.addField("Sector:", readOnly(String.valueOf(client.getSector())))
                .addField("Tier:", readOnly(String.valueOf(client.getScale())));
        }
    }

    private void loadProfilePicture(String filename, JLabel target) {
        if (filename == null || filename.isBlank()) {
            target.setText("No photo");
            return;
        }

        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                Image raw = ImageService.download(filename);
                return new ImageIcon(scaleToFit(raw, PHOTO_SIZE));
            }

            @Override
            protected void done() {
                try {
                    target.setIcon(get());
                    target.setText(null);
                } catch (Exception e) {
                    System.out.println("Error loading profile picture: " + e.getMessage());
                    target.setText("No photo");
                }
            }
        }.execute();
    }

    private Image scaleToFit(Image image, int maxSize) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);

        double scale = Math.min((double) maxSize / w, (double) maxSize / h);
        int newW = Math.max(1, (int) (w * scale));
        int newH = Math.max(1, (int) (h * scale));

        return image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
    }

    private JTextField readOnly(String value) {
        JTextField field = new JTextField(value, textFieldCols);
        field.setEditable(false);
        return field;
    }

    @Override
    protected List<JButton> getActions() {
        List<JButton> actions = new ArrayList<>();

        switch (subject.getStatus()) {
            case "PENDING":
                actions.add(statusButton("Approve", "approve this user", UserStatus.ACTIVE));
                actions.add(statusButton("Reject", "reject this user", UserStatus.REJECTED));
                break;
            case "ACTIVE":
                actions.add(statusButton("Deactivate", "deactivate this user", UserStatus.INACTIVE));
                break;
            case "INACTIVE":
                actions.add(statusButton("Activate", "activate this user", UserStatus.ACTIVE));
                break;
        }

        JButton btnEdit = new JButton("Edit Profile");
        btnEdit.addActionListener(e ->
            next(new UserEditState(subject))
        );
        actions.add(btnEdit);

        return actions;
    }

    private JButton statusButton(String label, String actionDescription, UserStatus newStatus) {
        JButton button = new JButton(label);
        button.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to " + actionDescription + "?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                DatabaseManager.getInstance().setUserStatus(subject, newStatus.toString());
                back();
            }
        });
        return button;
    }
}