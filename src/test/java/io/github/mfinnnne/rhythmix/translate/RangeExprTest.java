package io.github.mfinnnne.rhythmix.translate;

import io.github.mfinnnne.rhythmix.execute.RhythmixExecutor;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import io.github.mfinnnne.rhythmix.exception.LexicalException;
import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.lexer.Lexer;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;

class RangeExprTest {

    @Test
    public void translateRangeExpr() throws LexicalException, TranslatorException {
        String code = "(1,3)";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));

        EnvProxy env = new EnvProxy();
        String translatedCode = RangeExpr.translate(tokens, env);
        RhythmixExecutor compile = new RhythmixExecutor(translatedCode,env);
        
        RhythmixEventData pointData = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        RhythmixEventData p2 = Util.genEventData("1", "4", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(compile.execute(p2));
    }

    @Test
    public void translateRangeExpr1() throws LexicalException, TranslatorException {
        String code = "(1,3]";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        EnvProxy env = new EnvProxy();
        String translatedCode = RangeExpr.translate(tokens, env);
        RhythmixExecutor compile = new RhythmixExecutor(translatedCode,env);
        RhythmixEventData pointData = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        RhythmixEventData p3 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(p3));
        RhythmixEventData p2 = Util.genEventData("1", "4", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(compile.execute(p2));
    }


    @Test
    public void translateRangeExpr2() throws LexicalException, TranslatorException {
        String code = "(1.0,3.0]";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        EnvProxy env = new EnvProxy();
        String translatedCode = RangeExpr.translate(tokens, env);
        RhythmixExecutor compile = new RhythmixExecutor(translatedCode,env);
        RhythmixEventData pointData = Util.genEventData("1", "2.0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        RhythmixEventData p3 = Util.genEventData("1", "3.0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(p3));
        RhythmixEventData p2 = Util.genEventData("1", "4.0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(compile.execute(p2));
    }


    @Test
    public void translateRangeExpr3() throws LexicalException, TranslatorException {
        String code = "(-1,3]||(5,MAX)&&!=8";
        EnvProxy env = new EnvProxy();
        env.rawPut("MAX",10);
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor compile = new RhythmixExecutor(translatedCode,env);
        RhythmixEventData pointData = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        RhythmixEventData p3 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(p3));
        RhythmixEventData p2 = Util.genEventData("1", "4", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(compile.execute(p2));
    }



    @Test
    public void translateRangeExpr6() throws LexicalException, TranslatorException {
        String code = "((1+0)*1,3]";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        EnvProxy env = new EnvProxy();
        String translatedCode = RangeExpr.translate(tokens, env);
        RhythmixExecutor compile = new RhythmixExecutor(translatedCode,env);
        RhythmixEventData pointData = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        RhythmixEventData p3 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(p3));
        RhythmixEventData p2 = Util.genEventData("1", "4", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(compile.execute(p2));
    }

    @Test
    public void translateRangeExpr7() throws LexicalException, TranslatorException {
        String code = "(-1,1]||(5,MAX)&&!=8";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        EnvProxy env = new EnvProxy();
        String translatedCode = RangeExpr.translate(tokens, env);
        System.out.println(translatedCode);
    }
}