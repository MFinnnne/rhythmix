package io.github.mfinnnne.rhythmix.parser.ast;

import io.github.mfinnnne.rhythmix.exception.LexicalException;
import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.lexer.Lexer;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.parser.ast.ASTNode;
import io.github.mfinnnne.rhythmix.parser.ast.Expr;
import io.github.mfinnnne.rhythmix.util.ParserUtils;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class ExprTest {

    @Test
    void parse1() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "1/10";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        System.out.println(ParserUtils.toPostfixExpression(parse));
    }


    @Test
    void parse2() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "((1,1)||(1,2))&&(1,3)";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        String post = ParserUtils.toPostfixExpression(parse);
        Assertions.assertEquals("1 ( 1 ) range expr 1 ( 2 ) range expr || 1 ( 3 ) range expr &&", post);
    }


    @Test
    void parse3() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "1||2";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        String post = ParserUtils.toPostfixExpression(parse);
        Assertions.assertEquals("1 2 ||", post);
    }

    @Test
    void parse5() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "(1,2)";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        String post = ParserUtils.toPostfixExpression(parse);
        Assertions.assertEquals("1 ( 2 ) range expr", post);
    }

    @Test
    void parse6() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "(1,2)||(5,6)";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        String post = ParserUtils.toPostfixExpression(parse);
        Assertions.assertEquals("1 ( 2 ) range expr 5 ( 6 ) range expr ||", post);
    }

    @Test
    void parse7() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "(>1||(5,6))&&!=10";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        String post = ParserUtils.toPostfixExpression(parse);
        Assertions.assertEquals("1 > 5 ( 6 ) range expr || 10 != &&", post);
    }


    @Test
    void parse8() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "((5+1),9)";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        String post = ParserUtils.toPostfixExpression(parse);
        ParserUtils.printTree(parse,10);
        Assertions.assertEquals("5 1 + ( 9 ) range expr", post);
    }

    @Test
    void parse10() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "((2+1)/2,100)";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        Assertions.assertEquals("2 1 + 2 / ( 100 ) range expr", ParserUtils.toPostfixExpression(parse));
    }


    @Test
    void parse11() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "((2,1))";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));

        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        Assertions.assertEquals("2 ( 1 ) range expr", ParserUtils.toPostfixExpression(parse));
    }


    @Test
    void parse12() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "!a&&b";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));

        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        Assertions.assertEquals("a ! b &&", ParserUtils.toPostfixExpression(parse));
    }
}