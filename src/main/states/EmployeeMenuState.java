package main.states;

import javax.swing.JPanel;
import main.utils.MenuBuilder;

public class EmployeeMenuState extends State {
    @Override
    public JPanel buildView() {
        return new MenuBuilder(user)
            .addButton("View Repairs", "View your assigned repairs", () -> next(new RepairListState()))
            .addButton("Notifications", "View notifications", () -> next(new NotificationListState()))
            .addButton("Change Profile", "Edit your account details", () -> next(new EditUserState(user)))
            .addButton("Logout", "Logout", () -> { State.user = null; back(); })
            .build();
    }
}