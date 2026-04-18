package main.states;

import java.util.List;
import main.DatabaseManager;
import main.models.Part;
import main.utils.Input;

public class ListPartsState extends ListState<Part> {
    private String search = "";

    @Override
    protected String getTitle() { return "PARTS"; }

    @Override
    protected List<Part> fetchItems() {
        return DatabaseManager.getInstance().getParts(search);
    }

    @Override
    protected String getRowLabel(Part p, int index) {
        return String.format("%-30s %-20s stock: %d",
            p.getDesignation(), p.getManufacturer(), p.getStockQuantity());
    }

    @Override
    protected void onSelect(Part p) { next(new PartDetailState(p)); }

    @Override
    protected void renderExtras() {
        System.out.println("Search: [" + (search.isEmpty() ? " " : search) + "]  F. Filter");
    }

    @Override
    protected boolean handleExtra(String input) {
        switch (input) {
            case "F":
                System.out.print("Search: ");
                search = Input.getScanner().nextLine().trim();
                return true;
            default:
                return false;
        }
    }
}