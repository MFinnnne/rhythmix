package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.util.PeekTokenIterator;

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
