package com.celi.ferrum.parser.ast;


import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.util.PeekTokenIterator;

public class DeclareStmt extends Stmt {
    protected DeclareStmt() {
        super(ASTNodeTypes.DECLARE_STMT, "declare");
    }


    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        DeclareStmt declareStmt = new DeclareStmt();
        it.nextMatch("let");
        Token tkn = it.peek();
        ASTNode factor = Factor.parse( it);
        if (factor == null) {
            throw new ParseException(tkn);
        }
        declareStmt.addChild(factor);
        Token token = it.nextMatch("=");
        ASTNode parse = Expr.parse(it);
        declareStmt.addChild(parse);
        declareStmt.setLexeme(token);
        return declareStmt;
    }
}
