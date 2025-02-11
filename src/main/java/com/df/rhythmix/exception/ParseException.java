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

    public ParseException(String msg) {
        this.msg = msg;
    }

    public ParseException(Token token) {
        this.msg = String.format("Syntax error, unexpected character %s", token.getValue());
    }

    public ParseException(String ch, Token token) {
        this.msg = String.format("Syntax error, unexpected character \"%s\", expected \"%s\" ", token.getValue(), ch);
    }


    public ParseException(CharSequence template, Token... tokens) {
        this.msg = StrUtil.format(template, String.join("", Arrays.stream(tokens).map(Token::getValue).collect(Collectors.joining())));
    }


    @Override
    public String getMessage() {
        return this.msg;
    }
}