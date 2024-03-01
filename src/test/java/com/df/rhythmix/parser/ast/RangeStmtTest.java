package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Lexer;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.PeekTokenIterator;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class RangeStmtTest {

    @Test
    void isRangeStmt1() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "((2,1)||(5,8))";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        boolean rangeStmt = RangeStmt.isRangeStmt(new PeekTokenIterator(tokens.stream()));
        Assertions.assertFalse(rangeStmt);
    }


    @Test
    void isRangeStmt2() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "(2,1)";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        boolean rangeStmt = RangeStmt.isRangeStmt(new PeekTokenIterator(tokens.stream()));
        Assertions.assertTrue(rangeStmt);
    }


    @Test
    void isRangeStmt3() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "((2,1]||[5,8))";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        boolean rangeStmt = RangeStmt.isRangeStmt(new PeekTokenIterator(tokens.stream()));
        Assertions.assertFalse(rangeStmt);
    }

    @Test
    void isRangeStmt4() throws LexicalException, ParseException, ExecutionControl.NotImplementedException {
        String code = "(2,((1+1)*2)/3]";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        boolean rangeStmt = RangeStmt.isRangeStmt(new PeekTokenIterator(tokens.stream()));
        Assertions.assertTrue(rangeStmt);
    }
}