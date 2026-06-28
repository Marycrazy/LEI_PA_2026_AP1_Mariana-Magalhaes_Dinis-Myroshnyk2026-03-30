package main.states;

import javax.swing.JPanel;
import main.utils.MenuBuilder;

public class ClientMenuState extends State {
    @Override
    public JPanel buildView() {
        return new MenuBuilder(user)
            .addButton("My Equipment", "View and manage your equipment", () -> next(new EquipmentMenuState()))
            .addButton("My Repairs", "View your repair requests", () -> next(new RepairListState()))
            .addButton("Notifications", "View notifications", () -> next(new NotificationListState()))
            .addButton("Change Profile", "Edit your account details", () -> next(new UserEditState(user)))
            .addButton("Logout", "Logout", () -> { State.user = null; back(); })
            .build();
    }
}