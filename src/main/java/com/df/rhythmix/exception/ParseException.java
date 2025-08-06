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

public class ParseException extends RhythmixException {

    public ParseException(String msg) {
        super(msg);
    }

    public ParseException(Token token) {
        super(String.format("Syntax error, unexpected character %s", token.getValue()), token);
    }

    public ParseException(String ch, Token token) {
        super(String.format("Syntax error, unexpected character \"%s\", expected \"%s\" ", token.getValue(), ch), token);
    }

    public ParseException(CharSequence template, Token... tokens) {
        super(StrUtil.format(template, String.join("", Arrays.stream(tokens).map(Token::getValue).collect(Collectors.joining()))));
        // Extract position info from the first token if available
        if (tokens.length > 0 && tokens[0] != null && tokens[0].hasPositionInfo()) {
            setPositionInfo(tokens[0]);
        }
    }

    public ParseException(String msg, int characterPosition, int line, int column) {
        super(msg, characterPosition, line, column);
    }

    @Override
    public String getExceptionType() {
        return "parse";
    }
}