package io.github.mfinnnne.rhythmix.parser.ast;


import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;

/**
 * Represents an assignment statement in the AST.
 * e.g., {@code a = 1 + 2}
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class AssignStmt extends Stmt {
    /**
     * <p>Constructor for AssignStmt.</p>
     */
    protected AssignStmt() {
        super(ASTNodeTypes.ASSIGN_STMT, "assign");
    }

    /**
     * Parses an assignment statement from the token stream.
     * Expects a variable, followed by an equals sign, followed by an expression.
     *
     * @param it the token iterator.
     * @return an {@link ASTNode} representing the assignment statement.
     * @throws ParseException if the syntax is incorrect.
     */
    public static ASTNode parse( PeekTokenIterator it) throws ParseException {
        AssignStmt assignStmt = new AssignStmt();
        Token tkn = it.peek();
        ASTNode factor = Factor.parse( it);
        if (factor == null || tkn.isScalar()) {
            throw new ParseException(tkn);
        }
        assignStmt.addChild(factor);
        Token token = it.nextMatch("=");
        ASTNode parse = Expr.parse(it);
        assignStmt.addChild(parse);
        assignStmt.setLexeme(token);
        return assignStmt;
    }
}
