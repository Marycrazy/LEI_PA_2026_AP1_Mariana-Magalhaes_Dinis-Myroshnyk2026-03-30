package main.states;

import main.DatabaseManager;
import main.models.Part;
import main.utils.PressKey;
import main.utils.Input;

public class ManagePartsMenuState extends State {

    @Override
    public void render() {
        System.out.println("--- MANAGE PARTS ---\n");
        System.out.println("1. List parts");
        System.out.println("2. Add part");
        System.out.println("0. Back");
        System.out.print("Choice: ");
    }

    @Override
    public void handleInput() {
        String input = Input.getScanner().nextLine().trim();

        switch (input) {
            case "1": next(new ListPartsState()); break;
            case "2": createPart(); break;
            case "0": back(); break;
            default:
                System.out.println("Invalid option.");
                PressKey.enter();
        }
    }

    private void createPart() {
        Part part = Part.create();
        if (part == null) {
            System.out.println("Cancelled.");
        } else {
            try {
                DatabaseManager.getInstance().savePart(part);
                System.out.println("Part added successfully.");
            } catch (Exception e) {
                System.err.println("Failed to save part: " + e.getMessage());
            }
        }
        PressKey.enter();
    }
}