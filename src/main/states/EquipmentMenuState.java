package main.states;

import main.DatabaseManager;
import main.models.Equipment;
import main.utils.Input;
import main.utils.PressKey;

public class EquipmentMenuState extends State {

    @Override
    public void render() {
        System.out.println("--- EQUIPMENT ---\n");
        System.out.println("1. List my equipment");
        System.out.println("2. Add equipment");
        System.out.println("0. Back");
        System.out.print("Choice: ");
    }

    @Override
    public void handleInput() {
        String input = Input.getScanner().nextLine().trim();
        switch (input) {
            case "1": next(new ListEquipmentState(user)); break;
            case "2": addEquipment(); break;
            case "0": back(); break;
            default:
                System.out.println("Invalid option.");
                PressKey.enter();
        }
    }

    private void addEquipment() {
        Equipment equipment = Equipment.create();
        if (equipment == null) {
            System.out.println("Cancelled.");
            PressKey.enter();
            return;
        }
        try {
            DatabaseManager.getInstance().saveEquipment(equipment, user);
            System.out.println("Equipment added successfully. SKU: " + equipment.getSku());
        } catch (Exception e) {
            System.err.println("Failed to save equipment: " + e.getMessage());
        }
        PressKey.enter();
    }
}