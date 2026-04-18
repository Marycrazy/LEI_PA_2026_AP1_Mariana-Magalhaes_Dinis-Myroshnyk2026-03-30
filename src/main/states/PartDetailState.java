package main.states;

import java.util.LinkedHashMap;
import java.util.Map;
import main.DatabaseManager;
import main.models.Part;
import main.utils.PressKey;

public class PartDetailState extends DetailState<Part> {
    private Part subject;

    public PartDetailState(Part subject) {
        this.subject = subject;
    }

    @Override
    protected String getTitle() { return "PART DETAILS"; }

    @Override
    public void render() {
        Part fresh = DatabaseManager.getInstance().fetchPart(subject.getId());
        if (fresh != null) subject = fresh;
        super.render();
    }

    @Override
    protected void renderFields() {
        System.out.println("Code:         " + subject.getId());
        System.out.println("Designation:  " + subject.getDesignation());
        System.out.println("Manufacturer: " + subject.getManufacturer());
        System.out.println("Stock:        " + subject.getStockQuantity());
    }

    @Override
    protected Map<String, String> getActions() {
        Map<String, String> actions = new LinkedHashMap<>();
        actions.put("E", "Edit part");
        actions.put("U", "Update stock");
        return actions;
    }

    @Override
    protected void handleAction(String key) {
        switch (key) {
            case "E":
                Part updated = Part.edit(subject);
                if (updated == null) {
                    System.out.println("Cancelled.");
                } else {
                    try {
                        DatabaseManager.getInstance().updatePart(updated);
                        System.out.println("Part updated successfully.");
                    } catch (Exception e) {
                        System.err.println("Failed to update: " + e.getMessage());
                    }
                }
                PressKey.enter();
                break;
            case "U":
                updateStock();
                break;
        }
    }

    private void updateStock() {
        System.out.print("Quantity to add (use negative to subtract): ");
        try {
            int delta = Integer.parseInt(main.utils.Input.getScanner().nextLine().trim());
            int newQty = subject.getStockQuantity() + delta;
            if (newQty < 0) {
                System.err.println("Stock cannot go below 0.");
            } else {
                subject.setStockQuantity(newQty);
                DatabaseManager.getInstance().updatePart(subject);
                System.out.println("Stock updated to " + newQty + ".");
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid number.");
        }
        PressKey.enter();
    }
}