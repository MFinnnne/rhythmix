package com.df.rhythmix.exception;

import cn.hutool.core.util.StrUtil;
import com.df.rhythmix.lexer.Token;

import java.util.List;

/**
 * @author MFine
 * @version 1.0
 * @date 2021/7/5 0:31
 **/
public class TypeInferException extends RhythmixException {

    public TypeInferException(char c) {
        super(String.format("类型错误 %c", c));
    }

    public TypeInferException(String msg) {
        super(msg);
    }

    public TypeInferException(CharSequence template, Object... params) {
        super(template, params);
    }

    public TypeInferException(CharSequence template, List<Token> tokens) {
        super(template, tokens);
    }

    public TypeInferException(String msg, Token token) {
        super(msg, token);
    }

    public TypeInferException(CharSequence template, Token token, Object... params) {
        super(template, token, params);
    }

    public TypeInferException(String msg, int characterPosition, int line, int column) {
        super(msg, characterPosition, line, column);
    }

    @Override
    public String getExceptionType() {
        return "type";
    }
}