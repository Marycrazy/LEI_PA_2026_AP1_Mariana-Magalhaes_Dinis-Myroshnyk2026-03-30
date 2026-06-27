package main.utils;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.DatabaseManager;
import main.DatabaseManager.DbFunctionCall;

import java.util.ArrayList;
import java.util.List;

public class FormValidator {
    private final List<String> errors = new ArrayList<>();

    public FormValidator require(String label, JTextField field) {
        if (field.getText().isBlank()) errors.add(label + " cannot be empty");
        return this;
    }

    public FormValidator require(String label, JPasswordField field) {
        if (field.getPassword().length == 0) errors.add(label + " cannot be empty");
        return this;
    }

    public FormValidator dbValidate(String label, JTextField field, String dbFunc) {
        String value = field.getText();
        if (value.isBlank()) {
            errors.add(label + " cannot be empty");
            return this;
        }

        String errorMessage = DatabaseManager.getInstance().validateField(new DbFunctionCall(dbFunc, value));
        if (errorMessage != null) {
            errors.add(label + ": " + errorMessage);
        }
        return this;
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public String getErrorMessage() {
        return "Please fill in: " + String.join(", ", errors);
    }
}