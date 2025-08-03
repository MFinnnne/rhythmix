package com.df.rhythmix.exception;

/**
 * Utility class for formatting exceptions with position information in a Rust-like style.
 * Provides detailed error messages with source code snippets and position indicators.
 * 
 * @author MFine
 * @version 1.0
 */
public class ErrorFormatter {

    /**
     * Interface for exceptions that have position information
     */
    public interface PositionAware {
        boolean hasPositionInfo();
        int getCharacterPosition();
        int getLine();
        int getColumn();
        String getMessage();
    }

    /**
     * Formats an exception with position information in Rust-style error format.
     * 
     * @param exception The exception with position information
     * @param sourceCode The original source code
     * @param filename Optional filename to display (can be null)
     * @return Formatted error message with source code snippet and position indicators
     */
    public static String formatError(PositionAware exception, String sourceCode, String filename) {
        if (!exception.hasPositionInfo() || sourceCode == null) {
            return exception.getMessage();
        }

        StringBuilder result = new StringBuilder();
        String[] lines = sourceCode.split("\n");
        int errorLine = exception.getLine();
        int errorColumn = exception.getColumn();
        
        // Error header
        String errorType = exception.getClass().getSimpleName().replace("Exception", "").toLowerCase();
        result.append(String.format("error[%s]: %s\n", errorType, exception.getMessage()));
        
        // File location
        String location = filename != null ? filename : "<source>";
        result.append(String.format("  --> %s:%d:%d\n", location, errorLine, errorColumn));
        result.append("   |\n");
        
        // Show context lines
        int contextBefore = 1;
        int contextAfter = 1;
        int startLine = Math.max(0, errorLine - contextBefore - 1);
        int endLine = Math.min(lines.length - 1, errorLine + contextAfter - 1);
        
        // Calculate line number width for alignment
        int maxLineNum = endLine + 1;
        int lineNumWidth = String.valueOf(maxLineNum).length();
        
        // Show lines before error
        for (int i = startLine; i < errorLine - 1; i++) {
            result.append(String.format("%" + lineNumWidth + "d | %s\n", i + 1, lines[i]));
        }
        
        // Show error line
        if (errorLine - 1 < lines.length) {
            String errorLineContent = lines[errorLine - 1];
            result.append(String.format("%" + lineNumWidth + "d | %s\n", errorLine, errorLineContent));
            
            // Show pointer line
            StringBuilder pointer = new StringBuilder();
            for (int i = 0; i < lineNumWidth; i++) {
                pointer.append(" ");
            }
            pointer.append(" | ");
            
            // Add spaces up to error column
            for (int i = 0; i < errorColumn; i++) {
                pointer.append(" ");
            }
            pointer.append("^");
            
            // Add additional indicators if needed
            String errorMsg = getShortErrorMessage(exception.getMessage());
            if (errorMsg != null && !errorMsg.isEmpty()) {
                pointer.append(" ").append(errorMsg);
            }
            
            result.append(pointer.toString()).append("\n");
        }
        
        // Show lines after error
        for (int i = errorLine; i <= endLine; i++) {
            if (i < lines.length) {
                result.append(String.format("%" + lineNumWidth + "d | %s\n", i + 1, lines[i]));
            }
        }
        
        return result.toString();
    }

    /**
     * Formats an exception with position information using default filename.
     */
    public static String formatError(PositionAware exception, String sourceCode) {
        return formatError(exception, sourceCode, null);
    }

    /**
     * Formats a TranslatorException in Rust-style.
     */
    public static String formatError(TranslatorException exception, String sourceCode, String filename) {
        return formatError(new PositionAwareAdapter(exception), sourceCode, filename);
    }

    /**
     * Formats a TranslatorException in Rust-style with default filename.
     */
    public static String formatError(TranslatorException exception, String sourceCode) {
        return formatError(exception, sourceCode, null);
    }

    /**
     * Formats a LexicalException in Rust-style.
     */
    public static String formatError(LexicalException exception, String sourceCode, String filename) {
        return formatError(new LexicalExceptionAdapter(exception), sourceCode, filename);
    }

    /**
     * Formats a LexicalException in Rust-style with default filename.
     */
    public static String formatError(LexicalException exception, String sourceCode) {
        return formatError(exception, sourceCode, null);
    }

    /**
     * Formats a ParseException in Rust-style.
     */
    public static String formatError(ParseException exception, String sourceCode, String filename) {
        return formatError(new ParseExceptionAdapter(exception), sourceCode, filename);
    }

    /**
     * Formats a ParseException in Rust-style with default filename.
     */
    public static String formatError(ParseException exception, String sourceCode) {
        return formatError(exception, sourceCode, null);
    }

    /**
     * Formats a ComputeException in Rust-style.
     */
    public static String formatError(ComputeException exception, String sourceCode, String filename) {
        return formatError(new ComputeExceptionAdapter(exception), sourceCode, filename);
    }

    /**
     * Formats a ComputeException in Rust-style with default filename.
     */
    public static String formatError(ComputeException exception, String sourceCode) {
        return formatError(exception, sourceCode, null);
    }

    /**
     * Formats a TypeInferException in Rust-style.
     */
    public static String formatError(TypeInferException exception, String sourceCode, String filename) {
        return formatError(new TypeInferExceptionAdapter(exception), sourceCode, filename);
    }

    /**
     * Formats a TypeInferException in Rust-style with default filename.
     */
    public static String formatError(TypeInferException exception, String sourceCode) {
        return formatError(exception, sourceCode, null);
    }

    /**
     * Extracts a short error message for the pointer line.
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
        
        // Truncate if too long
        if (msg.length() > 50) {
            msg = msg.substring(0, 47) + "...";
        }
        
        return msg;
    }

    // Adapter classes to make existing exceptions compatible with PositionAware interface
    
    private static class PositionAwareAdapter implements PositionAware {
        private final TranslatorException exception;
        
        public PositionAwareAdapter(TranslatorException exception) {
            this.exception = exception;
        }
        
        @Override
        public boolean hasPositionInfo() {
            return exception.hasPositionInfo();
        }
        
        @Override
        public int getCharacterPosition() {
            return exception.getCharacterPosition();
        }
        
        @Override
        public int getLine() {
            return exception.getLine();
        }
        
        @Override
        public int getColumn() {
            return exception.getColumn();
        }
        
        @Override
        public String getMessage() {
            return exception.toString();
        }
    }

    private static class LexicalExceptionAdapter implements PositionAware {
        private final LexicalException exception;
        
        public LexicalExceptionAdapter(LexicalException exception) {
            this.exception = exception;
        }
        
        @Override
        public boolean hasPositionInfo() {
            return exception.hasPositionInfo();
        }
        
        @Override
        public int getCharacterPosition() {
            return exception.getCharacterPosition();
        }
        
        @Override
        public int getLine() {
            return exception.getLine();
        }
        
        @Override
        public int getColumn() {
            return exception.getColumn();
        }
        
        @Override
        public String getMessage() {
            return exception.toString();
        }
    }

    private static class ParseExceptionAdapter implements PositionAware {
        private final ParseException exception;
        
        public ParseExceptionAdapter(ParseException exception) {
            this.exception = exception;
        }
        
        @Override
        public boolean hasPositionInfo() {
            return exception.hasPositionInfo();
        }
        
        @Override
        public int getCharacterPosition() {
            return exception.getCharacterPosition();
        }
        
        @Override
        public int getLine() {
            return exception.getLine();
        }
        
        @Override
        public int getColumn() {
            return exception.getColumn();
        }
        
        @Override
        public String getMessage() {
            return exception.getMessage();
        }
    }

    private static class ComputeExceptionAdapter implements PositionAware {
        private final ComputeException exception;
        
        public ComputeExceptionAdapter(ComputeException exception) {
            this.exception = exception;
        }
        
        @Override
        public boolean hasPositionInfo() {
            return exception.hasPositionInfo();
        }
        
        @Override
        public int getCharacterPosition() {
            return exception.getCharacterPosition();
        }
        
        @Override
        public int getLine() {
            return exception.getLine();
        }
        
        @Override
        public int getColumn() {
            return exception.getColumn();
        }
        
        @Override
        public String getMessage() {
            return exception.getMessage();
        }
    }

    private static class TypeInferExceptionAdapter implements PositionAware {
        private final TypeInferException exception;
        
        public TypeInferExceptionAdapter(TypeInferException exception) {
            this.exception = exception;
        }
        
        @Override
        public boolean hasPositionInfo() {
            return exception.hasPositionInfo();
        }
        
        @Override
        public int getCharacterPosition() {
            return exception.getCharacterPosition();
        }
        
        @Override
        public int getLine() {
            return exception.getLine();
        }
        
        @Override
        public int getColumn() {
            return exception.getColumn();
        }
        
        @Override
        public String getMessage() {
            return exception.toString();
        }
    }
}
