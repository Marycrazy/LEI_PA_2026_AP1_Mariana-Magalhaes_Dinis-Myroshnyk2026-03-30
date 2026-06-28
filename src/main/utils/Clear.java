package main.utils;

/**
 * Utility class providing system-level helper routines to clear the console terminal screen
 * across different operating systems.
 */
public class Clear {
    /**
     * Clears the current command-line terminal window.
     * Attempts to execute the native OS clear commands ({@code cls} on Windows, {@code clear}
     * on UNIX-like environments), falling back to ANSI escape codes if process spawning fails.
     */
    public static void screen() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                new ProcessBuilder("clear").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }
}