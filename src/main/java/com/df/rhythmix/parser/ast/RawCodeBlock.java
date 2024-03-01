package com.df.rhythmix.parser.ast;


import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.lexer.TokenType;
import com.df.rhythmix.util.PeekTokenIterator;

import java.util.Stack;

public class RawCodeBlock extends Stmt {
    protected RawCodeBlock() {
        super(ASTNodeTypes.BLOCK, "block");
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        RawCodeBlock rawCodeBlock = new RawCodeBlock();
        it.nextMatch("{");
        Stack<String> stack = new Stack<>();
        stack.push("{");
        StringBuilder rawCode = new StringBuilder();
        while (it.hasNext()) {
            Token next = it.next();
            if ("{".equals(next.getValue())) {
                stack.push(next.getValue());
            }
            if ("}".equals(next.getValue())) {
                if (!stack.isEmpty()) {
                    stack.pop();
                    if (it.hasNext() && it.nextMatch1(")")) {
                        break;
                    }
                }
            }

            rawCode.append(next.getValue());
            if (next.getType() == TokenType.KEYWORD) {
                rawCode.append(" ");
            }
        }
        rawCodeBlock.label = rawCode.toString();
        return rawCodeBlock;
    }
}
