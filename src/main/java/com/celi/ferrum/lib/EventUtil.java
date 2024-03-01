package com.celi.ferrum.lib;

import cn.hutool.core.bean.BeanUtil;
import com.celi.ferrum.exception.ComputeException;
import com.celi.ferrum.exception.LexicalException;
import com.celi.ferrum.lexer.Lexer;
import com.celi.ferrum.lexer.Token;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventUtil {
    private static final Lexer LEXER = new Lexer();
    public static List<Token> event2ValueToken(List<Object> values){

        return values.stream().map(item -> {
            Map<String, Object> pd = BeanUtil.beanToMap(item);
            String code = pd.get("value").toString();
            try {
                ArrayList<Token> token = LEXER.analyse(code.chars().mapToObj(x -> (char) x));
                return token.get(0);
            } catch (LexicalException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}
