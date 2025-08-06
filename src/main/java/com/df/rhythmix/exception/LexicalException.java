package com.df.rhythmix.exception;

import com.df.rhythmix.lexer.Token;

/**
 * @author MFine
 * @version 1.0
 * @date 2021/7/5 0:31
 **/
public class LexicalException extends RhythmixException {

    public LexicalException(char c) {
        super(String.format("Unexpected character %c", c));
    }

    public LexicalException(String msg) {
        super(msg);
    }

    public LexicalException(String s, String unit) {
        super(String.format(s, unit));
    }

    public LexicalException(String msg, Token token) {
        super(msg, token);
    }

    public LexicalException(char c, Token token) {
        super(String.format("Unexpected character %c", c), token);
    }

    public LexicalException(String msg, int characterPosition, int line, int column) {
        super(msg, characterPosition, line, column);
    }

    @Override
    public String getExceptionType() {
        return "lexical";
    }
}