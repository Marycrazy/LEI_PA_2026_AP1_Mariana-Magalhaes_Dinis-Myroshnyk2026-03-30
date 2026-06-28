package main.utils;

import main.DatabaseManager;
import main.DatabaseManager.DbFunctionCall;

/**
 * Core validation helper targeting basic console string checks and database field constraints.
 */
public class Validator {
    /**
     * Internal structural null and empty string helper.
     *
     * @param value the content target
     * @return {@code true} if null, empty or entirely spaces; {@code false} otherwise
     */
    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Evaluates text values against both mandatory content checks and a dedicated database validator routine.
     *
     * @param dbFunc the identifier name of the specific storage validation rule routine
     * @param value  the string token payload to inspect
     * @return {@code true} if value satisfies all rules; {@code false} if empty or rejected by the database
     */
    public static boolean isValid(String dbFunc, String value) {
        if (isEmpty(value)) {
            System.err.println("Field cannot be empty!");
            return false;
        } else if (DatabaseManager.getInstance().validateField(new DbFunctionCall(dbFunc, value)) != null) return false;
        return true;
    }

    /**
     * Assesses basic structural presence ensuring a console field input is not empty.
     *
     * @param value the raw text token being scrutinized
     * @return {@code true} if content is present; {@code false} if empty or spaces
     */
    public static boolean isValid(String value) {
        if (isEmpty(value)) {
            System.err.println("Field cannot be empty!");
            return false;
        } else return true;
    }
}