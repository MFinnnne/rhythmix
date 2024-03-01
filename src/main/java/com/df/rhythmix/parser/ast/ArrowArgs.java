package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.PeekTokenIterator;

public class ArrowArgs extends ASTNode {
    public ArrowArgs() {
        super();
        this.label = "arrow args";
    }


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
