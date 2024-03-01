package com.celi.ferrum.translate;

import com.celi.ferrum.exception.LexicalException;
import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.parser.ast.ASTNode;
import com.celi.ferrum.parser.ast.ArrowStmt;
import com.celi.ferrum.util.PeekTokenIterator;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.lexer.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MutExpr {


    /**
     * 会将 <0,1>转换为 {0}=>{1}
     *
     * @param tokens 符号集合
     * @return 翻译得到的代码
     * @throws TranslatorException 翻译错误
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
                        throw new TranslatorException("{} 后期望得到','但是现在是 {}", next.getValue(), lookahead.getValue());
                    }
                    newTokens.add(new Token(TokenType.OPERATOR, "->"));
                    state = 0;
                    break;
            }
        }
        return newTokens;

    }

}
