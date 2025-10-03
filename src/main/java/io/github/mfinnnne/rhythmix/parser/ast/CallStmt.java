package io.github.mfinnnne.rhythmix.parser.ast;

import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;

/**
 * Represents a function call statement in the AST.
 * e.g., {@code myFunc(arg1, arg2)}
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class CallStmt extends Stmt {

    /**
     * <p>Constructor for CallStmt.</p>
     */
    protected CallStmt() {
        super(ASTNodeTypes.CALL_STMT, "call");
    }

    /**
     * Parses a function call statement from the token stream.
     * Expects an opening parenthesis '(', followed by a comma-separated list of argument expressions,
     * and a closing parenthesis ')'.
     *
     * @param it a {@link PeekTokenIterator} object.
     * @return a {@link ASTNode} object representing the function call.
     * @throws ParseException if the syntax is incorrect.
     */
    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        CallStmt callStmt = new CallStmt();
        it.nextMatch("(");
        while (it.hasNext()) {
            if (it.nextMatch1(")")) {
                it.next();
                return callStmt;
            }
            ASTNode node = Expr.parse(it);
            callStmt.addChild(node);
            if (it.hasNext() && ")".equals(it.peek().getValue())) {
                break;
            }
            if (it.hasNext() && ",".equals(it.peek().getValue())) {
                it.next();
                continue;
            }
            if (it.hasNext() && it.peek().isValue()) {
                continue;
            }
            break;
        }
        it.nextMatch(")");
        return callStmt;
    }
}
