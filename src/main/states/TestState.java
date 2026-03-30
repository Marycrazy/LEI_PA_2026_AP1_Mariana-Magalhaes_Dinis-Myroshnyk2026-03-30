package main.states;

import main.DatabaseManager;
import main.Employee;
import main.Client;
import main.enums.UserSpecialization;
import main.enums.Userscale;
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
        System.out.println("Creating test employee...");

        Employee employee = (Employee) new Employee.Builder()
            .setSpecialization(UserSpecialization.ONE.toString())
            .setStartDate(null)
            .setNif("123456789")
            .setPhone("923456789")
            .setAddress("Test Address")
            .setName("Test Employee")
            .setUsername("test_employee")
            .setPassword("test_employee")
            .setEmail("test_employee@test.com")
            .build();

        DatabaseManager.getInstance().saveUser(employee);

        System.out.println("Employee created!");
        System.out.println("\n[DB CHECK]");

        Employee fetchedEmployee = (Employee) DatabaseManager.getInstance().fetchUser(employee);
        System.out.println("Name: " + fetchedEmployee.getName());
        System.out.println("Username: " + fetchedEmployee.getUsername());
        System.out.println("Password: " + fetchedEmployee.getPassword());
        System.out.println("Email: " + fetchedEmployee.getEmail());
        System.out.println("Type: " + fetchedEmployee.getType());
        System.out.println("Status: " + fetchedEmployee.getStatus());
        System.out.println("Address: " + fetchedEmployee.getAddress());
        System.out.println("NIF: " + fetchedEmployee.getNif());
        System.out.println("Phone: " + fetchedEmployee.getPhone());
        System.out.println("Specialization: " + fetchedEmployee.getSpecialization());
        System.out.println("Start Date: " + fetchedEmployee.getStartDate());

        PressKey.enter();

    }

    private void createTestClient() {
        System.out.println("Creating test client...");

        Client client = (Client) new Client.Builder()
            .setScale(Userscale.A.toString())
            .setSector("Test Sector")
            .setNif("223456789")
            .setPhone("923456789")
            .setAddress("Test Address")
            .setName("Test Client")
            .setUsername("test_client")
            .setPassword("test_client")
            .setEmail("test_client@test.com")
            .build();

        DatabaseManager.getInstance().saveUser(client);

        System.out.println("Client created!");
        System.out.println("\n[DB CHECK]");

        Client fetchedClient = (Client) DatabaseManager.getInstance().fetchUser(client);
        System.out.println("Name: " + fetchedClient.getName());
        System.out.println("Username: " + fetchedClient.getUsername());
        System.out.println("Password: " + fetchedClient.getPassword());
        System.out.println("Email: " + fetchedClient.getEmail());
        System.out.println("Type: " + fetchedClient.getType());
        System.out.println("Status: " + fetchedClient.getStatus());
        System.out.println("Address: " + fetchedClient.getAddress());
        System.out.println("NIF: " + fetchedClient.getNif());
        System.out.println("Phone: " + fetchedClient.getPhone());
        System.out.println("Scale: " + fetchedClient.getScale());
        System.out.println("Sector: " + fetchedClient.getSector());

        PressKey.enter();
    }

    private void listAllUsers() {
        System.out.println("Listing all users...");
        PressKey.enter();
    }
}