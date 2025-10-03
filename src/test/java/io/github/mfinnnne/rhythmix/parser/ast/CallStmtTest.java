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