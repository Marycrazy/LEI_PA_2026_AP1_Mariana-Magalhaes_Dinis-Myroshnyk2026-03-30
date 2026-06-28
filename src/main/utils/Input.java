package main.utils;

import java.util.Scanner;

/**
 * Core command-line terminal capturing engine that manages scanner input routines and
 * handles exit sequences.
 */
public class Input {
    private static Scanner scanner;

    /**
     * Lazily instantiates and returns a single unified system input scanner stream reference.
     *
     * @return the singleton system input console reader {@link Scanner}
     */
    public static Scanner getScanner() {
        if (scanner == null) scanner = new Scanner(System.in);
        return scanner;
    }

    /**
     * Displays a prompt asking the terminal user for entry data while routing standard back-exit commands.
     *
     * @param prompt query question print string displayed to the console user
     * @return user input line entry value string; or {@code null} if the cancel sequence ':q' is triggered
     */
    public static String withExit(String prompt) {
        System.out.print("(type ':q' to exit) ");
        System.out.print(prompt);
        String input = getScanner().nextLine();
        if (input.equals(":q")) return null;
        return input;
    }

    /**
     * Captures a mandatory entry from the console stream, keeping the prompt open until content is valid.
     *
     * @param field name of the properties target printed to contextualize input queries
     * @return a non-blank token input; or {@code null} if exited
     */
    public static String getInput(String field) {
        while (true) {
            String input = withExit(field + ": ");
            if (input == null) return null;
            if (Validator.isValid(input)) return input;
        }
    }

    /**
     * Captures a terminal console string entry evaluated against a specific database validation rule.
     *
     * @param field  name of the property target printed to contextualize input queries
     * @param dbFunc database lookup validation routine context key label string
     * @return verified input text value; or {@code null} if cancelled
     */
    public static String getInput(String field, String dbFunc) {
        while (true) {
            String input = withExit(field + ": ");
            if (input == null) return null;
            if (Validator.isValid(dbFunc, input)) return input;
        }
    }

    /**
     * Interactively updates an existing system record property value, prefilled with a default value fallback option.
     *
     * @param field        the name tag label of the entry block field
     * @param defaultValue baseline value text injected if user keeps property unchanged by entering blank text
     * @param edit         flag denoting modification context state status
     * @return updated string data field entry; or {@code null} if execution is aborted via exit token
     */
    public static String getInput(String field, String defaultValue, boolean edit) {
        String input = withExit(field + " [" + defaultValue + "]: ");
        if (input == null) return null;
        return input.isEmpty() ? defaultValue : input;
    }

    /**
     * Overloaded field modifier entry processor executing dual baseline fallback settings and detailed database checks.
     *
     * @param field        the name tag label of the entry block field
     * @param dbFunc       database storage evaluation criteria validation method reference parameter
     * @param defaultValue default baseline text fallback if the entry field is left blank
     * @return validated input string data entry; or {@code null} if the cancel sequence is matched
     */
    public static String getInput(String field, String dbFunc, String defaultValue) {
        while (true) {
            String input = withExit(field + " [" + defaultValue + "]: ");
            if (input == null) return null;
            if (input.isEmpty()) return defaultValue;
            if (Validator.isValid(dbFunc, input)) return input;
        }
    }
}