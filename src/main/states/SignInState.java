package main.states;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.DatabaseManager;
import main.DatabaseManager.UserCredentials;
import main.models.Admin;
import main.models.Client;
import main.models.Employee;
import main.utils.FormBuilder;

public class SignInState extends State {
    private JTextField txtUsername = new JTextField(textFieldCols);
    private JPasswordField txtPassword = new JPasswordField(textFieldCols);

    @Override
    public JPanel buildView() {
        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> State.exit());

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> handleInput());

        return new FormBuilder("Login")
            .addField("Username:", txtUsername)
            .addField("Password:", txtPassword)
            .addButtonRow(btnExit, btnLogin)
            .addLinkLabel("Don't have an account? Register", () -> next(new SignUpState()))
            .build();
    }

    private void handleInput() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!DatabaseManager.getInstance().userExists(new UserCredentials(username, password))) {
            JOptionPane.showMessageDialog(null, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isActiveUser(username)) return;

        String type = DatabaseManager.getInstance().getType(username);

        if (type.equals("ADMIN")) {
            Admin admin = (Admin) DatabaseManager.getInstance().fetchUser(username);
            next(new AdminMenuState(admin));
        } else if (type.equals("EMPLOYEE")) {
            Employee employee = (Employee) DatabaseManager.getInstance().fetchUser(username);
            next(new EmployeeMenuState(employee));
        } else if (type.equals("CLIENT")) {
            Client client = (Client) DatabaseManager.getInstance().fetchUser(username);
            next(new ClientMenuState(client));
        }
    }

    private boolean isActiveUser(String username) {
        String status = DatabaseManager.getInstance().getUserStatus(username);

        switch (status) {
            case "PENDING":
                JOptionPane.showMessageDialog(null, "Your account is pending approval.", "Pending", JOptionPane.WARNING_MESSAGE);
                return false;
            case "REJECTED":
                JOptionPane.showMessageDialog(null, "Your account has been rejected.", "Rejected", JOptionPane.ERROR_MESSAGE);
                return false;
            case "INACTIVE":
                JOptionPane.showMessageDialog(null, "Your account has been deactivated.", "Inactive", JOptionPane.ERROR_MESSAGE);
                return false;
            default:
                return true;
        }
    }
}