package main.utils;

import java.util.Scanner;

/**
 * Utility containing console workflow blockers to pause program execution until explicit user acknowledgement.
 */
public class PressKey {
    /**
     * Suspends program flow output and prompts the console listener to wait until the ENTER key is pressed.
     */
    public static void enter() {
        Scanner scanner = Input.getScanner();
        System.out.print("\nPress ENTER to continue...");

        if (scanner.hasNextLine()) scanner.nextLine();
    }
}