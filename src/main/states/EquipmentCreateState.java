package main.states;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.DatabaseManager;
import main.models.Equipment;
import main.utils.FormBuilder;
import main.utils.FormValidator;

public class EquipmentCreateState extends State {
    private JTextField txtBrand = new JTextField(textFieldCols);
    private JTextField txtModel = new JTextField(textFieldCols);
    private JTextField txtBatch = new JTextField(textFieldCols);
    private JTextField txtDate = new JTextField(textFieldCols);

    @Override
    public JPanel buildView() {
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> back());

        JButton btnSubmit = new JButton("Add Equipment");
        btnSubmit.addActionListener(e -> submit());

        return new FormBuilder("Add Equipment")
            .addField("Brand:", txtBrand)
            .addField("Model:", txtModel)
            .addField("Batch:", txtBatch)
            .addField("Manufacturing date (YYYY-MM-DD):", txtDate)
            .addButtonRow(btnBack, btnSubmit)
            .build();
    }

    private boolean validateFields() {
        FormValidator validator = new FormValidator()
            .require("Brand", txtBrand)
            .require("Model", txtModel)
            .require("Batch", txtBatch)
            .require("Manufacturing date", txtDate);

        if (!validator.isValid()) {
            JOptionPane.showMessageDialog(null, validator.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void submit() {
        if (!validateFields()) return;

        ZonedDateTime manufacturingDate;
        try {
            manufacturingDate = LocalDate.parse(txtDate.getText().trim()).atStartOfDay(ZoneId.systemDefault());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Equipment equipment = new Equipment(
            txtBrand.getText().trim(),
            txtModel.getText().trim(),
            txtBatch.getText().trim(),
            manufacturingDate
        );

        try {
            DatabaseManager.getInstance().saveEquipment(equipment, user);
            JOptionPane.showMessageDialog(null,
                "Equipment added successfully. SKU: " + equipment.getSku(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
            back();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to save equipment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}