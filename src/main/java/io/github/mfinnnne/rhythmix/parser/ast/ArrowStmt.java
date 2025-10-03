package io.github.mfinnnne.rhythmix.parser.ast;

import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.exception.RhythmixException;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;

/**
 * Represents a chained arrow expression in the AST, e.g., {@code {>1} -> {<5} -> {...}}.
 * <p>
 * This node contains one or more child expressions parsed by {@link ArrowArgs},
 * separated by the {@code ->} operator.
 *
 * author MFine
 * @version 1.0
 * @since 1.0
 */
public class ArrowStmt extends Stmt {

    /**
     * <p>Constructor for ArrowStmt.</p>
     */
    protected ArrowStmt() {
        super(ASTNodeTypes.ARROW_EXPR, "arrow expr");
    }


    /**
     * Parses a chained arrow expression consisting of one or more arrow arguments
     * separated by {@code ->}.
     *
     * @param it the token iterator
     * @return the parsed {@link ArrowStmt}
     * @throws ParseException if an inner arrow argument fails to parse or the chain is malformed
     */
    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        ArrowStmt arrowStmt = new ArrowStmt();
        while (it.hasNext()) {
            try {
                ASTNode arg = ArrowArgs.parse(it);
                arrowStmt.addChild(arg);
                if (it.hasNext() && it.nextMatch1("->")) {
                    it.next();
                } else {
                    break;
                }
            }  catch (RhythmixException e) {
                Token contextToken = it.hasNext() ? it.peek() : null;
                throw new ParseException("Arrow statement parsing failed: " + e.getMessage(), contextToken);
            } catch (Exception e) {
                Token contextToken = it.hasNext() ? it.peek() : null;
                throw new ParseException("Arrow statement parsing error: " + e.getMessage(), contextToken);
            }
        }
        return arrowStmt;
    }
}
