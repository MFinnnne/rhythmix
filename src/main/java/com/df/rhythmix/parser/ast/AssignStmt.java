package com.df.rhythmix.parser.ast;


import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.PeekTokenIterator;

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
