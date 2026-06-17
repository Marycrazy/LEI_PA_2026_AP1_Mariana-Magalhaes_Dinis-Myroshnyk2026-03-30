package main.states;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;

import main.DatabaseManager;
import main.DatabaseManager.UserCredentials;
// import main.models.Admin;
// import main.models.Client;
// import main.models.Employee;

public class SignIn extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    

    public SignIn(){
        setTitle("Sign In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 250);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Login",
                TitledBorder.LEFT,
                TitledBorder.TOP
            )
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

      // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Username"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtUsername = new JTextField(15);
        panel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Password"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtPassword = new JPasswordField(15);
        panel.add(txtPassword, gbc);

        // Painel dos botões
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> System.exit(0));

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> handleInput());

        buttons.add(btnExit);
        buttons.add(btnLogin);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttons, gbc);

        add(panel, BorderLayout.CENTER);

        setVisible(true);
    }


    public void handleInput() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (DatabaseManager.getInstance().userExists(new UserCredentials(username, password))) {
            if (!isActiveUser(username)) return;

            String type = DatabaseManager.getInstance().getType(username);

            if (type.equals("ADMIN")) {
                // Admin admin = (Admin) DatabaseManager.getInstance().fetchUser(username);
                // // abre o menu do admin
                // // new AdminMenuFrame(admin).setVisible(true);
                dispose();
            } else if (type.equals("EMPLOYEE")) {
                // Employee employee = (Employee) DatabaseManager.getInstance().fetchUser(username);
                // new EmployeeMenuFrame(employee).setVisible(true);
                dispose();
            } else if (type.equals("CLIENT")) {
                // Client client = (Client) DatabaseManager.getInstance().fetchUser(username);
                // new ClientMenuFrame(client).setVisible(true);
                dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isActiveUser(String username) {
        String status = DatabaseManager.getInstance().getUserStatus(username);

        switch (status) {
            case "PENDING":
                JOptionPane.showMessageDialog(this, "Your account is pending approval.", "Pending", JOptionPane.WARNING_MESSAGE);
                return false;
            case "REJECTED":
                JOptionPane.showMessageDialog(this, "Your account has been rejected.", "Rejected", JOptionPane.ERROR_MESSAGE);
                return false;
            case "INACTIVE":
                JOptionPane.showMessageDialog(this, "Your account has been deactivated.", "Inactive", JOptionPane.ERROR_MESSAGE);
                return false;
            default:
                return true;
        }
    }
}