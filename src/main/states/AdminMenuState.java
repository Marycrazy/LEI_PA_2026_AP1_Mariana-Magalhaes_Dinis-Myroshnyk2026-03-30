package main.states;

import javax.swing.JPanel;
import main.utils.MenuBuilder;

public class AdminMenuState extends State {
    @Override
    public JPanel buildView() {
        return new MenuBuilder(user)
            .addButton("Manage Users", "Manage system users", () -> next(new ManageUsersMenuState()))
            .addButton("Repairs", "View and manage repairs", () -> next(new RepairListState())) // temporary button
            // .addButton("Repairs", "View and manage repairs", () -> next(new ListRepairsState())) // TODO: uncomment when ListRepairsState is ready
            .addButton("Parts", "Manage available parts", () -> next(new ManagePartsMenuState()))
            // .addButton("Parts", "Manage available parts", () -> next(new ManagePartsMenuState())) // TODO: uncomment when ManagePartsMenuState is ready
            .addButton("Notifications", "View notifications", () -> next(new NotificationListState()))
            .addButton("Action Log", "View action log", () -> next(new LogsListState()))
            // .addButton("Action Log", "View action log", () -> next(new ListActionLogState())) // TODO: uncomment when ActionLogState is ready
            .addButton("Change Profile", "Edit your account details", () -> next(new EditUserState(user)))
            .addButton("Logout", "Logout", () -> { State.user = null; back(); })
            .build();
    }
}