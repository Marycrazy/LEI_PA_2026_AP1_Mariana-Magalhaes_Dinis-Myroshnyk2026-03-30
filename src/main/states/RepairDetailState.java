package main.states;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import main.DatabaseManager;
import main.enums.RepairStatus;
import main.models.Repair;
import main.utils.FormBuilder;

public class RepairDetailState extends DetailState<Repair>{
    private Repair subject;

    public RepairDetailState(Repair repair) {
        this.subject = repair;
    }

    @Override
    protected String getTitle() {
        return "Repair Details";
    }

    @Override
    protected void onEnter() {
        Repair fresh = DatabaseManager.getInstance().fetchRepair(subject.getId());
        if (fresh != null) subject = fresh;
    }

    @Override
    protected void renderFields(FormBuilder form) {
        form.addField("Code:", readOnly(subject.getRepairCode()))
            .addField("State", readOnly(subject.getState()))
            .addField("Start date:", readOnly(String.valueOf (subject.getStartDate())));

        JTextArea txtContent = new JTextArea(subject.getObservations(), 4, textFieldCols);
        txtContent.setEditable(false);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);

        if(subject.getObservations() != null && !subject.getObservations().isBlank())
            form.addField("Observations:", new javax.swing.JScrollPane(txtContent));
        if (subject.getEndDate() != null)
            form.addField("End date:", readOnly(String.valueOf (subject.getEndDate())));
        if (subject.getCost() > 0)
            form.addField("Cost:", readOnly(String.valueOf (subject.getCost())));
    }

    private JTextField readOnly(String value) {
        JTextField field = new JTextField(value, textFieldCols);
        field.setEditable(false);
        return field;
    }

    protected List<JButton> getActions() {
        List<JButton> actions = new ArrayList<>();
        if (user.getType().equals("ADMIN")) {
            switch (subject.getState()) {
                case "PENDING":
                    actions.add(statusButton("Approve", "approve this repair", RepairStatus.ACCEPTED));
                    actions.add(statusButton("Reject", "reject this repair", RepairStatus.REJECTED_BY_ADMIN));
                    break;
                case "COMPLETED":
                    actions.add(statusButton("Archive", "archive this repair", RepairStatus.ARCHIVED));
                    break;
                case "REJECTED_BY_EMPLOYEE":
                    actions.add(statusButton("Approve and assign another employee", "assign another employee", RepairStatus.ACCEPTED));
                    actions.add(statusButton("Reject", "reject this repair", RepairStatus.REJECTED_BY_ADMIN));
                    actions.add(statusButton("Archive", "archive this repair", RepairStatus.ARCHIVED));
                    break;
                case "REJECTED_BY_ADMIN":
                    actions.add(statusButton("Archive", "archive this repair", RepairStatus.ARCHIVED));
                    break;
            }
        }else if (user.getType().equals("EMPLOYEE")) {
            switch (subject.getState()) {
                case "ACCEPTED":
                    actions.add(statusButton("Accepted", "accepted this repair", RepairStatus.IN_PROGRESS));
                    actions.add(statusButton("Reject", "reject this repair", RepairStatus.REJECTED_BY_EMPLOYEE));
                    break;
                case "IN_PROGRESS":
                    actions.add(statusButton("Completed", "completed this repair", RepairStatus.COMPLETED));
                    break;
            }
        }

        JButton btnPrint = new JButton("Print Statement");
        btnPrint.addActionListener(e -> printStatement());
        actions.add(btnPrint);
        return actions;
    }

    private void printStatement() {
        List<main.models.Log> logs = DatabaseManager.getInstance().getLogsForRepair(subject.getId());

        if (logs.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No logged actions found for this repair.", "Nothing to print", JOptionPane.WARNING_MESSAGE);
            return;
        }

        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setSelectedFile(new java.io.File("repair_" + subject.getRepairCode() + "_statement.pdf"));
        if (chooser.showSaveDialog(null) != javax.swing.JFileChooser.APPROVE_OPTION) return;

        java.io.File outputFile = chooser.getSelectedFile();

        try {
            main.utils.RepairStatementPrinter.print(subject.getRepairCode(), logs, outputFile);
            JOptionPane.showMessageDialog(null, "Statement saved to " + outputFile.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Could not generate PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton statusButton(String label, String actionDescription, RepairStatus newStatus) {
        JButton button = new JButton(label);
        button.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to " + actionDescription + "?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                if (user.getType().equals("ADMIN") && newStatus == RepairStatus.ACCEPTED)
                    next(new AssignEmployeeState(subject));
                else if (newStatus == RepairStatus.REJECTED_BY_ADMIN || newStatus == RepairStatus.REJECTED_BY_EMPLOYEE)
                    reject(newStatus);
                else if(newStatus == RepairStatus.COMPLETED){
                    String costString = JOptionPane.showInputDialog(null, "Please enter the cost of the repair:", "Repair Cost", JOptionPane.PLAIN_MESSAGE);
                    if (costString != null && !costString.isBlank()) {
                        try {
                            double cost = Double.parseDouble(costString);
                            updateState(newStatus, cost);
                            back();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Invalid cost format. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                else{
                    updateState(newStatus, null);
                    back();
                }
            }
        });
        return button;
    }

    private void reject(RepairStatus newStatus) {
        String reason = JOptionPane.showInputDialog(null, "Please enter the reason for rejection:", "Reject Repair", 1);
        if (reason != null) {
            try {
                DatabaseManager.getInstance().updateRepairState(subject, reason, newStatus.toString(), null);
                JOptionPane.showMessageDialog(null, "Repair rejected successfully.");
                back();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Could not reject repair: " + e.getMessage(), "Error", 0);
            }
        }

    }

    private void updateState(RepairStatus newState, Double cost) {
        DatabaseManager.getInstance().updateRepairState(subject, "", newState.toString(), cost);
        JOptionPane.showMessageDialog(null, "Repair state updated successfully.");
    }
}