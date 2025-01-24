package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.util.PeekTokenIterator;

public class CallStmt extends Stmt {

    protected CallStmt() {
        super(ASTNodeTypes.CALL_STMT, "call");
    }

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