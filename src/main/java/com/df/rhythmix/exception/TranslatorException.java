package com.df.rhythmix.exception;

import cn.hutool.core.util.StrUtil;
import com.df.rhythmix.lexer.Token;

import java.util.List;

/**
 * @author MFine
 * @version 1.0
 * @date 2021/7/5 0:31
 **/
public class TranslatorException extends RhythmixException {

    public TranslatorException(char c) {
        super(String.format("Unexpected token %c", c));
    }

    public TranslatorException(String msg) {
        super(msg);
    }

    public TranslatorException(CharSequence template, Object... params) {
        super(template, params);
    }

    public TranslatorException(CharSequence template, List<Token> tokens) {
        super(template, tokens);
    }

    public TranslatorException(String msg, Token token) {
        super(msg, token);
    }

    public TranslatorException(CharSequence template, Token token, Object... params) {
        super(template, token, params);
    }

    public TranslatorException(String msg, int characterPosition, int line, int column) {
        super(msg, characterPosition, line, column);
    }

    @Override
    public String getExceptionType() {
        return "translation";
    }
}