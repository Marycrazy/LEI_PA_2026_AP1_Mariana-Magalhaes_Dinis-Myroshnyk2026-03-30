package main.utils;

import java.util.Scanner;

public class Input {
    private static Scanner scanner;

    public static Scanner getScanner() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
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
            String input = Input.withExit(field + ": ");
            if (input == null) return null;
            if (Validator.isValid(input)) return input;
        }
    }

    public static String getInput(String field, String dbFunc) {
        while (true) {
            String input = Input.withExit(field + ": ");
            if (input == null) return null;
            if (Validator.isValid(dbFunc, input)) return input;
        }
    }
}