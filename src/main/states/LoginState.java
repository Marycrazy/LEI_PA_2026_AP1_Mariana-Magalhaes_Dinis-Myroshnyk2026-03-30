package main.states;

import main.Admin;
import main.Client;
import main.DatabaseManager;
import main.Employee;
import main.utils.Input;
import main.utils.PressKey;

public class LoginState extends State {
    @Override
    public void render() {
        System.out.println("--- LOGIN ---");
    }

    @Override
    public void handleInput() {
        String username, password;

        username = Input.getInput("Username");
        if (username == null) System.exit(0);

        password = Input.getInput("Password");
        if (password == null) System.exit(0);

        if (DatabaseManager.getInstance().userExists(username, password)) {
            String type = DatabaseManager.getInstance().getType(username);
            if (type.equals("ADMIN")) {
                Admin admin = (Admin) DatabaseManager.getInstance().fetchUser(username);
                System.out.println("Welcome " + admin.getName() + "!");
                PressKey.enter();
                // this.back();
                // new AdminMenuState().enter();
            }
            else if (type.equals("EMPLOYEE")) {
                Employee employee = (Employee) DatabaseManager.getInstance().fetchUser(username);
                System.out.println("Welcome " + employee.getName() + "!");
                PressKey.enter();
                // this.back();
                // new EmployeeMenuState().enter();
            }
            else if (type.equals("CLIENT")) {
                Client client = (Client) DatabaseManager.getInstance().fetchUser(username);
                System.out.println("Welcome " + client.getName() + "!");
                PressKey.enter();
                // this.back();
                // new ClientMenuState().enter();
            }
        }

        System.out.println("Invalid username or password!");
        PressKey.enter();
    }
}