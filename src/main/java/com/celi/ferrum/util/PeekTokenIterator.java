package com.celi.ferrum.util;


import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.lexer.PeekIterator;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.lexer.TokenType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author MFine
 * @version 1.0
 **/
public class PeekTokenIterator extends PeekIterator<Token> {

    public PeekTokenIterator(Stream<Token> stream) {
        super(stream);
    }

    public Token nextMatch(String value) throws ParseException {
        Token token = this.next();
        if (!token.getValue().equals(value)) {
            throw new ParseException(token);

        }
        return token;
    }

    public boolean nextMatch1(String value) {
        Token token = this.peek();
        return token.getValue().equals(value);
    }



    public boolean nextMatchContain(String... value) {
        List<String> res = Arrays.asList(value);
        if (this.hasNext()) {
            Token token = this.peek();
            return res.contains(token.getValue());
        }
        return false;
    }

    public Token nextMatch(TokenType type) throws ParseException {
        Token token = this.next();
        if (!token.getType().equals(type)) {
            throw new ParseException(token);
        }
        return token;
    }
}