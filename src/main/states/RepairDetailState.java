package main.states;

import java.util.LinkedHashMap;
import java.util.Map;
import main.DatabaseManager;
import main.enums.RepairStatus;
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
        if (user.getType().equals("ADMIN")) {
            switch (subject.getState()) {
                case "PENDING":
                    actions.put("A", "Approve and assign employee");
                    actions.put("R", "Reject");
                    break;
                case "COMPLETED":
                    actions.put("X", "Archive");
                    break;
                case "REJECTED_BY_EMPLOYEE":
                    actions.put("A", "Assign another employee");
                    actions.put("R", "Reject");
                    break;

            }
        }else if (user.getType().equals("EMPLOYEE")) {
            switch (subject.getState()) {
                case "ACCEPTED":
                    actions.put("A", "Accept and start repair");
                    actions.put("R", "Reject");
                    break;
                case "IN_PROGRESS":
                    actions.put("C", "Mark as completed");
                    break;
            }
        }
        return actions;
    }

    @Override
    protected void handleAction(String key) {
        switch (key) {
            case "A": if (user.getType().equals("ADMIN")) next(new AssignEmployeeState(subject));
                else accept_by_employee();
                break;
            case "R": reject(); break;
            case "C": mark_as_completed(); break;
            case "X": archive(); break;
        }
    }

    private void reject() {
        System.out.print("Reason (optional): ");
        String reason = Input.getScanner().nextLine().trim();
        try {
            if(user.getType().equals("ADMIN"))
                DatabaseManager.getInstance().updateRepairState(subject, reason, RepairStatus.REJECTED_BY_ADMIN.toString());
            else
                DatabaseManager.getInstance().updateRepairState(subject, reason, RepairStatus.REJECTED_BY_EMPLOYEE.toString());
            System.out.println("Repair rejected.");
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }
        PressKey.enter();
        back();
    }

    private void archive() {
        try {
            DatabaseManager.getInstance().updateRepairState(subject, "", RepairStatus.ARCHIVED.toString());
            System.out.println("Repair archived.");
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }
        PressKey.enter();
        back();
    }

    private void accept_by_employee() {
        try {
            DatabaseManager.getInstance().updateRepairState(subject, "", RepairStatus.IN_PROGRESS.toString());
            System.out.println("Repair accepted. You can start working on it.");
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }
        PressKey.enter();
        back();
    }

    private void mark_as_completed() {
        try {
            DatabaseManager.getInstance().updateRepairState(subject, "", RepairStatus.COMPLETED.toString());
            System.out.println("Repair marked as completed. Awaiting admin approval.");
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }
        PressKey.enter();
        back();
    }
        
}