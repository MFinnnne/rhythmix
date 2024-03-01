package com.celi.ferrum.parser.ast;

import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.util.PeekTokenIterator;

public class ArrowStmt extends Stmt {

    protected ArrowStmt() {
        super(ASTNodeTypes.ARROW_EXPR, "arrow expr");
    }


    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        ArrowStmt arrowStmt = new ArrowStmt();
        while (it.hasNext()) {
            try {
                ASTNode arg = ArrowArgs.parse(it);
                arrowStmt.addChild(arg);
                if (it.hasNext() && it.nextMatch1("->")) {
                    it.next();
                } else {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new ParseException(e.getMessage());
            }
        }
        return arrowStmt;
    }
}
