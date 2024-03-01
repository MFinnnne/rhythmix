package com.celi.ferrum.exception;

/**
 * @author MFine
 * @version 1.0
 * @date 2021/7/5 0:31
 **/
public class LexicalException extends Exception {

    private String msg;

    public LexicalException(char c) {
        msg = String.format("Unexpected character %c", c);
    }

    public LexicalException(String msg) {
        this.msg = msg;
    }

    public LexicalException(String s, String unit) {
        msg = String.format(s, unit);
    }

    @Override
    public String toString() {
        return this.msg;
    }
}