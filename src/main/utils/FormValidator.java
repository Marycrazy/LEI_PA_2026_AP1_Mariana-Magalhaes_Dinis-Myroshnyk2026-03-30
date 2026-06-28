package main.utils;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.DatabaseManager;
import main.DatabaseManager.DbFunctionCall;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent interface builder utility for evaluating and aggregating client-side UI form input
 * validation constraints alongside database checking routines.
 */
public class FormValidator {
    /** List tracking all accumulated validation error messages. */
    private final List<String> errors = new ArrayList<>();

    /**
     * Mandates that a text component must contain non-blank character values.
     *
     * @param label the descriptive label tag used to identify the input field in error strings
     * @param field the GUI input component target being scrutinized
     * @return this builder instance for fluent validation chaining
     */
    public FormValidator require(String label, JTextField field) {
        if (field.getText().isBlank()) errors.add(label + " cannot be empty");
        return this;
    }

    /**
     * Mandates that a password entry box cannot be empty.
     *
     * @param label the descriptive label tag used to identify the input field in error strings
     * @param field the GUI password component target being scrutinized
     * @return this builder instance for fluent validation chaining
     */
    public FormValidator require(String label, JPasswordField field) {
        if (field.getPassword().length == 0) errors.add(label + " cannot be empty");
        return this;
    }

    /**
     * Dispatches an interactive database function routine check to validate a text field value server-side.
     *
     * @param label  the descriptive label tag used to identify the input field in error strings
     * @param field  the GUI input component target being scrutinized
     * @param dbFunc the identifier name of the specific database storage verification function
     * @return this builder instance for fluent validation chaining
     */
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

    /**
     * Checks if all chained verification checks passed successfully without triggers.
     *
     * @return {@code true} if no validation error logs exist; {@code false} otherwise
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Compiles all aggregated validation messages into a single newline-separated block string.
     *
     * @return combined string statement summarizing errors
     */
    public String getErrorMessage() {
        return String.join("\n", errors);
    }
}