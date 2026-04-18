package main.states;

import java.util.LinkedHashMap;
import java.util.Map;

import main.DatabaseManager;
import main.models.Equipment;
import main.utils.Input;
import main.utils.PressKey;

public class EquipmentDetailState extends DetailState<Equipment> {
    private final Equipment subject;

    public EquipmentDetailState(Equipment subject) {
        this.subject = subject;
    }

    @Override
    protected String getTitle() { return "EQUIPMENT DETAILS"; }

    @Override
    protected void renderFields() {
        System.out.println("Brand:      " + subject.getBrand());
        System.out.println("Model:      " + subject.getModel());
        System.out.println("SKU:        " + subject.getSku());
        System.out.println("Batch:      " + subject.getBatch());
        System.out.println("Mfg. date:  " + subject.getManufacturingDate());
    }

    @Override
    protected Map<String, String> getActions() {
        Map<String, String> actions = new LinkedHashMap<>();
        actions.put("R", "Request repair");
        return actions;
    }

    @Override
    protected void handleAction(String key) {
        if (key.equals("R")) submitRepair();
    }

    private void submitRepair() {
        System.out.print("Confirm by entering SKU [" + subject.getSku() + "]: ");
        String confirm = Input.getScanner().nextLine().trim();
        if (!confirm.equals(String.valueOf(subject.getSku()))) {
            System.out.println("SKU mismatch, request cancelled.");
            PressKey.enter();
            return;
        }

        try {
            DatabaseManager.getInstance().saveRepair(subject, user);
            System.out.println("Repair request submitted. A manager will review it shortly.");
        } catch (Exception e) {
            System.err.println("Failed to submit: " + e.getMessage());
        }
        PressKey.enter();
    }
}