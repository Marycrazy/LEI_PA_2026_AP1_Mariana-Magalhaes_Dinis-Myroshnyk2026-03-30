package main.states;

import main.DatabaseManager;
import main.models.Admin;
import main.models.Client;
import main.models.Employee;
import main.models.User;
import main.utils.PressKey;

public class EditUserState extends State {
    private final User subject;

    public EditUserState(User subject) {
        this.subject = subject;
    }

    @Override
    public void render() {
        System.out.println("--- EDIT USER: " + subject.getUsername() + " ---");
        System.out.println("Fill each field, or press Enter to keep the current value.");
    }

    @Override
    public void handleInput() {
        User updated = null;

        if (subject instanceof Client client) updated = Client.edit(client);
        else if (subject instanceof Employee employee) updated = Employee.edit(employee);
        else if (subject instanceof Admin admin) updated = Admin.edit(admin);

        if (updated == null) {
            System.out.println("Edit cancelled.");
        } else {
            try {
                DatabaseManager.getInstance().updateUser(updated);
                System.out.println("User updated successfully.");
            } catch (Exception e) {
                System.err.println("Failed to save: " + e.getMessage());
            }
        }

        PressKey.enter();
        back();
    }
}