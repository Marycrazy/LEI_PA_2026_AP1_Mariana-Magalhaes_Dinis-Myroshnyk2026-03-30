package main.states;

import java.util.List;
import main.DatabaseManager;
import main.models.Employee;
import main.models.Repair;
import main.utils.PressKey;

public class AssignEmployeeState extends ListState<Employee> {
    private final Repair repair;

    public AssignEmployeeState(Repair repair) {
        this.repair = repair;
    }

    @Override
    protected String getTitle() { return "ASSIGN EMPLOYEE — " + repair.getRepairCode(); }

    @Override
    protected List<Employee> fetchItems() {
        return DatabaseManager.getInstance().getAvailableEmployees(repair.getId());
    }

    @Override
    protected String getRowLabel(Employee e, int index) {
        return String.format("%-20s Level: %s", e.getName(), e.getSpecialization());
    }

    @Override
    protected void onSelect(Employee e) {
        try {
            DatabaseManager.getInstance().assignEmployee(repair, e);
            System.out.println("Employee assigned. They will be notified.");
        } catch (Exception ex) {
            System.err.println("Failed to assign: " + ex.getMessage());
        }
        PressKey.enter();
        back();
        back();
    }
}