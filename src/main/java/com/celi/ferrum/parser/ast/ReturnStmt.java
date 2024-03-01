package com.celi.ferrum.parser.ast;

import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.util.PeekTokenIterator;


public class ReturnStmt extends Stmt {

    protected ReturnStmt() {
        super(ASTNodeTypes.RETURN_STMT,"return");
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        it.nextMatch("return");

        ReturnStmt returnStmt = new ReturnStmt();
        ASTNode parse = Expr.parse(it);
        returnStmt.addChild(parse);
        return returnStmt;
    }

}
