/*
 * author: MFine
 * date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-02-06 22:37:55
 * @LastEditors: MFine
 * @Description: 
 */
package com.df.rhythmix.translate;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.util.PeekTokenIterator;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>MutExpr class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
public class MutExpr {


    /**
     * @param tokens 符号集合
     * @return 翻译得到的代码
     * @throws com.df.rhythmix.exception.TranslatorException 翻译错误
     */
    public static List<Token> translateMutExpr(List<Token> tokens) throws TranslatorException {
        PeekTokenIterator it = new PeekTokenIterator(tokens.stream());
        List<Token> newTokens = new ArrayList<>();
        int state = 0;
        while (it.hasNext()) {
            if (it.nextMatch1("<")) {
                it.next();
                continue;
            }
            Token next = it.next();
            Token lookahead = it.peek();
            switch (state) {
                case 0:
                    newTokens.add(new Token(TokenType.BRACKET, "{"));
                    newTokens.add(new Token(TokenType.OPERATOR, "=="));
                    newTokens.add(next);
                    newTokens.add(new Token(TokenType.BRACKET, "}"));
                    if (">".equals(lookahead.getValue())) {
                        it.next();
                        break;
                    } else {
                        state = 1;
                    }
                    break;
                case 1:
                    if (!",".equals(next.getValue())) {
                        throw new TranslatorException("Illegal expression, unable to understand this syntax or it may not be supported yet");
                    }
                    newTokens.add(new Token(TokenType.OPERATOR, "->"));
                    state = 0;
                    break;
            }
        }
        return newTokens;

    }

}
