package main.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import main.DatabaseManager;
import main.models.User;

public class MenuBuilder {
    private final JPanel root = new JPanel(new BorderLayout());
    private final JPanel centerPanel = new JPanel(new GridBagLayout());
    private final GridBagConstraints gbc = new GridBagConstraints();
    private int row = 0;

    public MenuBuilder(User user) {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        long notifications = DatabaseManager.getInstance().getUnreadNotifications(user);

        JLabel lblUser = new JLabel("User: " + user.getUsername());
        JLabel lblNotifications = new JLabel("Notifications: " + notifications + " pending");
        if (notifications > 0) {
            lblNotifications.setForeground(Color.RED);
            lblNotifications.setFont(lblNotifications.getFont().deriveFont(Font.BOLD));
        }

        topPanel.add(lblUser, BorderLayout.WEST);
        topPanel.add(lblNotifications, BorderLayout.EAST);

        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.weightx = 1;

        root.add(topPanel, BorderLayout.NORTH);
        root.add(centerPanel, BorderLayout.CENTER);

        if (notifications > 0) {
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null,
                    "You have " + notifications + " unread notification(s).",
                    "Notifications", JOptionPane.INFORMATION_MESSAGE)
            );
        }
    }

    public MenuBuilder addButton(String label, String tooltip, Runnable onClick) {
        JButton button = new JButton(label);
        button.setToolTipText(tooltip);
        button.addActionListener(e -> onClick.run());

        gbc.gridy = row++;
        centerPanel.add(button, gbc);
        return this;
    }

    public JPanel build() {
        return root;
    }
}