package com.df.rhythmix.exception;

import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.lexer.TokenType;
import org.junit.jupiter.api.Test;

/**
 * Simple test to demonstrate the enhanced exception classes with position information.
 */
public class SimpleErrorTest {

    @Test
    public void testEnhancedExceptionClasses() {
        System.out.println("=== Testing Enhanced Exception Classes ===\n");
        
        // Test LexicalException with position info
        testLexicalException();
        
        // Test ParseException with position info
        testParseException();
        
        // Test ComputeException with position info
        testComputeException();
        
        // Test TypeInferException with position info
        testTypeInferException();
        
        // Test TranslatorException with position info
        testTranslatorException();
    }
    
    private void testLexicalException() {
        System.out.println("1. LexicalException with position information:");
        
        // Create a token with position information
        Token errorToken = new Token(TokenType.OPERATOR, "@", 21, 21, 2, 8);
        
        // Create exception with position info
        LexicalException exception = new LexicalException("Unexpected character '@'", errorToken);
        
        System.out.println("   Exception: " + exception.toString());
        System.out.println("   Has position info: " + exception.hasPositionInfo());
        if (exception.hasPositionInfo()) {
            System.out.println("   Position: " + exception.getCharacterPosition());
            System.out.println("   Line: " + exception.getLine());
            System.out.println("   Column: " + exception.getColumn());
        }
        System.out.println();
    }
    
    private void testParseException() {
        System.out.println("2. ParseException with position information:");
        
        // Create a token representing missing semicolon
        Token errorToken = new Token(TokenType.OPERATOR, "}", 35, 35, 3, 0);
        
        // Create exception with position info using the existing constructor
        ParseException exception = new ParseException("Expected ';' before", errorToken);
        
        System.out.println("   Exception: " + exception.getMessage());
        System.out.println("   Has position info: " + exception.hasPositionInfo());
        if (exception.hasPositionInfo()) {
            System.out.println("   Position: " + exception.getCharacterPosition());
            System.out.println("   Line: " + exception.getLine());
            System.out.println("   Column: " + exception.getColumn());
        }
        System.out.println();
    }
    
    private void testComputeException() {
        System.out.println("3. ComputeException with position information:");
        
        // Create a token representing the division by zero
        Token errorToken = new Token(TokenType.INTEGER, "0", 21, 21, 2, 10);
        
        // Create exception with position info
        ComputeException exception = new ComputeException("Division by zero", errorToken);
        
        System.out.println("   Exception: " + exception.getMessage());
        System.out.println("   Has position info: " + exception.hasPositionInfo());
        if (exception.hasPositionInfo()) {
            System.out.println("   Position: " + exception.getCharacterPosition());
            System.out.println("   Line: " + exception.getLine());
            System.out.println("   Column: " + exception.getColumn());
        }
        System.out.println();
    }
    
    private void testTypeInferException() {
        System.out.println("4. TypeInferException with position information:");
        
        // Create a token representing the string literal
        Token errorToken = new Token(TokenType.STRING, "\"hello\"", 13, 19, 1, 13);
        
        // Create exception with position info
        TypeInferException exception = new TypeInferException("Cannot assign string to int variable", errorToken);
        
        System.out.println("   Exception: " + exception.toString());
        System.out.println("   Has position info: " + exception.hasPositionInfo());
        if (exception.hasPositionInfo()) {
            System.out.println("   Position: " + exception.getCharacterPosition());
            System.out.println("   Line: " + exception.getLine());
            System.out.println("   Column: " + exception.getColumn());
        }
        System.out.println();
    }
    
    private void testTranslatorException() {
        System.out.println("5. TranslatorException with position information:");
        
        // Create a token representing the undefined function
        Token errorToken = new Token(TokenType.VARIABLE, "undefined_function", 11, 28, 2, 0);
        
        // Create exception with position info
        TranslatorException exception = new TranslatorException("Undefined function 'undefined_function'", errorToken);
        
        System.out.println("   Exception: " + exception.toString());
        System.out.println("   Has position info: " + exception.hasPositionInfo());
        if (exception.hasPositionInfo()) {
            System.out.println("   Position: " + exception.getCharacterPosition());
            System.out.println("   Line: " + exception.getLine());
            System.out.println("   Column: " + exception.getColumn());
        }
        System.out.println();
    }
}
