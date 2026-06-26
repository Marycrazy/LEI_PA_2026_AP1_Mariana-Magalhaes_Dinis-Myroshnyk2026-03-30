package main.utils;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.util.ArrayList;
import java.util.List;

public class FormValidator {
    private final List<String> emptyFields = new ArrayList<>();

    public FormValidator require(String label, JTextField field) {
        if (field.getText().isBlank()) emptyFields.add(label);
        return this;
    }

    public FormValidator require(String label, JPasswordField field) {
        if (field.getPassword().length == 0) emptyFields.add(label);
        return this;
    }

    public boolean isValid() {
        return emptyFields.isEmpty();
    }

    public String getErrorMessage() {
        return "Please fill in: " + String.join(", ", emptyFields);
    }
}