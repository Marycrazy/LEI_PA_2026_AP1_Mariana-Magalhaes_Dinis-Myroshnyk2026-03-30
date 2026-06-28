package main.states;

import javax.swing.JPanel;
import main.utils.MenuBuilder;

public class AdminMenuState extends State {
    @Override
    public JPanel buildView() {
        return new MenuBuilder(user)
            .addButton("Manage Users", "Manage system users", () -> next(new ManageUsersMenuState()))
            .addButton("Repairs", "View and manage repairs", () -> next(new RepairListState()))
            .addButton("Parts", "Manage available parts", () -> next(new ManagePartsMenuState()))
            .addButton("Notifications", "View notifications", () -> next(new NotificationListState()))
            .addButton("Action Log", "View action log", () -> next(new LogsListState()))
            .addButton("Change Profile", "Edit your account details", () -> next(new UserEditState(user)))
            .addButton("Logout", "Logout", () -> { State.user = null; back(); })
            .build();
    }
}