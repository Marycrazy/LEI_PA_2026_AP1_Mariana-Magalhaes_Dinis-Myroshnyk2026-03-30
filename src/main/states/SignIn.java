package main.states;

import main.DatabaseManager;
import main.DatabaseManager.UserCredentials;
import main.models.Admin;
import main.models.Client;
import main.models.Employee;
import main.utils.Input;
import main.utils.PressKey;

public class SignIn extends State {
    @Override
    public void render() {
        System.out.println("--- SIGN IN ---");
    }

    @Override
    public void handleInput() {
        String username, password;

        username = Input.getInput("Username");
        if (username == null) {
            this.back();
            return;
        }

        password = Input.getInput("Password");
        if (password == null) {
            this.back();
            return;
        }

        if (DatabaseManager.getInstance().userExists(new UserCredentials(username, password))) {
            if (!isActiveUser(username)) return;

            String type = DatabaseManager.getInstance().getType(username);
            if (type.equals("ADMIN")) {
                Admin admin = (Admin) DatabaseManager.getInstance().fetchUser(username);
                State.user = admin;
                System.out.println("Welcome " + admin.getName() + "!");
                PressKey.enter();
                new AdminMenuState().enter();
            }
            else if (type.equals("EMPLOYEE")) {
                Employee employee = (Employee) DatabaseManager.getInstance().fetchUser(username);
                State.user = employee;
                System.out.println("Welcome " + employee.getName() + "!");
                PressKey.enter();
                // new EmployeeMenuState().enter();
            }
            else if (type.equals("CLIENT")) {
                Client client = (Client) DatabaseManager.getInstance().fetchUser(username);
                State.user = client;
                System.out.println("Welcome " + client.getName() + "!");
                PressKey.enter();
                // new ClientMenuState().enter();
            }
        } else {
            System.out.println("Invalid username or password!");
            PressKey.enter();
        }
    }

    private boolean isActiveUser(String username) {
        String status = DatabaseManager.getInstance().getUserStatus(username);

        if (status.equals("PENDING")) {
            System.out.println("Your account is pending approval. Please wait for an admin to approve it.");
            PressKey.enter();
            return false;
        } else if (status.equals("REJECTED")) {
            System.out.println("Your account has been rejected. Please contact an admin for more information.");
            PressKey.enter();
            return false;
        } else if (status.equals("INACTIVE")) {
            System.out.println("Your account has been deactivated. Please contact an admin for more information.");
            PressKey.enter();
            return false;
        } else return true;
    }
}