package main.utils;

import main.DatabaseManager;

public class Validator {
    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValid(String dbFunc, String value) {
        if (isEmpty(value)) {
            System.err.println("Field cannot be empty!");
            return false;
        } else if (!DatabaseManager.getInstance().validateField(dbFunc, value)) return false;
        return true;
    }

    public static boolean isValid(String value) {
        if (isEmpty(value)) {
            System.err.println("Field cannot be empty!");
            return false;
        } else return true;
    }
}