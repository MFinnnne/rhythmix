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