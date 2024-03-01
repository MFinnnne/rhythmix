package com.celi.ferrum.parser.ast;

import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.util.PeekTokenIterator;

public class CallStmt extends Stmt {

    protected CallStmt() {
        super(ASTNodeTypes.CALL_STMT, "call");
    }

    public static ASTNode parse( PeekTokenIterator it) throws ParseException {
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
            }
        }
        it.nextMatch(")");
        return callStmt;
    }
}