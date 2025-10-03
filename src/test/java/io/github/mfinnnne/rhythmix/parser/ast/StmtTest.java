package io.github.mfinnnne.rhythmix.parser.ast;


import io.github.mfinnnne.rhythmix.exception.LexicalException;
import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.lexer.Lexer;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.lexer.TokenType;
import io.github.mfinnnne.rhythmix.parser.ast.ASTNode;
import io.github.mfinnnne.rhythmix.parser.ast.IfStmt;
import io.github.mfinnnne.rhythmix.util.ParserUtils;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * author MFine
 * version 1.0
 * date 2021/10/26 0:07
 **/
class StmtTest {

    void assertToken(Token token, String value, TokenType type) {
        Assertions.assertEquals(value, token.getValue());
        Assertions.assertEquals(type, token.getType());
    }


    private PeekTokenIterator createTokenIt(String src) throws LexicalException {
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(src.chars().mapToObj(x -> (char) x));
        return new PeekTokenIterator(tokens.stream());
    }

    @Test
    void IfStmt() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        PeekTokenIterator tokenIt = createTokenIt("if(a){\n" +
                "a=1\n" +
                "}");
        ASTNode parse = IfStmt.parse(tokenIt);
        parse.print(0);
        Assertions.assertEquals("a a 1 assign block if", ParserUtils.toPostfixExpression(parse));
    }

    @Test
    void IfElseStmt() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        PeekTokenIterator tokenIt = createTokenIt("if(a){\n" +
                "a=1\n" +
                "} else {\n" +
                "a=2\n" +
                "a=a*3" +
                "}");
        ASTNode parse = IfStmt.parse(tokenIt);
        parse.print(0);
        assertToken(parse.getLexeme(), "if", TokenType.KEYWORD);
        ASTNode children = parse.getChildren(0);
        assertToken(children.getLexeme(), "a", TokenType.VARIABLE);
        ASTNode children1 = parse.getChildren(1);
        Assertions.assertEquals(children1.getLabel(), "block");

        ASTNode assign = children1.getChildren(0);
        assertToken(assign.getLexeme(), "=", TokenType.OPERATOR);

        ASTNode children2 = parse.getChildren(2);
        Assertions.assertEquals(children2.getLabel(), "block");

        ASTNode elseAssign1 = children2.getChildren(0);
        assertToken(elseAssign1.getLexeme(), "=", TokenType.OPERATOR);

        ASTNode elseAssign2 = children2.getChildren(1);
        assertToken(elseAssign2.getLexeme(), "=", TokenType.OPERATOR);
    }


}