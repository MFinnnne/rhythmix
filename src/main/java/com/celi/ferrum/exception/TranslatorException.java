package com.celi.ferrum.exception;

import cn.hutool.core.util.StrUtil;
import com.celi.ferrum.lexer.Token;

import java.util.List;

/**
 * @author MFine
 * @version 1.0
 * @date 2021/7/5 0:31
 **/
public class TranslatorException extends Exception {

    private String msg;

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


    @Override
    public String toString() {
        return this.msg;
    }
}