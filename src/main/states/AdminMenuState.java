package main.states;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import main.models.User;
import main.utils.MenuBuilder;

public class AdminMenuState extends State {
    private final User user;

    public AdminMenuState(User user) {
        this.user = user;
    }

    @Override
    public JPanel buildView() {
        return new MenuBuilder(user)
            .addButton("Manage Users", "Manage system users", () -> JOptionPane.showMessageDialog(null, "Manage Users - TODO")) // temporary button
            // .addButton("Manage Users", "Manage system users", () -> next(new ManageUsersMenuState())) // TODO: uncomment when ManageUsersMenuState is ready
            .addButton("Repairs", "View and manage repairs", () -> JOptionPane.showMessageDialog(null, "Repairs - TODO")) // temporary button
            // .addButton("Repairs", "View and manage repairs", () -> next(new ListRepairsState())) // TODO: uncomment when ListRepairsState is ready
            .addButton("Parts", "Manage available parts", () -> JOptionPane.showMessageDialog(null, "Parts - TODO")) // temporary button
            // .addButton("Parts", "Manage available parts", () -> next(new ManagePartsMenuState())) // TODO: uncomment when ManagePartsMenuState is ready
            .addButton("Notifications", "View notifications", () -> JOptionPane.showMessageDialog(null, "Notifications - TODO")) // temporary button
            // .addButton("Notifications", "View notifications", () -> next(new ListNotificationState(true))) // TODO: uncomment when ListNotificationState is ready
            .addButton("Action Log", "View action log", () -> JOptionPane.showMessageDialog(null, "Action Log - TODO")) // temporary button
            // .addButton("Action Log", "View action log", () -> next(new ListActionLogState())) // TODO: uncomment when ActionLogState is ready
            .addButton("Logout", "Logout", () -> { State.user = null; back(); })
            .build();
    }
}