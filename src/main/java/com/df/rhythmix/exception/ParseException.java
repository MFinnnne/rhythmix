/*
 * @Author: MFine
 * @Date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-02-12 00:52:45
 * @LastEditors: MFine
 * @Description:
 */
package com.df.rhythmix.exception;


import cn.hutool.core.util.StrUtil;
import com.df.rhythmix.lexer.Token;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ParseException extends Exception {
    private final String msg;
    private int characterPosition = -1;
    private int line = -1;
    private int column = -1;

    public ParseException(String msg) {
        this.msg = msg;
    }

    public ParseException(Token token) {
        this.msg = String.format("Syntax error, unexpected character %s", token.getValue());
        if (token != null && token.hasPositionInfo()) {
            this.characterPosition = token.getStartPosition();
            this.line = token.getLine();
            this.column = token.getColumn();
        }
    }

    public ParseException(String ch, Token token) {
        this.msg = String.format("Syntax error, unexpected character \"%s\", expected \"%s\" ", token.getValue(), ch);
        if (token != null && token.hasPositionInfo()) {
            this.characterPosition = token.getStartPosition();
            this.line = token.getLine();
            this.column = token.getColumn();
        }
    }

    public ParseException(CharSequence template, Token... tokens) {
        this.msg = StrUtil.format(template, String.join("", Arrays.stream(tokens).map(Token::getValue).collect(Collectors.joining())));
        // Extract position info from the first token if available
        if (tokens.length > 0 && tokens[0] != null && tokens[0].hasPositionInfo()) {
            this.characterPosition = tokens[0].getStartPosition();
            this.line = tokens[0].getLine();
            this.column = tokens[0].getColumn();
        }
    }



    public ParseException(String msg, int characterPosition, int line, int column) {
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
    public String getMessage() {
        if (hasPositionInfo()) {
            return String.format("Parse error at position %d (line %d, column %d): %s",
                characterPosition + 1, line, column, this.msg);
        }
        return this.msg;
    }

    @Override
    public String toString() {
        return getMessage();
    }
}