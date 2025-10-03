package io.github.mfinnnne.rhythmix.lexer;

/**
 * Enumeration of the different types of expressions supported by Rhythmix.
 * <p>
 * This enum is used to identify the type of an expression during parsing and translation.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public enum ExprTypeEnum {
    /**
     * Represents a range expression, e.g., {@code (1, 10)}.
     */
    RANGE,
    /**
     * Represents a comparison expression, e.g., {@code > 10}.
     */
    COMPARE,
    /**
     * Represents a mutation expression (state transition), e.g., {@code <0,1>}.
     */
    MUT,
    /**
     * Represents an arrow expression for defining sequential logic, e.g., {@code {>1}->{<5}}.
     */
    ARROW,
}
