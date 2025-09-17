package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.PeekTokenIterator;

/**
 * Parses an arrow expression argument enclosed in braces, e.g., {@code { > 1 }}.
 * <p>
 * This node acts primarily as a parser helper to validate and extract the inner expression
 * between '{' and '}'.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class ArrowArgs extends ASTNode {
    /**
     * <p>Constructor for ArrowArgs.</p>
     */
    public ArrowArgs() {
        super();
        this.label = "arrow args";
    }


    /**
     * Parses an arrow argument surrounded by '{' and '}'.
     *
     * @param it the token iterator
     * @return the parsed inner {@link ASTNode}
     * @throws ParseException if the braces are missing or the inner expression is invalid
     */
    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        Token token = it.peek();
        if (!"{".equals(token.getValue())) {
            throw new ParseException(token);
        }
        it.next();
        ASTNode node = Expr.parse(it);
        token = it.peek();
        if (!"}".equals(token.getValue())) {
            throw new ParseException(token);
        }
        it.next();
        return node;
    }
}
