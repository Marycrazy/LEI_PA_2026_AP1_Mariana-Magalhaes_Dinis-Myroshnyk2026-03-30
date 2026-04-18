package main.states;

import java.util.LinkedHashMap;
import java.util.Map;
import main.DatabaseManager;
import main.models.Repair;
import main.utils.Input;
import main.utils.PressKey;

public class RepairDetailState extends DetailState<Repair> {
    private Repair subject;

    public RepairDetailState(Repair repair) {
        this.subject = repair;
    }

    @Override
    public void render() {
        Repair fresh = DatabaseManager.getInstance().fetchRepair(subject.getId());
        if (fresh != null) subject = fresh;
        super.render();
    }

    @Override
    protected String getTitle() { return "REPAIR DETAILS"; }

    @Override
    protected void renderFields() {
        System.out.println("Code        : " + subject.getRepairCode());
        System.out.println("State       : " + subject.getState());
        System.out.println("Submitted   : " + subject.getStartDate().toLocalDate());
        System.out.println("Observations: " + subject.getObservations());

        if (subject.getEndDate() != null)
            System.out.println("Finished    : " + subject.getEndDate().toLocalDate());
        if (subject.getCost() > 0)
            System.out.println("Cost        : " + subject.getCost() + " €");
    }

    @Override
    protected Map<String, String> getActions() {
        Map<String, String> actions = new LinkedHashMap<>();
        switch (subject.getState()) {
            case "PENDING":
                actions.put("A", "Approve and assign employee");
                actions.put("R", "Reject");
                break;
            case "COMPLETED":
                actions.put("X", "Archive");
                break;
        }
        return actions;
    }

    @Override
    protected void handleAction(String key) {
        switch (key) {
            case "A": next(new AssignEmployeeState(subject)); break;
            case "R": reject(); break;
            case "X": archive(); break;
        }
    }

    private void reject() {
        System.out.print("Reason (optional): ");
        String reason = Input.getScanner().nextLine().trim();
        try {
            DatabaseManager.getInstance().rejectRepair(subject, reason);
            System.out.println("Repair rejected.");
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }
        PressKey.enter();
        back();
    }

    private void archive() {
        try {
            DatabaseManager.getInstance().updateRepairState(subject.getId(), "ARCHIVED");
            System.out.println("Repair archived.");
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }
        PressKey.enter();
        back();
    }
}