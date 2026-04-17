package main.utils;

public class Text {
    public static String wrap(String text, int maxWidth) {
        String[] lines = text.split("\n");
        StringBuilder wrapped = new StringBuilder();

        for (String line : lines) {
            appendWrappedLine(wrapped, line, maxWidth);
        }

        trimTrailingNewline(wrapped);
        return wrapped.toString();
    }

    private static void appendWrappedLine(StringBuilder wrapped, String line, int maxWidth) {
        if (line.isEmpty()) {
            wrapped.append("\n");
            return;
        }

        String[] words = line.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            currentLine = appendWord(wrapped, currentLine, word, maxWidth);
        }

        if (currentLine.length() > 0) {
            wrapped.append(currentLine).append("\n");
        }
    }

    private static StringBuilder appendWord(StringBuilder wrapped, StringBuilder currentLine, String word, int maxWidth) {
        if (word.length() > maxWidth) {
            flushCurrentLine(wrapped, currentLine);
            appendLongWord(wrapped, word, maxWidth);
            return new StringBuilder();
        }

        if (fitsOnCurrentLine(currentLine, word, maxWidth)) {
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        } else {
            wrapped.append(currentLine).append("\n");
            currentLine = new StringBuilder(word);
        }

        return currentLine;
    }

    private static void flushCurrentLine(StringBuilder wrapped, StringBuilder currentLine) {
        if (currentLine.length() > 0) {
            wrapped.append(currentLine).append("\n");
        }
    }

    private static void appendLongWord(StringBuilder wrapped, String word, int maxWidth) {
        for (int i = 0; i < word.length(); i += maxWidth) {
            int end = Math.min(i + maxWidth, word.length());
            wrapped.append(word.substring(i, end)).append("\n");
        }
    }

    private static boolean fitsOnCurrentLine(StringBuilder currentLine, String word, int maxWidth) {
        int spaceNeeded = currentLine.length() + word.length() + (currentLine.length() > 0 ? 1 : 0);
        return spaceNeeded <= maxWidth;
    }

    private static void trimTrailingNewline(StringBuilder sb) {
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
            sb.setLength(sb.length() - 1);
        }
    }
}