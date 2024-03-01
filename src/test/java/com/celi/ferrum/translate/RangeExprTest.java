package com.celi.ferrum.translate;

import cii.da.message.codec.model.PointData;
import com.celi.ferrum.exception.LexicalException;
import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.execute.FerrumExecutor;
import com.celi.ferrum.lexer.Lexer;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class RangeExprTest {

    @Test
    public void translateRangeExpr() throws LexicalException, TranslatorException {
        String code = "(1,3)";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));

        EnvProxy env = new EnvProxy();
        String translatedCode = RangeExpr.translate(tokens, env);
        FerrumExecutor compile = new FerrumExecutor(translatedCode,env);
        
        PointData pointData = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        PointData p2 = Util.genPointData("1", "4", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(compile.execute(p2));
    }

    @Test
    public void translateRangeExpr1() throws LexicalException, TranslatorException {
        String code = "(1,3]";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        EnvProxy env = new EnvProxy();
        String translatedCode = RangeExpr.translate(tokens, env);
        FerrumExecutor compile = new FerrumExecutor(translatedCode,env);
        PointData pointData = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        PointData p3 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(p3));
        PointData p2 = Util.genPointData("1", "4", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(compile.execute(p2));
    }


    @Test
    public void translateRangeExpr2() throws LexicalException, TranslatorException {
        String code = "(1.0,3.0]";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        EnvProxy env = new EnvProxy();
        String translatedCode = RangeExpr.translate(tokens, env);
        FerrumExecutor compile = new FerrumExecutor(translatedCode,env);
        PointData pointData = Util.genPointData("1", "2.0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        PointData p3 = Util.genPointData("1", "3.0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(p3));
        PointData p2 = Util.genPointData("1", "4.0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(compile.execute(p2));
    }


    @Test
    public void translateRangeExpr3() throws LexicalException, TranslatorException {
        String code = "(-1,3]||(5,MAX)&&!=8";
        EnvProxy env = new EnvProxy();
        env.rawPut("MAX",10);
        String translatedCode = Translator.translate(code, env);
        FerrumExecutor compile = new FerrumExecutor(translatedCode,env);
        PointData pointData = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        PointData p3 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(p3));
        PointData p2 = Util.genPointData("1", "4", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(compile.execute(p2));
    }

    @Test
    public void translateRangeExpr4() throws LexicalException, TranslatorException {
        String code = "(1,3.0]";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        Assertions.assertThrows(TranslatorException.class,()->{
            EnvProxy env = new EnvProxy();
            String translatedCode = RangeExpr.translate(tokens, env);
            FerrumExecutor compile = new FerrumExecutor(translatedCode,env);
        });
    }

    @Test
    public void translateRangeExpr5() throws LexicalException, TranslatorException {
        String code = "('1','3.0']";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        Assertions.assertThrows(TranslatorException.class,()->{
            EnvProxy env = new EnvProxy();
            String translatedCode = RangeExpr.translate(tokens, env);
            FerrumExecutor compile = new FerrumExecutor(translatedCode,env);
        });
    }

    @Test
    public void translateRangeExpr6() throws LexicalException, TranslatorException {
        String code = "((1+0)*1,3]";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        EnvProxy env = new EnvProxy();
        String translatedCode = RangeExpr.translate(tokens, env);
        FerrumExecutor compile = new FerrumExecutor(translatedCode,env);
        PointData pointData = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        PointData p3 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(p3));
        PointData p2 = Util.genPointData("1", "4", new Timestamp(System.currentTimeMillis()));
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