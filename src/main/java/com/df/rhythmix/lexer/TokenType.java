package com.df.rhythmix.lexer;

/**
 * Enumeration of token types used in the Rhythmix language.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public enum TokenType {
    /**
     * A reserved keyword, such as 'if', 'let', 'func'.
     */
    KEYWORD("keyword"),
    /**
     * An identifier for a variable.
     */
    VARIABLE("var"),
    /**
     * An operator, such as '+', '-', '==', '->'.
     */
    OPERATOR("operator"),
    /**
     * A bracket or parenthesis, such as '(', ')', '{', '}'.
     */
    BRACKET("bracket"),
    /**
     * A string literal, enclosed in quotes.
     */
    STRING("string"),
    /**
     * A floating-point number literal.
     */
    FLOAT("float"),
    /**
     * A boolean literal, 'true' or 'false'.
     */
    BOOLEAN("boolean"),
    /**
     * An integer literal.
     */
    INTEGER("integer");

    private String title;

    TokenType(String title) {
        this.title = title;
    }

    /**
     * Gets the descriptive title of the token type.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTitle() {
        return title;
    }
}
