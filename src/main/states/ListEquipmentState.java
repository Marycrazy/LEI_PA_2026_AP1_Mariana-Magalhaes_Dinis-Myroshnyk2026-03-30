package main.states;

import java.util.List;
import main.DatabaseManager;
import main.models.Equipment;
import main.models.User;
import main.utils.Input;

public class ListEquipmentState extends ListState<Equipment> {
    private final User owner;
    private String search = "";

    public ListEquipmentState(User owner) {
        this.owner = owner;
    }

    @Override
    protected String getTitle() { return "MY EQUIPMENT"; }

    @Override
    protected List<Equipment> fetchItems() {
        return DatabaseManager.getInstance().getEquipment(owner.getUserId(), search);
    }

    @Override
    protected String getRowLabel(Equipment e, int index) {
        return String.format("%-15s %-20s SKU: %-10d %s",
            e.getBrand(), e.getModel(), e.getSku(), e.getManufacturingDate());
    }

    @Override
    protected void onSelect(Equipment e) {
        next(new EquipmentDetailState(e));
    }

    @Override
    protected void renderExtras() {
        System.out.println("Search: [" + (search.isEmpty() ? " " : search) + "]  F. Filter");
    }

    @Override
    protected boolean handleExtra(String input) {
        switch (input) {
            case "F":
                System.out.print("Search (brand or SKU): ");
                search = Input.getScanner().nextLine().trim();
                return true;
            default:
                return false;
        }
    }
}