package io.github.mfinnnne.rhythmix.util;

import io.github.mfinnnne.rhythmix.exception.LexicalException;
import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.lexer.Lexer;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.parser.ast.ASTNode;
import io.github.mfinnnne.rhythmix.parser.ast.Expr;
import io.github.mfinnnne.rhythmix.util.ParserUtils;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;
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