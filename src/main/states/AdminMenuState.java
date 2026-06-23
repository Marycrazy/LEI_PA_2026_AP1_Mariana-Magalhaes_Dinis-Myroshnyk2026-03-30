package main.states;

import javax.swing.BorderFactory;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.DatabaseManager;
import main.models.User;

import java.awt.*;

public class AdminMenuState extends JFrame  {
    private static User user;
    private JButton btnUsers;
    private JButton btnRepairs;
    private JButton btnParts;
    private JButton btnNotifications;
    private JButton btnActionLog;
    private JButton btnLogout;

    public AdminMenuState(User user) {
        this.user = user;
        setTitle("Admin Dashboard");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // --- painel do topo ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblUser = new JLabel("User: " + user.getUsername());
        long notifications = DatabaseManager.getInstance().getUnreadNotifications(user);
        JLabel lblNotifications = new JLabel("🔔 Notifications: " + notifications + " pending");

        topPanel.add(lblUser, BorderLayout.WEST);
        topPanel.add(lblNotifications, BorderLayout.EAST);

        // --- painel dos botões ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // botões esticam na horizontal
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.weightx = 1;

        btnUsers         = new JButton("Manage Users");
        btnRepairs       = new JButton("Repairs");
        btnParts         = new JButton("Parts");
        btnNotifications = new JButton("Notifications");
        btnActionLog     = new JButton("Action Log");
        btnLogout        = new JButton("Logout");

        btnUsers.setToolTipText("Manage system users");
        btnRepairs.setToolTipText("View and manage repairs");
        btnParts.setToolTipText("Manage available parts");
        btnNotifications.setToolTipText("View notifications");
        btnActionLog.setToolTipText("View action log");
        btnLogout.setToolTipText("Logout");

        JButton[] buttons = { btnUsers, btnRepairs, btnParts, btnNotifications, btnActionLog, btnLogout };
        for (int i = 0; i < buttons.length; i++) {
            gbc.gridy = i;
            centerPanel.add(buttons[i], gbc);
        }

        // --- juntar à janela ---
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);

        handleInput();
    
    }

    public void handleInput() {
        // Para este botao era:
        // new ManageUsersMenuState().enter(); break;
        btnUsers.addActionListener(e -> JOptionPane.showMessageDialog(this, "Manage Users clicked"));
        //Para este:
        // new ListRepairsState().enter(); break;
        btnRepairs.addActionListener(e -> JOptionPane.showMessageDialog(this, "List Repairs clicked"));
        // Para este:
        // new ManagePartsMenuState().enter(); break;
        btnParts.addActionListener(e -> JOptionPane.showMessageDialog(this, "Parts"));
        // Para este:
        // new ListNotificationState(true).enter(); break;
        btnNotifications.addActionListener(e -> JOptionPane.showMessageDialog(this, "Notifications"));
        btnLogout.addActionListener(e -> {dispose(); new SignIn().setVisible(true);});
        btnActionLog. addActionListener(e -> JOptionPane.showMessageDialog(this, "Action Log"));
    
    }
}