package main.states;

import main.DatabaseManager;
import main.Employee;
import main.Client;
import main.utils.Input;
import main.utils.PressKey;

public class TestState extends State {
    @Override
    public void render() {
        System.out.println("--- TEST STATE ---");
        System.out.println("Admin user found! Printing menu...\n");
        System.out.println("1. Create test employee");
        System.out.println("2. Create test client");
        System.out.println("0. Exit");
        System.out.print("Choice: ");
    }

    @Override
    public void handleInput() {
        String input = Input.getScanner().nextLine();

        switch (input) {
            case "1": createTestEmployee(); break;
            case "2": createTestClient(); break;
            case "3": listAllUsers(); break;
            case "0":
                System.out.println("Exiting...");
                System.exit(0);
            default:
                System.out.println("Invalid option!");
                PressKey.enter();
                break;
        }
    }

    private void createTestEmployee() {
        System.out.println("Creating employee...");

        Employee employee = Employee.create();
        if (employee == null) return;

        DatabaseManager.getInstance().saveUser(employee);
        System.out.println("Employee created!");
        PressKey.enter();
    }

    private void createTestClient() {
        System.out.println("Creating test client...");

        Client client = Client.create();
        if (client == null) return;

        DatabaseManager.getInstance().saveUser(client);
        System.out.println("Client created!");
        PressKey.enter();
    }

    private void listAllUsers() {
        System.out.println("Listing all users...");
        PressKey.enter();
    }
}