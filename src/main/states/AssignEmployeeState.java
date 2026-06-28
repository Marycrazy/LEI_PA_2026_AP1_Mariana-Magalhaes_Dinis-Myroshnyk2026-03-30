package main.states;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.DatabaseManager;
import main.enums.RepairStatus;
import main.models.Employee;
import main.models.Repair;


public class AssignEmployeeState extends ListState<Employee> {
    private final Repair repair;

    public AssignEmployeeState(Repair repair) {
        this.repair = repair;
    }

    @Override
    protected String getTitle() {
        return "ASSIGN EMPLOYEE - " + repair.getRepairCode();
    }

    @Override
    protected List<Employee> fetchItems() {
        return DatabaseManager.getInstance().getAvailableEmployees(repair.getId());
    }

    protected String[] getColumns() {
        return new String[]{"Name", "email", "address", "phone","nif", "specialization", "start date"};
    }

    @Override
    protected Object[] getRowValues(Employee e) {
        return new Object[]{e.getName(), e.getEmail(), e.getAddress(), e.getPhone(), e.getNif(), e.getSpecialization(), e.getStartDate()};
    }

    @Override
    protected void onSelect(Employee e) {
        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to assign " + e.getName() + " be the employee with the responsibility?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                DatabaseManager.getInstance().assignEmployee(repair, e);
                DatabaseManager.getInstance().updateRepairState(repair, "", RepairStatus.ACCEPTED.toString());
                JOptionPane.showMessageDialog(null, "Employee assigned. They will be notified.");
                back();
            }
    }

    @Override
    protected void renderExtras(JPanel extrasPanel) {}
}