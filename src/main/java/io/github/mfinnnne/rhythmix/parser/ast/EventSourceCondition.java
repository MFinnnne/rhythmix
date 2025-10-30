package io.github.mfinnnne.rhythmix.parser.ast;

import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;

/**
 * Represents a single event source condition in a multi-source event expression.
 * <p>
 * Example: In {@code #temp:<30#}, this node represents the entire condition
 * with "temp" as the event source alias and "<30" as the condition expression.
 * <p>
 * Structure:
 * <ul>
 *   <li>Event source alias: The identifier for the event source (e.g., "temp", "humidity")</li>
 *   <li>Condition expression: The child node containing the actual condition (e.g., "<30", "[20,30]")</li>
 * </ul>
 *
 * @author MFine
 * @version 1.0
 * @since 1.1
 */
public class EventSourceCondition extends ASTNode {

    /**
     * The event source alias (e.g., "temp", "humidity").
     */
    private String eventSourceAlias;

    /**
     * Constructs an EventSourceCondition with the specified alias and lexeme.
     *
     * @param alias  the event source alias
     * @param lexeme the token representing the opening # delimiter
     */
    protected EventSourceCondition(String alias, Token lexeme) {
        super(ASTNodeTypes.COMPARE_EXPR, "event source condition");
        this.eventSourceAlias = alias;
        this.lexeme = lexeme;
    }

    /**
     * Gets the event source alias.
     *
     * @return the event source alias string
     */
    public String getEventSourceAlias() {
        return eventSourceAlias;
    }

    /**
     * Parses a single event source condition from the token stream.
     * <p>
     * Expected format: {@code #alias:condition#}
     * <p>
     * Example: {@code #temp:<30#}
     * <ul>
     *   <li>{@code #} - opening delimiter (already consumed)</li>
     *   <li>{@code temp} - event source alias</li>
     *   <li>{@code :} - separator</li>
     *   <li>{@code <30} - condition expression</li>
     *   <li>{@code #} - closing delimiter</li>
     * </ul>
     *
     * @param it the token iterator (positioned after the opening #)
     * @return the parsed EventSourceCondition node
     * @throws ParseException if the syntax is invalid
     */
    public static EventSourceCondition parse(PeekTokenIterator it) throws ParseException {
        Token openingHash = it.peek();

        // Expect opening #
        if (!"#".equals(it.peek().getValue())) {
            throw new ParseException("Expected '#' at start of event source condition", it.peek());
        }
        it.next(); // consume #

        // Parse event source alias
        Token aliasToken = it.next();
        if (!aliasToken.isVariable()) {
            throw new ParseException("Expected event source alias (variable) after '#'", aliasToken);
        }
        String alias = aliasToken.getValue();

        // Expect colon separator
        if (!":".equals(it.peek().getValue())) {
            throw new ParseException("Expected ':' after event source alias", it.peek());
        }
        it.next(); // consume :

        // Create the EventSourceCondition node
        EventSourceCondition condition = new EventSourceCondition(alias, openingHash);

        // Parse the condition expression until closing #
        ASTNode conditionExpr = parseConditionUntilHash(it);
        condition.addChild(conditionExpr);

        // Expect closing #
        if (!it.hasNext() || !"#".equals(it.peek().getValue())) {
            if (it.hasNext()) {
                throw new ParseException("Expected closing '#' after condition expression", it.peek());
            }
            throw new ParseException("Expected closing '#' after condition expression");
        }
        it.next(); // consume closing #

        return condition;
    }

    /**
     * Parses the condition expression until the closing # delimiter.
     * <p>
     * This method collects all tokens until it finds a # at depth 0 (not inside brackets/braces).
     * It handles nested expressions like ranges {@code [20,30]}, chains {@code >20 && <30},
     * and other complex conditions.
     *
     * @param it the token iterator
     * @return the parsed condition expression as an ASTNode
     * @throws ParseException if parsing fails
     */
    private static ASTNode parseConditionUntilHash(PeekTokenIterator it) throws ParseException {
        // We need to collect tokens until we find the closing # at depth 0
        // Depth tracking is needed to handle nested brackets/braces in conditions

        int depth = 0;
        it.record(); // Start recording position

        // Scan ahead to find the closing #
        while (it.hasNext()) {
            Token token = it.peek();
            String value = token.getValue();

            // Track depth for brackets and braces
            if ("{".equals(value) || "[".equals(value) || "(".equals(value)) {
                depth++;
            } else if ("}".equals(value) || "]".equals(value) || ")".equals(value)) {
                depth--;
            } else if ("#".equals(value) && depth == 0) {
                // Found closing # at depth 0, stop here
                break;
            }

            it.next(); // consume token
        }

        // Now backtrack and parse the expression properly
        it.backRecord();

        // Parse the condition expression using Expr.parse
        // This will handle all expression types: compare, range, chain, binary, etc.
        ASTNode conditionExpr = Expr.parse(it);

        return conditionExpr;
    }
}

