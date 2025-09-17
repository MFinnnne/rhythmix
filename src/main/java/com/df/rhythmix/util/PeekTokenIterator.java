package com.df.rhythmix.util;


import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.PeekIterator;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.lexer.TokenType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * <p>PeekTokenIterator class.</p>
 *
 * author MFine
 * version 1.0
 */
public class PeekTokenIterator extends PeekIterator<Token> {

    /**
     * <p>Constructor for PeekTokenIterator.</p>
     *
     * @param stream a {@link java.util.stream.Stream} object.
     */
    public PeekTokenIterator(Stream<Token> stream) {
        super(stream);
    }

    /**
     * <p>nextMatch.</p>
     *
     * @param value a {@link java.lang.String} object.
     * @return a {@link com.df.rhythmix.lexer.Token} object.
     * @throws com.df.rhythmix.exception.ParseException if any.
     */
    public Token nextMatch(String value) throws ParseException {
        Token token = this.next();
        if (!token.getValue().equals(value)) {
            throw new ParseException(value,token);

        }
        return token;
    }

    /**
     * <p>nextMatch1.</p>
     *
     * @param value a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean nextMatch1(String value) {
        Token token = this.peek();
        return token.getValue().equals(value);
    }



    /**
     * <p>nextMatchContain.</p>
     *
     * @param value a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean nextMatchContain(String... value) {
        List<String> res = Arrays.asList(value);
        if (this.hasNext()) {
            Token token = this.peek();
            return res.contains(token.getValue());
        }
        return false;
    }

    /**
     * <p>nextMatch.</p>
     *
     * @param type a {@link com.df.rhythmix.lexer.TokenType} object.
     * @return a {@link com.df.rhythmix.lexer.Token} object.
     * @throws com.df.rhythmix.exception.ParseException if any.
     */
    public Token nextMatch(TokenType type) throws ParseException {
        Token token = this.next();
        if (!token.getType().equals(type)) {
            throw new ParseException(token);
        }
        return token;
    }
}
