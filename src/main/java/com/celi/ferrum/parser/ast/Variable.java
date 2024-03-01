package com.celi.ferrum.parser.ast;

import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.util.PeekTokenIterator;

public class Variable extends Factor{
    private Token typeLexeme = null;

    public Variable( PeekTokenIterator it) {
        super( it);
    }

    public Token getTypeLexeme() {
        return typeLexeme;
    }

    public void setTypeLexeme(Token typeLexeme) {
        this.typeLexeme = typeLexeme;
    }
}
