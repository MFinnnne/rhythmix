package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.PeekTokenIterator;

import java.util.Stack;

/**
 * @Author MFine
 * @Date 2023/12/13
 * @Description
 */

public class ArrowFuncStmt extends Stmt {

    /**
     *
     */
    protected ArrowFuncStmt() {
        super(ASTNodeTypes.ARROW_FUNC, "Anonymous function");
    }

    /**
     * @param it
     * @return {@link ASTNode}
     * @throws ParseException
     */
    public static ASTNode parse(PeekTokenIterator it) throws ParseException {

        ArrowFuncStmt afStmt = new ArrowFuncStmt();
        it.nextMatch("(");
        ASTNode arg = ArrowFuncArgStmt.parse(it);
        afStmt.addChild(arg);
        it.nextMatch(")");
        it.nextMatch("=>");
        ASTNode rawCode = RawCodeBlock.parse(it);
        afStmt.addChild(rawCode);
        return afStmt;
    }

    public static boolean isArrowFunc(PeekTokenIterator it) {
        try {
            it.record();
            Token next = it.next();
            Stack<String> stack = new Stack<>();
            if (!"(".equals(next.getValue())) {
                return false;
            }
            stack.push(next.getValue());
            while (it.hasNext()) {
                next = it.next();
                if ("(".equals(next.getValue())) {
                    stack.push(next.getValue());
                    continue;
                }
                if (")".equals(next.getValue())) {
                    if (!stack.isEmpty()) {
                        stack.pop();
                        next = it.next();
                        if (!it.hasNext()) {
                            return false;
                        }
                        return "=>".equals(next.getValue());
                    } else {
                        return false;
                    }
                }
            }
            return false;
        } finally {
            it.backRecord();
        }
    }

}
