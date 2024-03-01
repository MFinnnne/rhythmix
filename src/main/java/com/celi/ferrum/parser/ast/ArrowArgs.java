package com.celi.ferrum.parser.ast;

import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.util.PeekTokenIterator;

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
