package com.df.rhythmix.translate;

import com.df.rhythmix.execute.RhythmixExecutor;
import com.df.rhythmix.util.RhythmixEventData;
import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.lexer.Lexer;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;

class CompareExprTest {

    @Test
    void translateCompareExpr() throws LexicalException, TranslatorException {
        String code = ">1";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        EnvProxy env = new EnvProxy();
        String translatedCode = CompareExpr.translate(tokens, env);
        RhythmixExecutor compile = new RhythmixExecutor(translatedCode,env);
        RhythmixEventData pointData = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        RhythmixEventData p2 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(compile.execute(p2));
    }

    @Test
    void translateCompareExpr1() throws LexicalException, TranslatorException {
        String code = ">1.0";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        EnvProxy env = new EnvProxy();
        String translatedCode = CompareExpr.translate(tokens, env);
        RhythmixExecutor compile = new RhythmixExecutor(translatedCode,env);
        RhythmixEventData pointData = Util.genEventData("1", "2.0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        RhythmixEventData p2 = Util.genEventData("1", "0.0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(compile.execute(p2));
    }

    @Test
    void translateCompareExpr2() throws LexicalException {
        String code = ">'3.0'";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        Assertions.assertThrows(TranslatorException.class, () -> {
            EnvProxy env = new EnvProxy();
            String translatedCode = CompareExpr.translate(tokens, env);
            RhythmixExecutor compile = new RhythmixExecutor(translatedCode,env);
        });

        String code1 = "=='3.0'";
        Lexer lexer1 = new Lexer();
        ArrayList<Token> tokens1 = lexer1.analyse(code1.chars().mapToObj(x -> (char) x));
        Assertions.assertDoesNotThrow(() -> {
            EnvProxy env = new EnvProxy();
            String translatedCode = CompareExpr.translate(tokens1, env);
            new RhythmixExecutor(translatedCode,env);
        });
    }
}