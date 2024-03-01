package com.celi.ferrum.parser.ast;


import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.util.PeekTokenIterator;

public class AssignStmt extends Stmt {
    protected AssignStmt() {
        super(ASTNodeTypes.ASSIGN_STMT, "assign");
    }

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
