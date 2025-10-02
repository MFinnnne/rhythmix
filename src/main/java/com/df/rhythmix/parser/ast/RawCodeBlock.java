package com.df.rhythmix.parser.ast;


import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.lexer.TokenType;
import com.df.rhythmix.util.PeekTokenIterator;

import java.util.Stack;

/**
 * Represents a raw code block captured as a string label inside braces.
 * <p>
 * This is primarily used for arrow-function bodies where the contents are not
 * parsed into structured AST nodes but preserved as raw text.
 *
 * author MFine
 * @version 1.0
 * @since 1.0
 */
public class RawCodeBlock extends Stmt {
    /**
     * <p>Constructor for RawCodeBlock.</p>
     */
    protected RawCodeBlock() {
        super(ASTNodeTypes.BLOCK, "block");
    }

    /**
     * Parses a raw code block delimited by '{' and '}', capturing its content as text.
     *
     * @param it the token iterator
     * @return the parsed {@link RawCodeBlock}
     * @throws ParseException if braces are unbalanced or malformed
     */
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
