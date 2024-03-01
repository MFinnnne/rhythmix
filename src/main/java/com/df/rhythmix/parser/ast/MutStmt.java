package com.df.rhythmix.parser.ast;


import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.translate.MutExpr;
import com.df.rhythmix.util.PeekTokenIterator;

import java.util.ArrayList;
import java.util.List;

public class MutStmt extends Stmt {

    protected MutStmt() {
        super(ASTNodeTypes.ARROW_EXPR, "arrow expr");
    }


    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        if (isMutStmt(it)) {
            List<Token> tokens = new ArrayList<>();
            while (it.hasNext()) {
                if (">".equals(it.peek().getValue())) {
                    tokens.add(it.next());
                    break;
                }
                tokens.add(it.next());
            }
            try {
                List<Token> tks = MutExpr.translateMutExpr(tokens);
                return ArrowStmt.parse(new PeekTokenIterator(tks.stream()));
            } catch (TranslatorException e) {
                throw new ParseException(e.getMessage());
            }
        }
        return null;
    }

    public static boolean isMutStmt(PeekTokenIterator it) {
        try {
            it.record();
            if (!it.nextMatch1("<")) {
                return false;
            }
            it.next();
            if (!it.next().isScalar()) {
                return false;
            }
            while (it.hasNext() && !">".equals(it.peek().getValue())) {
                if (!",".equals(it.next().getValue())) {
                    return false;
                }
                if (!it.next().isScalar()) {
                    return false;
                }
                if (">".equals(it.next().getValue())) {
                    return true;
                }
            }
        } finally {
            it.backRecord();
        }
        return false;
    }
}
