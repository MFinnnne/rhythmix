package com.df.rhythmix.parser.ast;

/**
 * Enumeration of the different types of nodes in the Abstract Syntax Tree (AST).
 * <p>
 * Each type corresponds to a specific grammatical construct in the Rhythmix language.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public enum ASTNodeTypes {
    /**
     * A block of statements, typically enclosed in curly braces.
     */
    BLOCK,
    /**
     * A binary expression, involving two operands and an operator (e.g., a + b).
     */
    BINARY_EXPR,
    /**
     * A chain expression, used for sequential data processing (e.g., filter().sum()).
     */
    CHAIN_EXPR,

    /**
     * A range expression, defining a numeric range (e.g., (1, 10)).
     */
    RANGE_EXPR,

    /**
     * A comparison expression (e.g., > 10).
     */
    COMPARE_EXPR,

    /**
     * An arrow expression, defining a sequence of states (e.g., {>1}->{<5}).
     */
    ARROW_EXPR,


    /**
     * An anonymous function (lambda expression).
     */
    ARROW_FUNC,

    /**
     * A unary expression, with a single operand and an operator (e.g., ++a, !b).
     */
    UNARY_EXPR,
    /**
     * A variable identifier.
     */
    VARIABLE,
    /**
     * A scalar literal value (e.g., number, string, boolean).
     */
    SCALAR,
    /**
     * An 'if' statement, for conditional execution.
     */
    IF_STMT,
    /**
     * A 'while' loop statement.
     */
    WHILE_STMT,
    /**
     * A 'for' loop statement.
     */
    FOR_STMT,
    /**
     * An assignment statement (e.g., a = 5).
     */
    ASSIGN_STMT,
    /**
     * A function declaration statement.
     */
    FUNCTION_DECLARE_STMT,

    /**
     * A variable declaration statement (e.g., let a = 5).
     */
    DECLARE_STMT,

    /**
     * A 'return' statement from a function.
     */
    RETURN_STMT,
    /**
     * A function call statement.
     */
    CALL_STMT,

    /**
     * The root node of the program's AST.
     */
    PROGRAM;
}
