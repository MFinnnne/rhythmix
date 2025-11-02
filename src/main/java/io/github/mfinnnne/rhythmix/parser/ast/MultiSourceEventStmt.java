package io.github.mfinnnne.rhythmix.parser.ast;

import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;
import io.github.mfinnnne.rhythmix.util.PriorityTable;

import java.util.Arrays;

/**
 * Represents a multi-source event expression in the AST.
 * <p>
 * Example: {@code {#temp:<30# && #humidity:>80#}}
 * <p>
 * This statement combines conditions from multiple event sources using logical operators (&&, ||).
 * The structure is a binary tree where:
 * <ul>
 *   <li>Leaf nodes are {@link EventSourceCondition} instances</li>
 *   <li>Internal nodes are binary expressions (&&, ||) combining conditions</li>
 * </ul>
 *
 * @author MFine
 * @version 1.0
 * @since 1.1
 */
public class MultiSourceEventStmt extends Stmt {

    /**
     * Priority table with only logical operators for multi-source event expressions.
     * <p>
     * Precedence (lower index = lower precedence, evaluated first):
     * <ul>
     *   <li>Level 0: || (OR) - lower precedence</li>
     *   <li>Level 1: && (AND) - higher precedence</li>
     * </ul>
     * <p>
     * This ensures that {@code a || b && c} is parsed as {@code a || (b && c)}.
     */
    private static final PriorityTable MULTI_SOURCE_PRIORITY_TABLE = new PriorityTable(
        Arrays.asList("||"),  // Level 0: OR (lower precedence)
        Arrays.asList("&&")   // Level 1: AND (higher precedence)
    );

    /**
     * Constructs a MultiSourceEventStmt.
     */
    protected MultiSourceEventStmt() {
        super(ASTNodeTypes.MULTI_SOURCE_EVENT_EXPR, "multi-source event expr");
    }

    /**
     * Detects whether the upcoming tokens represent a multi-source event expression.
     * <p>
     * A multi-source event expression starts with {@code {#} pattern.
     * This method uses lookahead to check without consuming tokens.
     *
     * @param it the token iterator
     * @return {@code true} if the pattern matches {@code {#}; {@code false} otherwise
     */
    public static boolean isMultiSourceEvent(PeekTokenIterator it) {
        try {
            it.record();
            
            // Check for opening brace
            if (!"{".equals(it.peek().getValue())) {
                return false;
            }
            it.next(); // consume {
            
            // Check for hash delimiter
            if (!it.hasNext()) {
                return false;
            }
            
            if ("#".equals(it.peek().getValue())) {
                return true;
            }
            
            return false;
        } finally {
            it.backRecord();
        }
    }

    /**
     * Parses a multi-source event expression from the token stream.
     * <p>
     * Expected format: {@code {#alias:condition# logicalOp #alias:condition# ...}}
     * <p>
     * Examples:
     * <ul>
     *   <li>{@code {#temp:<30# && #humidity:>80#}} - simple AND</li>
     *   <li>{@code {#a:>1# || #b:>2# && #c:>3#}} - precedence: {@code a || (b && c)}</li>
     *   <li>{@code {(#a:>1# || #b:>2#) && #c:>3#}} - parentheses: {@code (a || b) && c}</li>
     * </ul>
     * <p>
     * The parsing process:
     * <ol>
     *   <li>Consume opening {@code {}</li>
     *   <li>Use Pratt parser to parse the logical expression with proper precedence</li>
     *   <li>Validate that the expression contains only EventSourceCondition nodes and logical operators</li>
     *   <li>Consume closing {@code }}</li>
     * </ol>
     * <p>
     * Operator precedence (AND has higher precedence than OR):
     * <ul>
     *   <li>{@code &&} - higher precedence (evaluated first)</li>
     *   <li>{@code ||} - lower precedence</li>
     * </ul>
     *
     * @param it the token iterator
     * @return the parsed MultiSourceEventStmt
     * @throws ParseException if the syntax is invalid
     */
    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        MultiSourceEventStmt stmt = new MultiSourceEventStmt();

        // Expect opening brace
        if (!"{".equals(it.peek().getValue())) {
            throw new ParseException("Expected '{' at start of multi-source event expression", it.peek());
        }
        Token openingBrace = it.next(); // consume {
        stmt.lexeme = openingBrace;

        // Parse the logical expression using Pratt parser with custom priority table
        // This handles precedence (AND before OR) and parentheses automatically
        ASTNode logicalExpr = Expr.parse(it, MULTI_SOURCE_PRIORITY_TABLE);

        // Validate that the expression contains only EventSourceCondition nodes
        // and logical operators (&&, ||)
        validateMultiSourceExpression(logicalExpr);

        stmt.addChild(logicalExpr);

        // Expect closing brace
        if (!it.hasNext()) {
            throw new ParseException("Expected '}' at end of multi-source event expression");
        }
        if (!"}".equals(it.peek().getValue())) {
            throw new ParseException("Expected '}' at end of multi-source event expression", it.peek());
        }
        it.next(); // consume }

        return stmt;
    }

    /**
     * Validates that the expression tree contains only EventSourceCondition nodes
     * and logical operators (&&, ||).
     * <p>
     * This ensures that multi-source event expressions don't contain invalid constructs
     * like arithmetic operations, comparisons outside of event source conditions, etc.
     *
     * @param node the AST node to validate
     * @throws ParseException if the node contains invalid constructs
     */
    private static void validateMultiSourceExpression(ASTNode node) throws ParseException {
        if (node == null) {
            return;
        }

        ASTNodeTypes type = node.getType();

        // Allow EventSourceCondition (leaf nodes)
        if (type == ASTNodeTypes.COMPARE_EXPR && node instanceof EventSourceCondition) {
            return; // Valid leaf node
        }

        // Allow BINARY_EXPR with && or || operators
        if (type == ASTNodeTypes.BINARY_EXPR) {
            String operator = node.getLexeme().getValue();
            if (!"&&".equals(operator) && !"||".equals(operator)) {
                throw new ParseException(
                    "Invalid operator '" + operator + "' in multi-source event expression. " +
                    "Only && and || are allowed.", node.getLexeme());
            }
            // Recursively validate children
            for (ASTNode child : node.getChildren()) {
                validateMultiSourceExpression(child);
            }
            return;
        }

        // Invalid node type
        throw new ParseException(
            "Invalid expression in multi-source event. " +
            "Only event source conditions (#alias:condition#) and logical operators (&&, ||) are allowed.",
            node.getLexeme());
    }
}

