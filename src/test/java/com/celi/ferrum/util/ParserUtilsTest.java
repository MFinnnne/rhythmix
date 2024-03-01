package com.celi.ferrum.util;

import com.celi.ferrum.exception.LexicalException;
import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.lexer.Lexer;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.parser.ast.ASTNode;
import com.celi.ferrum.parser.ast.Expr;
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