package main.states;

import javax.swing.JPanel;
import main.utils.MenuBuilder;

public class ManageUsersMenuState extends State {
    @Override
    public JPanel buildView() {
        return new MenuBuilder()
            .addButton("Create User", "Create a new user", () -> next(new AdminUserCreateSelectorState()))
            .addButton("List Users", "View and manage users", () -> next(new UserListState()))
            .addButton("Back", "Return to previous menu", this::back)
            .build();
    }
}