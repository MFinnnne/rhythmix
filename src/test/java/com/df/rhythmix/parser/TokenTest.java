package com.df.rhythmix.parser;

import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.lexer.Lexer;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.lexer.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class TokenTest {
    void assertToken(Token token, String value, TokenType type) {
        Assertions.assertEquals(value, token.getValue());
        Assertions.assertEquals(type, token.getType());
    }

    @Test
    public void testExpression() throws LexicalException {
        Lexer lexer = new Lexer();
        var source = "(a+b)^100.12==+100-20/10";
        ArrayList<Token> tokens = lexer.analyse(source.chars().mapToObj(x -> (char) x));
        Assertions.assertEquals(13, tokens.size());
        assertToken(tokens.get(0), "(", TokenType.BRACKET);
        assertToken(tokens.get(1), "a", TokenType.VARIABLE);
        assertToken(tokens.get(2), "+", TokenType.OPERATOR);
        assertToken(tokens.get(3), "b", TokenType.VARIABLE);
        assertToken(tokens.get(4), ")", TokenType.BRACKET);
        assertToken(tokens.get(5), "^", TokenType.OPERATOR);
        assertToken(tokens.get(6), "100.12", TokenType.FLOAT);
        assertToken(tokens.get(7), "==", TokenType.OPERATOR);
        assertToken(tokens.get(8), "+100", TokenType.INTEGER);
        assertToken(tokens.get(9), "-", TokenType.OPERATOR);
        assertToken(tokens.get(10), "20", TokenType.INTEGER);
        assertToken(tokens.get(11), "/", TokenType.OPERATOR);
        assertToken(tokens.get(12), "10", TokenType.INTEGER);
    }

    @Test
    public void testString() throws LexicalException {
        Lexer lexer = new Lexer();
        var source = "\"1\" == \"1\"";
        ArrayList<Token> tokens = lexer.analyse(source.chars().mapToObj(x -> (char) x));
        Assertions.assertEquals(3, tokens.size());
        assertToken(tokens.get(0), "\"1\"", TokenType.STRING);
        assertToken(tokens.get(1), "==", TokenType.OPERATOR);
        assertToken(tokens.get(2), "\"1\"", TokenType.STRING);
    }


    @Test
    void testFunction() throws LexicalException {
        var source = "func foo(a,b){\n" +
                "printf(a+b)\n" +
                "}\n" +
                "foo(-100.0,100)";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(source.chars().mapToObj(x -> (char) x));
        assertToken(tokens.get(0), "func", TokenType.KEYWORD);
        assertToken(tokens.get(1), "foo", TokenType.VARIABLE);
        assertToken(tokens.get(2), "(", TokenType.BRACKET);
        assertToken(tokens.get(3), "a", TokenType.VARIABLE);
        assertToken(tokens.get(4), ",", TokenType.OPERATOR);
        assertToken(tokens.get(5), "b", TokenType.VARIABLE);
        assertToken(tokens.get(6), ")", TokenType.BRACKET);
        assertToken(tokens.get(7), "{", TokenType.BRACKET);
        assertToken(tokens.get(8), "printf", TokenType.VARIABLE);
        assertToken(tokens.get(9), "(", TokenType.BRACKET);
        assertToken(tokens.get(10), "a", TokenType.VARIABLE);
        assertToken(tokens.get(11), "+", TokenType.OPERATOR);
        assertToken(tokens.get(12), "b", TokenType.VARIABLE);
        assertToken(tokens.get(13), ")", TokenType.BRACKET);
        assertToken(tokens.get(14), "}", TokenType.BRACKET);
        assertToken(tokens.get(15), "foo", TokenType.VARIABLE);
        assertToken(tokens.get(16), "(", TokenType.BRACKET);
        assertToken(tokens.get(17), "-100.0", TokenType.FLOAT);
        assertToken(tokens.get(18), ",", TokenType.OPERATOR);
        assertToken(tokens.get(19), "100", TokenType.INTEGER);
        assertToken(tokens.get(20), ")", TokenType.BRACKET);
    }

    @Test
    void testComment() throws LexicalException {
        var source = "/*123131" +
                "\n2321213121*/a=1";
        var lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(source.chars().mapToObj(x -> (char) x));
        Assertions.assertEquals(3, tokens.size());
        assertToken(tokens.get(0), "a", TokenType.VARIABLE);
        assertToken(tokens.get(1), "=", TokenType.OPERATOR);
        assertToken(tokens.get(2), "1", TokenType.INTEGER);


        var source1 = "//123131+\n" +
                "a=1";
        var lexer1 = new Lexer();
        ArrayList<Token> tokens1 = lexer1.analyse(source1.chars().mapToObj(x -> (char) x));
        Assertions.assertEquals(3, tokens1.size());
        assertToken(tokens1.get(0), "a", TokenType.VARIABLE);
        assertToken(tokens1.get(1), "=", TokenType.OPERATOR);
        assertToken(tokens1.get(2), "1", TokenType.INTEGER);
    }


}