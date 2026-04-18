package main.utils;

import java.util.Scanner;

public class Input {
    private static Scanner scanner;

    public static Scanner getScanner() {
        if (scanner == null) scanner = new Scanner(System.in);
        return scanner;
    }

    public static String withExit(String prompt) {
        System.out.print("(type ':q' to exit) ");
        System.out.print(prompt);
        String input = getScanner().nextLine();
        if (input.equals(":q")) return null;
        return input;
    }

    public static String getInput(String field) {
        while (true) {
            String input = withExit(field + ": ");
            if (input == null) return null;
            if (Validator.isValid(input)) return input;
        }
    }

    public static String getInput(String field, String dbFunc) {
        while (true) {
            String input = withExit(field + ": ");
            if (input == null) return null;
            if (Validator.isValid(dbFunc, input)) return input;
        }
    }

    public static String getInput(String field, String defaultValue, boolean edit) {
        String input = withExit(field + " [" + defaultValue + "]: ");
        if (input == null) return null;
        return input.isEmpty() ? defaultValue : input;
    }

    public static String getInput(String field, String dbFunc, String defaultValue) {
        while (true) {
            String input = withExit(field + " [" + defaultValue + "]: ");
            if (input == null) return null;
            if (input.isEmpty()) return defaultValue;
            if (Validator.isValid(dbFunc, input)) return input;
        }
    }
}