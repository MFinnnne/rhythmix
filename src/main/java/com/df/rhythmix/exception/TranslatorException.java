package com.df.rhythmix.exception;

import cn.hutool.core.util.StrUtil;
import com.df.rhythmix.lexer.Token;

import java.util.List;

/**
 * @author MFine
 * @version 1.0
 * @date 2021/7/5 0:31
 **/
public class TranslatorException extends Exception {

    private String msg;
    private int characterPosition = -1;
    private int line = -1;
    private int column = -1;

    public TranslatorException(char c) {
        msg = String.format("Unexpected token %c", c);
    }

    public TranslatorException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public TranslatorException(CharSequence template,Object... params) {
        this.msg = StrUtil.format(template, params);
    }

    public TranslatorException(CharSequence template, List<Token> tokens) {
        this.msg = StrUtil.format(template, String.join("",tokens.stream().map(Token::getValue).toArray(String[]::new)));
    }

    public TranslatorException(String msg, Token token) {
        this.msg = msg;
        if (token != null && token.hasPositionInfo()) {
            this.characterPosition = token.getStartPosition();
            this.line = token.getLine();
            this.column = token.getColumn();
        }
    }

    public TranslatorException(CharSequence template, Token token, Object... params) {
        this.msg = StrUtil.format(template, params);
        if (token != null && token.hasPositionInfo()) {
            this.characterPosition = token.getStartPosition();
            this.line = token.getLine();
            this.column = token.getColumn();
        }
    }

    public TranslatorException(String msg, int characterPosition, int line, int column) {
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
            return String.format("Translation error at position %d (line %d, column %d): %s",
                characterPosition + 1, line, column, this.msg);
        }
        return this.msg;
    }
}