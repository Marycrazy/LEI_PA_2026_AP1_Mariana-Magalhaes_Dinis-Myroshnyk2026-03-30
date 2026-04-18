package main.states;

import java.util.List;
import main.DatabaseManager;
import main.models.User;

public class ListUsersState extends ListState<User> {
    private String search = "";
    private String sortBy = "name";
    private boolean asc = true;

    @Override
    protected String getTitle() { return "USERS"; }

    @Override
    protected List<User> fetchItems() {
        return DatabaseManager.getInstance().getUsers(search, sortBy, asc, user);
    }

    @Override
    protected String getRowLabel(User user, int index) {
        return String.format("%-20s %-10s %s",
            user.getName(), user.getType(), user.getStatus());
    }

    @Override
    protected void onSelect(User user) {
        next(new UserDetailState(user));
    }

    @Override
    protected void renderExtras() {
        System.out.println("Sort: N. Name  T. Type  S. Status  | Order: A. Asc  D. Desc");
        System.out.println("Search: [" + (search.isEmpty() ? "        " : search) + "]  F. Filter");
    }

    @Override
    protected boolean handleExtra(String input) {
        switch (input) {
            case "N": sortBy = "name"; return true;
            case "T": sortBy = "type"; return true;
            case "S": sortBy = "status"; return true;
            case "A": asc = true; return true;
            case "D": asc = false; return true;
            case "F":
                System.out.print("Search term: ");
                search = main.utils.Input.getScanner().nextLine().trim();
                return true;
            default: return false;
        }
    }
}