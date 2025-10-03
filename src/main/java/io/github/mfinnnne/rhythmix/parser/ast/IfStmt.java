package io.github.mfinnnne.rhythmix.parser.ast;


import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;


/**
 * Represents an 'if' statement in the AST, including optional 'else' and 'else if' clauses.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class IfStmt extends Stmt {
    /**
     * <p>Constructor for IfStmt.</p>
     */
    protected IfStmt() {
        super( ASTNodeTypes.IF_STMT, "if");
    }

    /**
     * Parses an 'if' statement, along with any 'else' or 'else if' clauses.
     *
     * @param it a {@link PeekTokenIterator} object.
     * @return an {@link ASTNode} representing the full conditional statement.
     * @throws ParseException if the syntax is incorrect.
     */
    public static ASTNode parse( PeekTokenIterator it) throws ParseException {
        Token token = it.nextMatch("if");
        it.nextMatch("(");
        IfStmt ifStmt = new IfStmt();
        ifStmt.setLexeme(token);
        ASTNode expr = Expr.parse(it);
        ifStmt.addChild(expr);
        it.nextMatch(")");
        ASTNode block = Block.parse( it);
        ifStmt.addChild(block);
        ASTNode tail = tail(it);
        if (tail != null) {
            ifStmt.addChild(tail);
        }
        return ifStmt;
    }

    /**
     * Parses the tail of an 'if' statement, which can be an 'else' block or an 'else if' statement.
     *
     * @param it a {@link PeekTokenIterator} object.
     * @return an {@link ASTNode} for the 'else' or 'else if' part, or {@code null} if there is none.
     * @throws ParseException if the syntax is incorrect.
     */
    public static ASTNode tail( PeekTokenIterator it) throws ParseException {
        if (!it.hasNext() || !it.peek().getValue().equals("else")) {
            return null;
        }
        it.nextMatch("else");
        Token lookahead = it.peek();
        if (lookahead.getValue().equals("{")) {
            return Block.parse( it);
        }
        if (lookahead.getValue().equals("if")){
            return parse( it);
        }
        return null;
    }
}
