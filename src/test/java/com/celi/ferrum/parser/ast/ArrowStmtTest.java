package com.celi.ferrum.parser.ast;

import com.celi.ferrum.exception.LexicalException;
import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.lexer.Lexer;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.util.ParserUtils;
import com.celi.ferrum.util.PeekTokenIterator;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class ArrowStmtTest {

    @Test
    void parser1() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "{==1}->{==2}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode node = ArrowStmt.parse(new PeekTokenIterator(tokens.stream()));
        String post = ParserUtils.toPostfixExpression(node);
        Assertions.assertEquals("1 == 2 == arrow expr", post);
    }

    @Test
    void parser2() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "{>=1}->{<2}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode node = ArrowStmt.parse(new PeekTokenIterator(tokens.stream()));
        String post = ParserUtils.toPostfixExpression(node);
         Assertions.assertEquals("1 >= 2 < arrow expr", post);
    }


    @Test
    void parser3() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "{>=1}->{<2}->{==1}->{!=2}->{(1,2)}->{(2,3]}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode node = ArrowStmt.parse(new PeekTokenIterator(tokens.stream()));
        String post = ParserUtils.toPostfixExpression(node);
        Assertions.assertEquals("1 >= 2 < 1 == 2 != 1 ( 2 ) range expr 2 ( 3 ] range expr arrow expr", post);
    }


    @Test
    void parser4() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = ">=1}->{<2}->{==1}->{!=2}->{(1,2)}->{(2,3]}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        Assertions.assertThrows(ParseException.class, () -> {
            ArrowStmt.parse(new PeekTokenIterator(tokens.stream()));
        });
    }

    @Test
    void parser5() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "{>=1}-><2}->{==1}->{!=2}->{(1,2)}->{(2,3]}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        Assertions.assertThrows(ParseException.class, () -> {
            ArrowStmt.parse(new PeekTokenIterator(tokens.stream()));
        });
    }


    @Test
    void parser7() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "{(1,)}->{(2,3]}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        Assertions.assertThrows(ParseException.class, () -> {
            ASTNode node = ArrowStmt.parse(new PeekTokenIterator(tokens.stream()));
            System.out.println(ParserUtils.toPostfixExpression(node));
        });
    }

    @Test
    void parser8() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "{>=1||<-3}->{<2}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode node = ArrowStmt.parse(new PeekTokenIterator(tokens.stream()));
        String post = ParserUtils.toPostfixExpression(node);
        Assertions.assertEquals("1 >= -3 < || 2 < arrow expr", post);
    }
}