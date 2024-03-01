package com.df.rhythmix.util;

import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Lexer;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.parser.ast.Expr;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class ParserUtilsTest {

    @Test
    void hasVariable() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "1/vp";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        System.out.println(ParserUtils.toPostfixExpression(parse));
    }
}