package io.github.mfinnnne.rhythmix.exception;

/**
 * Utility class that formats {@link RhythmixException} instances with position information
 * in a Rust-style, including source code context and caret indicators.
 * <p>
 * The formatter renders a short, user-friendly error message with optional filename and
 * surrounding source lines to aid debugging.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class ErrorFormatter {

    /**
     * Formats a RhythmixException with position information in Rust-style error format.
     *
     * @param exception The RhythmixException with position information
     * @param sourceCode The original source code
     * @param filename Optional filename to display (can be null)
     * @return Formatted error message with source code snippet and position indicators
     */
    public static String formatError(RhythmixException exception, String sourceCode, String filename) {
        if (!exception.hasPositionInfo() || sourceCode == null) {
            return exception.getMessage();
        }

        StringBuilder result = new StringBuilder();
        String[] lines = sourceCode.split("\n");
        int errorLine = exception.getLine();
        int errorColumn = exception.getColumn();

        result.append(String.format(" %s", exception.getMessage()));

        String location = filename != null ? filename : "<source>";
        result.append(String.format("  --> %s:%d:%d\n", location, errorLine, errorColumn));
        result.append("   |\n");

        int contextBefore = 1;
        int contextAfter = 1;
        int startLine = Math.max(0, errorLine - contextBefore - 1);
        int endLine = Math.min(lines.length - 1, errorLine + contextAfter - 1);

        int maxLineNum = endLine + 1;
        int lineNumWidth = String.valueOf(maxLineNum).length();

        for (int i = startLine; i < errorLine - 1; i++) {
            result.append(String.format("%" + lineNumWidth + "d | %s\n", i + 1, lines[i]));
        }

        if (errorLine - 1 < lines.length) {
            String errorLineContent = lines[errorLine - 1];
            result.append(String.format("%" + lineNumWidth + "d | %s\n", errorLine, errorLineContent));

            StringBuilder pointer = new StringBuilder();
            pointer.append(" ".repeat(lineNumWidth));
            pointer.append(" | ");

            pointer.append(" ".repeat(Math.max(0, errorColumn-1)));
            pointer.append("^");

            String errorMsg = getShortErrorMessage(exception.getMessage());
            if (!errorMsg.isEmpty()) {
                pointer.append(" ").append(errorMsg);
            }

            result.append(pointer).append("\n");
        }

        for (int i = errorLine; i <= endLine; i++) {
            if (i < lines.length) {
                result.append(String.format("%" + lineNumWidth + "d | %s\n", i + 1, lines[i]));
            }
        }

        return result.toString();
    }

    /**
     * Formats a RhythmixException with position information using default filename.
     *
     * @param exception a {@link RhythmixException} object.
     * @param sourceCode a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String formatError(RhythmixException exception, String sourceCode) {
        return formatError(exception, sourceCode, null);
    }

    /**
     * Extracts a concise error message for the pointer line.
     *
     * @param fullMessage the full error message to shorten; may be {@code null}
     * @return a shortened, single-line message suitable for inline display; never {@code null}
     */
    private static String getShortErrorMessage(String fullMessage) {
        if (fullMessage == null) return "";

        // Extract the core error message, removing position information
        String msg = fullMessage;
        if (msg.contains(": ")) {
            String[] parts = msg.split(": ", 2);
            if (parts.length > 1) {
                msg = parts[1];
            }
        }

        if (msg.length() > 100) {
            msg = msg.substring(0, 47) + "...";
        }

        return msg;
    }
}
