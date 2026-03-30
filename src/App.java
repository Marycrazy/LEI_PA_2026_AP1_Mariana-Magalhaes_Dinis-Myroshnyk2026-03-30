public class App {
    public static void main(String[] args) {
        try {
            DatabaseManager.getInstance().connect();
            System.out.println("Connected to the database!");
            initSystem();
            showMenu();
        } catch (Exception e) {
            System.err.println("Operation failed!");
            e.printStackTrace();
        } finally {
            DatabaseManager.getInstance().close();
        }
    }

    private static void initSystem() {
        if (!DatabaseManager.getInstance().hasAdmin()) {
            System.out.println("No admin user found. Creating one...");
            System.out.println("Name: ");
            String name = Input.getScanner().nextLine();
            System.out.println("Email: ");
            String email = Input.getScanner().nextLine();
            System.out.println("Username: ");
            String username = Input.getScanner().nextLine();
            System.out.println("Password: ");
            String password = Input.getScanner().nextLine();

            Admin admin = new Admin();
            admin.setName(name);
            admin.setEmail(email);
            admin.setUsername(username);
            admin.setPassword(password);
            admin.setType(UserType.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);

            DatabaseManager.getInstance().createUser(admin);
            System.out.println("Admin user created!");
        }
    }

    private static void showMenu() {
        System.out.println("1. Create employee");
        System.out.println("2. Create client");
        System.out.println("3. Exit");
        System.out.println("Enter your choice: ");
        String choice = Input.getScanner().nextLine();
        switch (choice) {
            case "1":
                System.out.println("Creating employee...");
                break;
            case "2":
                System.out.println("Creating client...");
                break;
            case "3":
                System.out.println("Exiting...");
                System.exit(0);
            default:
                System.out.println("Invalid choice!");
                showMenu();
                break;
        }
    }
}