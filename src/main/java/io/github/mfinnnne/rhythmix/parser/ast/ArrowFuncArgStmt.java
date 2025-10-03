package io.github.mfinnnne.rhythmix.parser.ast;

import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;

/**
 * Parses the argument list for an arrow (anonymous) function, e.g., {@code (a, b)}.
 * <p>
 * This node collects variables until a closing parenthesis ')'.
 *
 * author MFine
 * date 2023/12/13
 * author MFine
 * @version 1.0
 * @since 1.0
 */
public class ArrowFuncArgStmt extends Stmt {

    /**
     * <p>Constructor for ArrowFuncArgStmt.</p>
     */
    protected ArrowFuncArgStmt() {
        super(ASTNodeTypes.ARROW_FUNC, "arrow function arg");
    }

    /**
     * Parses a comma-separated list of variable identifiers until ')'.
     *
     * @param it the token iterator
     * @return the populated {@link ArrowFuncArgStmt}
     * @throws ParseException if a variable is malformed
     */
    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        ArrowFuncArgStmt functionArgs = new ArrowFuncArgStmt();
        while (it.hasNext()) {
            Variable var = (Variable) Factor.parse(it);
            if (var == null) {
                return functionArgs;
            }
            functionArgs.addChild(var);
            if (!it.peek().getValue().equals(")")) {
                it.nextMatch(",");
            }else {
                break;
            }
        }
        return functionArgs;
    }
}
