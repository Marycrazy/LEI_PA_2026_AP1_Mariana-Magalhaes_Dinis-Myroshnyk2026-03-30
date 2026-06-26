package main.states;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.DatabaseManager;
import main.enums.UserStatus;
import main.models.Admin;
import main.utils.FormBuilder;
import main.utils.FormValidator;

public class FirstInitState extends State {
    private JTextField txtName = new JTextField(15);
    private JTextField txtUsername = new JTextField(15);
    private JPasswordField txtPassword = new JPasswordField(15);
    private JTextField txtEmail = new JTextField(15);

    @Override
    public JPanel buildView() {
        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> State.exit());

        JButton btnSubmit = new JButton("Create admin");
        btnSubmit.addActionListener(e -> submit());

        return new FormBuilder("Initial Setup - Create Admin")
            .addFullWidthRow(new JLabel("No active admin found. Create one to continue:"))
            .addField("Name:", txtName)
            .addField("Username:", txtUsername)
            .addField("Password:", txtPassword)
            .addField("Email:", txtEmail)
            .addButtonRow(btnExit, btnSubmit)
            .build();
    }

    private void submit() {
        FormValidator validator = new FormValidator()
            .require("Name", txtName)
            .require("Username", txtUsername)
            .require("Password", txtPassword)
            .require("Email", txtEmail);

        if (!validator.isValid()) {
            JOptionPane.showMessageDialog(null, validator.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Admin admin = new Admin.Builder()
            .setName(txtName.getText())
            .setUsername(txtUsername.getText())
            .setPassword(new String(txtPassword.getPassword()))
            .setEmail(txtEmail.getText())
            .setImage("")
            .setStatus(UserStatus.ACTIVE.toString())
            .build();

        try {
            DatabaseManager.getInstance().saveUser(admin);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Could not create admin: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        State.user = admin;
        JOptionPane.showMessageDialog(null, "Admin user created!", "Success", JOptionPane.INFORMATION_MESSAGE);
        // next(new AdminMenuState(admin)); // TODO: uncomment when AdminMenuState is ready
    }
}