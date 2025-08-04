package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.exception.RhythmixException;
import com.df.rhythmix.lexer.Token;
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
            }  catch (RhythmixException e) {
                Token contextToken = it.hasNext() ? it.peek() : null;
                throw new ParseException("Arrow statement parsing failed: " + e.getMessage(), contextToken);
            } catch (Exception e) {
                Token contextToken = it.hasNext() ? it.peek() : null;
                throw new ParseException("Arrow statement parsing error: " + e.getMessage(), contextToken);
            }
        }
        return arrowStmt;
    }
}
