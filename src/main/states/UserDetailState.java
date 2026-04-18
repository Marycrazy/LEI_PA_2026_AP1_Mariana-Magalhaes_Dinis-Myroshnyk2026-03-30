package main.states;

import java.util.LinkedHashMap;
import java.util.Map;

import main.DatabaseManager;
import main.enums.UserStatus;
import main.models.Client;
import main.models.Employee;
import main.models.RegistrableUser;
import main.models.User;

public class UserDetailState extends DetailState<User> {
    private User subject;

    public UserDetailState(User subject) {
        this.subject = subject;
    }

    @Override
    public void render() {
        User fresh = DatabaseManager.getInstance().fetchUser(subject.getUsername());
        if (fresh != null) subject = fresh;
        super.render();
    }

    @Override
    protected String getTitle() { return "USER DETAILS"; }

    @Override
    protected void renderFields() {
        System.out.println("Name:     " + subject.getName());
        System.out.println("Username: " + subject.getUsername());
        System.out.println("Email:    " + subject.getEmail());
        System.out.println("Type:     " + subject.getType());
        System.out.println("Status:   " + subject.getStatus());

        if (subject instanceof RegistrableUser reg) {
            System.out.println("NIF:      " + reg.getNif());
            System.out.println("Phone:    " + reg.getPhone());
            System.out.println("Address:  " + reg.getAddress());
        }

        if (subject instanceof Employee emp) {
            System.out.println("Level:    " + emp.getSpecialization());
            System.out.println("Since:    " + emp.getStartDate().toLocalDate());
        } else if (subject instanceof Client client) {
            System.out.println("Sector:   " + client.getSector());
            System.out.println("Tier:     " + client.getScale());
        }
    }

    @Override
    protected Map<String, String> getActions() {
        Map<String, String> actions = new LinkedHashMap<>();
        switch (subject.getStatus()) {
            case "PENDING":
                actions.put("A", "Approve");
                actions.put("R", "Reject");
                break;
            case "ACTIVE":
                actions.put("D", "Deactivate");
                break;
            case "INACTIVE":
                actions.put("E", "Activate");
                break;
        }
        actions.put("P", "Edit profile");
        return actions;
    }

    @Override
    protected void handleAction(String key) {
        switch (key) {
            case "A": DatabaseManager.getInstance().setUserStatus(user, UserStatus.ACTIVE.toString()); back(); break;
            case "R": DatabaseManager.getInstance().setUserStatus(user, UserStatus.REJECTED.toString()); back(); break;
            case "D": DatabaseManager.getInstance().setUserStatus(user, UserStatus.INACTIVE.toString()); back(); break;
            case "E": DatabaseManager.getInstance().setUserStatus(user, UserStatus.ACTIVE.toString()); back(); break;
            case "P": next(new EditUserState(subject)); break;
        }
    }
}