package io.github.mfinnnne.rhythmix.parser.ast;

import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.lexer.TokenType;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;

/**
 * Represents a factor in an expression, which is the most basic element.
 * A factor can be a variable or a scalar value.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class Factor extends ASTNode {
    /**
     * <p>Constructor for Factor.</p>
     *
     * @param it a {@link PeekTokenIterator} object.
     */
    protected Factor(PeekTokenIterator it) {
        super();
        Token next = it.next();
        TokenType type = next.getType();
        if (type == TokenType.VARIABLE) {
            this.type = ASTNodeTypes.VARIABLE;
        } else {
            this.type = ASTNodeTypes.SCALAR;
        }
        this.label = next.getValue();
        this.lexeme = next;

    }

    /**
     * Parses a factor from the token stream.
     * It checks if the next token is a variable, scalar, or event source condition
     * and creates the corresponding node.
     *
     * @param it the token iterator.
     * @return an {@link ASTNode} for the factor, or {@code null} if no factor can be parsed.
     * @throws ParseException if parsing an event source condition fails.
     */
    public static ASTNode parse(PeekTokenIterator it) throws ParseException {

        Token next = it.peek();

        // Check for event source condition (#alias:condition#)
        if (EventSourceCondition.isEventSourceCondition(it)) {
            return EventSourceCondition.parse(it);
        }

        if (next.isVariable()) {
            return new Variable(it);
        } else if (next.isScalar()) {
            return new Scalar(it);
        }
        return null;
    }
}
