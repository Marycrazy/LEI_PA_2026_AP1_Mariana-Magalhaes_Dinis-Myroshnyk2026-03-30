package main.states;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import main.models.User;
import main.utils.MenuBuilder;

public class ClientMenuState extends State {
    private final User user;

    public ClientMenuState(User user) {
        this.user = user;
    }

    @Override
    public JPanel buildView() {
        return new MenuBuilder(user)
            .addButton("My Equipment", "View and manage your equipment", () -> JOptionPane.showMessageDialog(null, "My Equipment - TODO")) // temporary button
            // .addButton("My Equipment", "View and manage your equipment", () -> next(new EquipmentMenuState())) // TODO: uncomment when EquipmentMenuState is ready
            .addButton("My Repairs", "View your repair requests", () -> JOptionPane.showMessageDialog(null, "My Repairs - TODO")) // temporary button
            // .addButton("My Repairs", "View your repair requests", () -> next(new ListRepairsState())) // TODO: uncomment when ListRepairsState is ready
            .addButton("Notifications", "View notifications", () -> JOptionPane.showMessageDialog(null, "Notifications - TODO")) // temporary button
            // .addButton("Notifications", "View notifications", () -> next(new NotificationMenuState())) // TODO: uncomment when NotificationMenuState is ready
            .addButton("Change Profile", "Edit your account details", () -> JOptionPane.showMessageDialog(null, "Change Profile - TODO")) // temporary button
            // .addButton("Change Profile", "Edit your account details", () -> next(new EditUserState(user))) // TODO: uncomment when EditUserState is ready
            .addButton("Logout", "Logout", () -> { State.user = null; back(); })
            .build();
    }
}