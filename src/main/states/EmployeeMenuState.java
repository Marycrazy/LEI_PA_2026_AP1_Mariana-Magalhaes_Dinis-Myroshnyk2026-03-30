package main.states;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import main.utils.MenuBuilder;

public class EmployeeMenuState extends State {
    @Override
    public JPanel buildView() {
        return new MenuBuilder(user)
            .addButton("View Repairs", "View your assigned repairs", () -> next(new ListRepairsState()))
            
            .addButton("Notifications", "View notifications", () -> next(new NotificationListState()))
            .addButton("Change Profile", "Edit your account details", () -> JOptionPane.showMessageDialog(null, "Change Profile - TODO")) // temporary button
            // .addButton("Change Profile", "Edit your account details", () -> next(new EditUserState(user))) // TODO: uncomment when EditUserState is ready
            .addButton("Logout", "Logout", () -> { State.user = null; back(); })
            .build();
    }
}