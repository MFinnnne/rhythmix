package io.github.mfinnnne.rhythmix.parser.ast;

import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;

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
     * Example: {@code {#temp:<30# && #humidity:>80#}}
     * <p>
     * The parsing process:
     * <ol>
     *   <li>Consume opening {@code {}</li>
     *   <li>Parse first {@link EventSourceCondition}</li>
     *   <li>While logical operators (&&, ||) exist:
     *     <ul>
     *       <li>Parse the operator</li>
     *       <li>Parse the next {@link EventSourceCondition}</li>
     *       <li>Build a binary expression tree</li>
     *     </ul>
     *   </li>
     *   <li>Consume closing {@code }}</li>
     * </ol>
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
        
        // Parse the first event source condition
        EventSourceCondition firstCondition = EventSourceCondition.parse(it);
        
        // Check if there are logical operators
        if (!it.hasNext() || "}".equals(it.peek().getValue())) {
            // Single condition case: {#temp:<30#}
            stmt.addChild(firstCondition);
            
            // Consume closing brace
            if (!"}".equals(it.peek().getValue())) {
                throw new ParseException("Expected '}' at end of multi-source event expression", it.peek());
            }
            it.next(); // consume }
            
            return stmt;
        }
        
        // Multiple conditions with logical operators
        ASTNode currentExpr = firstCondition;
        
        while (it.hasNext() && !"}".equals(it.peek().getValue())) {
            Token operatorToken = it.peek();
            String operator = operatorToken.getValue();
            
            // Check for logical operator
            if (!"&&".equals(operator) && !"||".equals(operator)) {
                throw new ParseException("Expected logical operator (&&, ||) between event source conditions", operatorToken);
            }
            it.next(); // consume operator
            
            // Parse the next event source condition
            EventSourceCondition nextCondition = EventSourceCondition.parse(it);
            
            // Build binary expression tree
            Expr binaryExpr = new Expr(ASTNodeTypes.BINARY_EXPR, operatorToken);
            binaryExpr.addChild(currentExpr);
            binaryExpr.addChild(nextCondition);
            
            currentExpr = binaryExpr;
        }
        
        // Add the final expression tree as child
        stmt.addChild(currentExpr);
        
        // Expect closing brace
        if (!it.hasNext()) {
            throw new ParseException("Expected '}' at end of multi-source event expression");
        }
        if ( !"}".equals(it.peek().getValue())) {
            throw new ParseException("Expected '}' at end of multi-source event expression", it.peek());
        }
        it.next(); // consume }
        
        return stmt;
    }
}

