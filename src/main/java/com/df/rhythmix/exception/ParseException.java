package com.df.rhythmix.exception;


import cn.hutool.core.util.StrUtil;
import com.df.rhythmix.lexer.Token;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ParseException extends Exception {
    private String msg;

    public ParseException(String msg) {
        this.msg = msg;
    }

    public ParseException(Token token) {
        this.msg = String.format("语法错误,预料之外的字符 %s", token.getValue());
    }

    public ParseException(String ch, Token token) {
        this.msg = String.format("语法错误,预料之外的字符 \"%s\",期望字符 \"%s\" ", token.getValue(), ch);
    }


    public ParseException(CharSequence template, Token... tokens) {
        this.msg = StrUtil.format(template, String.join("", Arrays.stream(tokens).map(Token::getValue).collect(Collectors.joining())));
    }


    @Override
    public String getMessage() {
        return this.msg;
    }
}