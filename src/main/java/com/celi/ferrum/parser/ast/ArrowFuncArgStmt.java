package com.celi.ferrum.parser.ast;

import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.util.PeekTokenIterator;

import java.util.Stack;

/**
 * @Author MFine
 * @Date 2023/12/13
 * @Description
 */

public class ArrowFuncArgStmt extends Stmt {

    /**
     *
     */
    protected ArrowFuncArgStmt() {
        super(ASTNodeTypes.ARROW_FUNC, "arrow function arg");
    }

    /**
     * @param it
     * @return {@link ASTNode}
     * @throws ParseException
     */

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        ArrowFuncArgStmt functionArgs = new ArrowFuncArgStmt();
        while (it.hasNext()) {
            Variable var = (Variable) Factor.parse(it);
            if (var == null) {
                return functionArgs;
            }
            functionArgs.addChild(var);
            if (!it.peek().getValue().equals(")")) {
                it.nextMatch(",");
            }else {
                break;
            }
        }
        return functionArgs;
    }
}
