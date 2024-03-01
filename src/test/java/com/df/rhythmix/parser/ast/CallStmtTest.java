package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Lexer;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.ParserUtils;
import com.df.rhythmix.util.PeekTokenIterator;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class CallStmtTest {

    @Test
    void parse() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "filter(>3).limit(2).sum().meet(>1)";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode node = Expr.parse(new PeekTokenIterator(tokens.stream()));
        String post = ParserUtils.toPostfixExpression(node);
        Assertions.assertEquals("filter limit sum meet . . .", post);
    }
}