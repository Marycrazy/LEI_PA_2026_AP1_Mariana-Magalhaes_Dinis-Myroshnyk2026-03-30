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
import main.states.State;

/**
 * UI pattern helper for constructing standardized modular application menu panels with a center grid panel
 * and an optional persistent header for notifications and profile information.
 */
public class MenuBuilder {
    private final JPanel root = new JPanel(new BorderLayout());
    private final JPanel centerPanel = new JPanel(new GridBagLayout());
    private final GridBagConstraints gbc = new GridBagConstraints();
    private int row = 0;

    /**
     * Prepares a standard menu panel assembly with center item anchoring rules.
     */
    public MenuBuilder() {
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.weightx = 1;

        root.add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Instantiates an active custom user menu workspace panel including an active top toolbar
     * detailing pending system notification counters.
     *
     * @param user the currently authenticated active session context account
     */
    public MenuBuilder(User user) {
        this();

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

        root.add(topPanel, BorderLayout.NORTH);

        if (notifications > 0 && !State.hasShownNotificationPopup()) {
            State.markNotificationPopupShown();
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null,
                    "You have " + notifications + " unread notification(s).",
                    "Notifications", JOptionPane.INFORMATION_MESSAGE)
            );
        }
    }

    /**
     * Appends an interactive navigation or operation push button element onto the center grid block stack.
     *
     * @param label   the title string printed on the button layout face
     * @param tooltip contextual hover tip helper documentation text
     * @param onClick an event callback routine method executed upon triggering the button action
     * @return this menu builder instance for chaining calls
     */
    public MenuBuilder addButton(String label, String tooltip, Runnable onClick) {
        JButton button = new JButton(label);
        button.setToolTipText(tooltip);
        button.addActionListener(e -> onClick.run());

        gbc.gridy = row++;
        centerPanel.add(button, gbc);
        return this;
    }

    /**
     * Compiles and outputs the configured composite panel view hierarchy containing the complete window layout tree structures.
     *
     * @return a single managed layout container {@link JPanel}
     */
    public JPanel build() {
        return root;
    }
}