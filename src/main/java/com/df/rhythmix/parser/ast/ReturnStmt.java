package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.util.PeekTokenIterator;


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
