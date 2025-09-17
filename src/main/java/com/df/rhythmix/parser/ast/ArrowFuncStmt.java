package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.PeekTokenIterator;

import java.util.Stack;

/**
 * Represents an anonymous arrow function in the AST, e.g., {@code (a, b) => { ... }}.
 *
 * author MFine
 * date 2023/12/13
 * author MFine
 * @version 1.0
 * @since 1.0
 */
public class ArrowFuncStmt extends Stmt {

    /**
     * <p>Constructor for ArrowFuncStmt.</p>
     */
    protected ArrowFuncStmt() {
        super(ASTNodeTypes.ARROW_FUNC, "Anonymous function");
    }

    /**
     * Parses an arrow function of the form {@code (args) => { ... }}.
     *
     * @param it the token iterator
     * @return the parsed {@link ArrowFuncStmt}
     * @throws ParseException if syntax is invalid
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

    /**
     * Heuristically checks whether the upcoming tokens form an arrow function.
     *
     * @param it the token iterator
     * @return {@code true} if an arrow function pattern is detected; {@code false} otherwise
     */
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
