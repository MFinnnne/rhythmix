package io.github.mfinnnne.rhythmix.parser.ast;

import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;

/**
 * Abstract base class for all statement nodes in the AST.
 * <p>
 * Statements represent actions or declarations, such as assignments, function calls,
 * or control flow constructs. This class provides the main entry point for parsing any statement.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class Stmt extends ASTNode {

    /**
     * <p>Constructor for Stmt.</p>
     *
     * @param types a {@link ASTNodeTypes} object.
     * @param label a {@link java.lang.String} object.
     */
    protected Stmt(ASTNodeTypes types, String label) {
        super(types, label);
    }

    /**
     * Parses the next statement from the token stream.
     * <p>
     * This method acts as a dispatcher, looking at the next tokens to decide which
     * specific statement parser to invoke (e.g., {@link AssignStmt}, {@link IfStmt}).
     *
     * @param it a {@link PeekTokenIterator} object.
     * @return a {@link ASTNode} object representing the parsed statement, or {@code null} if no statement is found.
     * @throws ParseException if a syntax error occurs.
     */
    public static ASTNode parseStmt(PeekTokenIterator it) throws ParseException {
        Token token = it.next();
        Token lookahead = it.peek();
        it.putBack();

        if (token.isVariable() && "=".equals(lookahead.getValue())) {
            return AssignStmt.parse(it);
        } else if ("let".equals(token.getValue())) {
            return DeclareStmt.parse(it);
        } else if ("func".equals(token.getValue())) {
            return FunctionDefineStmt.parse(it);
        } else if ("return".equals(token.getValue())) {
            return ReturnStmt.parse(it);
        } else if ("if".equals(token.getValue())) {
            return IfStmt.parse(it);
        } else if (token.isVariable() && "(".equals(lookahead.getValue())) {
            return  Expr.parse(it);
        }
        return null;
    }


}
