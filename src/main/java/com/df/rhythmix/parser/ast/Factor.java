package com.df.rhythmix.parser.ast;

import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.lexer.TokenType;
import com.df.rhythmix.util.PeekTokenIterator;

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