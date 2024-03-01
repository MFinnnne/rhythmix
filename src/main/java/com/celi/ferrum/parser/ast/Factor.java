package com.celi.ferrum.parser.ast;

import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.lexer.TokenType;
import com.celi.ferrum.util.PeekTokenIterator;

public class Factor extends ASTNode {
    protected Factor(PeekTokenIterator it) {
        super();
        Token next = it.next();
        TokenType type = next.getType();
        if (type == TokenType.VARIABLE) {
            this.type = ASTNodeTypes.VARIABLE;
        } else {
            this.type = ASTNodeTypes.SCALAR;
        }
        this.label = next.getValue();
        this.lexeme = next;

    }

    public static ASTNode parse(PeekTokenIterator it) {

        Token next = it.peek();
        if (next.isVariable()) {
            return new Variable(it);
        } else if (next.isScalar()) {
            return new Scalar(it);
        }
        return null;
    }
}