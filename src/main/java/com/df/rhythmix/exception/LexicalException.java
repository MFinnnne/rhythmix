package com.df.rhythmix.exception;

import com.df.rhythmix.lexer.Token;

/**
 * @author MFine
 * @version 1.0
 * @date 2021/7/5 0:31
 **/
public class LexicalException extends Exception {

    private String msg;
    private int characterPosition = -1;
    private int line = -1;
    private int column = -1;

    public LexicalException(char c) {
        msg = String.format("Unexpected character %c", c);
    }

    public LexicalException(String msg) {
        this.msg = msg;
    }

    public LexicalException(String s, String unit) {
        msg = String.format(s, unit);
    }

    public LexicalException(String msg, Token token) {
        this.msg = msg;
        if (token != null && token.hasPositionInfo()) {
            this.characterPosition = token.getStartPosition();
            this.line = token.getLine();
            this.column = token.getColumn();
        }
    }

    public LexicalException(char c, Token token) {
        this.msg = String.format("Unexpected character %c", c);
        if (token != null && token.hasPositionInfo()) {
            this.characterPosition = token.getStartPosition();
            this.line = token.getLine();
            this.column = token.getColumn();
        }
    }

    public LexicalException(String msg, int characterPosition, int line, int column) {
        this.msg = msg;
        this.characterPosition = characterPosition;
        this.line = line;
        this.column = column;
    }

    public int getCharacterPosition() {
        return characterPosition;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public boolean hasPositionInfo() {
        return characterPosition >= 0 && line >= 0 && column >= 0;
    }

    @Override
    public String toString() {
        if (hasPositionInfo()) {
            return String.format("Lexical error at position %d (line %d, column %d): %s",
                characterPosition + 1, line, column, this.msg);
        }
        return this.msg;
    }
}