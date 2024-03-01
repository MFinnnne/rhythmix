package com.df.rhythmix.parser.ast;

import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.PeekTokenIterator;

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
