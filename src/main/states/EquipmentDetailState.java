package main.states;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import main.DatabaseManager;
import main.models.Equipment;
import main.utils.FormBuilder;


public class EquipmentDetailState extends DetailState<Equipment> {
    private final Equipment subject;

    public EquipmentDetailState(Equipment subject) {
        this.subject = subject;
    }

    @Override
    protected String getTitle() {
        return "Equipment Details";
    }

    @Override
    protected void onEnter() {}

    @Override
    protected void renderFields(FormBuilder form) {
        form.addField("Brand:", readOnly(subject.getBrand()))
            .addField("Model:", readOnly(subject.getModel()))
            .addField("SKU:", readOnly(String.valueOf (subject.getSku())))
            .addField("Batch:", readOnly(subject.getBatch()))
            .addField("Manufacturing date:", readOnly(String.valueOf (subject.getManufacturingDate())))
            .addField("Last repair date:", readOnly(String.valueOf (subject.getLastRepairDate())))
            .addField("Last submission date:", readOnly(String.valueOf (subject.getLastSubmissionDate())));
    }

    private JTextField readOnly(String value) {
        JTextField field = new JTextField(value, textFieldCols);
        field.setEditable(false);
        return field;
    }

    @Override
    protected List<JButton> getActions() {
        List<JButton> actions = new ArrayList<>();
        JButton button = new JButton("Submit repair");
        button.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to submit this equipment for repair ?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                DatabaseManager.getInstance().saveRepair(subject, user);
                JOptionPane.showMessageDialog(null, "Repair request submitted. A manager will review it shortly.");
                back();
            }
        });

        actions.add(button);

        return actions;
    }
}