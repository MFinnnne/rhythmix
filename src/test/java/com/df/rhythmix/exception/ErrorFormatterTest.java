package com.df.rhythmix.exception;

import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.lexer.TokenType;
import org.junit.jupiter.api.Test;

/**
 * Test class to demonstrate the new error formatting capabilities.
 * Shows how exceptions with position information can be formatted in Rust-style.
 */
public class ErrorFormatterTest {

    @Test
    public void testLexicalExceptionFormatting() {
        String sourceCode = "let x = 5;\nlet y = @invalid;\nlet z = 10;";
        
        // Create a token with position information
        Token errorToken = new Token(TokenType.OPERATOR, "@", 21, 21, 2, 8);
        
        // Create exception with position info
        LexicalException exception = new LexicalException("Unexpected character '@'", errorToken);
        
        // Format the error
        String formattedError = ErrorFormatter.formatError(exception, sourceCode, "test.rhythmix");
        
        System.out.println("=== Lexical Exception Formatting ===");
        System.out.println(formattedError);
        System.out.println();
    }

    @Test
    public void testParseExceptionFormatting() {
        String sourceCode = "function add(a, b) {\n    return a + b\n}";
        
        // Create a token representing missing semicolon
        Token errorToken = new Token(TokenType.OPERATOR, "}", 35, 35, 3, 0);
        
        // Create exception with position info
        ParseException exception = new ParseException("Expected ';' before '}'", errorToken);
        
        // Format the error
        String formattedError = ErrorFormatter.formatError(exception, sourceCode, "calculator.rhythmix");
        
        System.out.println("=== Parse Exception Formatting ===");
        System.out.println(formattedError);
        System.out.println();
    }

    @Test
    public void testTypeInferExceptionFormatting() {
        String sourceCode = "let x: int = \"hello\";\nlet y = x + 5;";
        
        // Create a token representing the string literal
        Token errorToken = new Token(TokenType.STRING, "\"hello\"", 13, 19, 1, 13);
        
        // Create exception with position info
        TypeInferException exception = new TypeInferException("Cannot assign string to int variable", errorToken);
        
        // Format the error
        String formattedError = ErrorFormatter.formatError(exception, sourceCode, "types.rhythmix");
        
        System.out.println("=== Type Inference Exception Formatting ===");
        System.out.println(formattedError);
        System.out.println();
    }

    @Test
    public void testComputeExceptionFormatting() {
        String sourceCode = "let x = 10;\nlet y = x / 0;\nprint(y);";
        
        // Create a token representing the division by zero
        Token errorToken = new Token(TokenType.INTEGER, "0", 21, 21, 2, 10);
        
        // Create exception with position info
        ComputeException exception = new ComputeException("Division by zero", errorToken);
        
        // Format the error
        String formattedError = ErrorFormatter.formatError(exception, sourceCode, "math.rhythmix");
        
        System.out.println("=== Compute Exception Formatting ===");
        System.out.println(formattedError);
        System.out.println();
    }

    @Test
    public void testTranslatorExceptionFormatting() {
        String sourceCode = "let x = 5;\nundefined_function();\nlet y = 10;";
        
        // Create a token representing the undefined function
        Token errorToken = new Token(TokenType.VARIABLE, "undefined_function", 11, 28, 2, 0);
        
        // Create exception with position info
        TranslatorException exception = new TranslatorException("Undefined function 'undefined_function'", errorToken);
        
        // Format the error
        String formattedError = ErrorFormatter.formatError(exception, sourceCode, "main.rhythmix");
        
        System.out.println("=== Translator Exception Formatting ===");
        System.out.println(formattedError);
        System.out.println();
    }

    @Test
    public void testMultiLineErrorFormatting() {
        String sourceCode = "function fibonacci(n) {\n" +
                           "    if (n <= 1) {\n" +
                           "        return n;\n" +
                           "    }\n" +
                           "    return fibonacci(n-1) + fibonacci(n-2);\n" +
                           "}\n" +
                           "\n" +
                           "let result = fibonacci(\"not a number\");\n" +
                           "print(result);";
        
        // Create a token representing the invalid argument
        Token errorToken = new Token(TokenType.STRING, "\"not a number\"", 134, 147, 8, 23);
        
        // Create exception with position info
        TypeInferException exception = new TypeInferException("Expected numeric type, found string", errorToken);
        
        // Format the error
        String formattedError = ErrorFormatter.formatError(exception, sourceCode, "fibonacci.rhythmix");
        
        System.out.println("=== Multi-line Error Formatting ===");
        System.out.println(formattedError);
        System.out.println();
    }

    @Test
    public void testExceptionWithoutPositionInfo() {
        String sourceCode = "let x = 5;";
        
        // Create exception without position info
        LexicalException exception = new LexicalException("Generic error without position");
        
        // Format the error
        String formattedError = ErrorFormatter.formatError(exception, sourceCode);
        
        System.out.println("=== Exception Without Position Info ===");
        System.out.println(formattedError);
        System.out.println();
    }
}
