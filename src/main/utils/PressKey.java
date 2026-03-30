package main.utils;

import java.util.Scanner;

public class PressKey {
    public static void enter() {
        Scanner scanner = Input.getScanner();
        System.out.print("\nPress ENTER to continue...");

        if (scanner.hasNextLine()) scanner.nextLine();
    }
}