# Enhanced Error Reporting with Position Information

This document describes the enhanced error reporting system that has been added to the Rhythmix project, providing Rust-style error formatting with precise position information.

## Overview

All exception classes in the `com.df.rhythmix.exception` package have been enhanced to include position information (line, column, character position) and support for Rust-style error formatting that shows the exact location of errors in the source code.

## Enhanced Exception Classes

### 1. LexicalException
- **Added position fields**: `characterPosition`, `line`, `column`
- **New constructors**:
  - `LexicalException(String msg, Token token)` - Extracts position from token
  - `LexicalException(char c, Token token)` - For character-based errors with position
  - `LexicalException(String msg, int characterPosition, int line, int column)` - Explicit position
- **New methods**:
  - `hasPositionInfo()` - Check if position information is available
  - `getCharacterPosition()`, `getLine()`, `getColumn()` - Access position data
- **Enhanced toString()** - Includes position information when available

### 2. ParseException
- **Added position fields**: `characterPosition`, `line`, `column`
- **Enhanced existing constructors** to extract position from Token parameters
- **New constructors**:
  - `ParseException(String msg, int characterPosition, int line, int column)` - Explicit position
- **New methods**: Same position-related methods as LexicalException
- **Enhanced getMessage()** - Includes position information when available

### 3. ComputeException
- **Added position fields**: `characterPosition`, `line`, `column`
- **New constructors**:
  - `ComputeException(String msg, Token token)` - Extracts position from token
  - `ComputeException(String msg, int characterPosition, int line, int column)` - Explicit position
- **New methods**: Same position-related methods as other exceptions
- **Enhanced getMessage()** - Includes position information when available

### 4. TypeInferException
- **Added position fields**: `characterPosition`, `line`, `column`
- **Enhanced existing constructors** to extract position from Token parameters
- **New constructors**:
  - `TypeInferException(String msg, Token token)` - Extracts position from token
  - `TypeInferException(CharSequence template, Token token, Object... params)` - Template with token
  - `TypeInferException(String msg, int characterPosition, int line, int column)` - Explicit position
- **New methods**: Same position-related methods as other exceptions
- **Enhanced toString()** - Includes position information when available

### 5. TranslatorException
- **Already had position information** - Used as the reference implementation
- **No changes needed** - Already includes all required functionality

## ErrorFormatter Utility Class

A new `ErrorFormatter` class provides Rust-style error formatting that displays:

### Features
- **Source code snippets** with line numbers
- **Precise error pointers** (^) indicating the exact column where the error occurred
- **Context lines** showing code before and after the error
- **Formatted error messages** similar to Rust compiler output

### Usage Examples

```java
// Basic usage
String sourceCode = "let x = 5;\nlet y = @invalid;\nlet z = 10;";
Token errorToken = new Token(TokenType.OPERATOR, "@", 19, 19, 2, 8);
LexicalException exception = new LexicalException("Unexpected character '@'", errorToken);

// Generate Rust-style error report
String formattedError = ErrorFormatter.formatError(exception, sourceCode, "test.rhythmix");
System.out.println(formattedError);
```

### Output Format
```
error[lexical]: Unexpected character '@'
  --> test.rhythmix:2:8
   |
1 | let x = 5;
2 | let y = @invalid;
  |         ^ Unexpected character '@'
3 | let z = 10;
```

## Benefits

### 1. Precise Error Location
- **Character-level precision** - Know exactly where in the source code the error occurred
- **Line and column information** - Easy navigation to error location in editors
- **Context preservation** - Maintain connection between tokens and source positions

### 2. Enhanced Developer Experience
- **Visual error indication** - See exactly where the problem is in the code
- **Context awareness** - View surrounding code for better understanding
- **Consistent formatting** - Uniform error presentation across all exception types

### 3. Better Debugging
- **Source code display** - No need to manually locate error positions
- **Multi-line support** - Handle errors in complex, multi-line expressions
- **IDE integration ready** - Position information can be used by IDEs for navigation

## Integration Examples

### In Lexer
```java
try {
    // Lexical analysis
    Token token = lexer.nextToken();
} catch (LexicalException e) {
    if (e.hasPositionInfo()) {
        String rustStyleError = ErrorFormatter.formatError(e, sourceCode, filename);
        System.err.println(rustStyleError);
    } else {
        System.err.println(e.toString());
    }
}
```

### In Parser
```java
try {
    // Parsing
    ASTNode ast = parser.parse();
} catch (ParseException e) {
    String formattedError = ErrorFormatter.formatError(e, sourceCode, filename);
    throw new CompilerException(formattedError);
}
```

### In Type Checker
```java
if (!isCompatibleType(expectedType, actualType)) {
    throw new TypeInferException("Type mismatch: expected {} but got {}", 
                                errorToken, expectedType, actualType);
}
```

## Testing

Three test classes demonstrate the functionality (located in `/src/test/java/com/df/rhythmix/exception/`):

1. **ErrorFormatterTest** - JUnit tests for the ErrorFormatter utility class
2. **SimpleErrorTest** - Shows basic position information functionality
3. **RustStyleErrorDemo** - Demonstrates Rust-style error formatting

Run the tests:
```bash
# Run all exception tests
mvn test -Dtest="com.df.rhythmix.exception.*"

# Run specific tests
mvn test -Dtest=SimpleErrorTest
mvn test -Dtest=ErrorFormatterTest
```

## Future Enhancements

1. **Multi-error reporting** - Show multiple errors in a single report
2. **Syntax highlighting** - Add color coding to error output
3. **Error recovery suggestions** - Provide hints for fixing common errors
4. **IDE plugin integration** - Create plugins that use this error information
5. **Error categorization** - Group related errors and show fix suggestions

## Conclusion

The enhanced error reporting system provides a significant improvement in developer experience by offering precise error location information and Rust-style formatting. This makes debugging and error resolution much more efficient and user-friendly.
