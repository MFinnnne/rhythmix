package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.util.PeekTokenIterator;

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
