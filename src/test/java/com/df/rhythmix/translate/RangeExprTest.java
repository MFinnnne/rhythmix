package com.df.rhythmix.translate;

import cii.da.message.codec.model.SensorEvent;
import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.FerrumExecutor;
import com.df.rhythmix.lexer.Lexer;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.Util;
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
        FerrumExecutor compile = new FerrumExecutor(translatedCode,env);
        
        SensorEvent pointData = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        SensorEvent p2 = Util.genPointData("1", "4", new Timestamp(System.currentTimeMillis()));
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
        SensorEvent pointData = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        SensorEvent p3 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(p3));
        SensorEvent p2 = Util.genPointData("1", "4", new Timestamp(System.currentTimeMillis()));
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
        SensorEvent pointData = Util.genPointData("1", "2.0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        SensorEvent p3 = Util.genPointData("1", "3.0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(p3));
        SensorEvent p2 = Util.genPointData("1", "4.0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(compile.execute(p2));
    }


    @Test
    public void translateRangeExpr3() throws LexicalException, TranslatorException {
        String code = "(-1,3]||(5,MAX)&&!=8";
        EnvProxy env = new EnvProxy();
        env.rawPut("MAX",10);
        String translatedCode = Translator.translate(code, env);
        FerrumExecutor compile = new FerrumExecutor(translatedCode,env);
        SensorEvent pointData = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        SensorEvent p3 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(p3));
        SensorEvent p2 = Util.genPointData("1", "4", new Timestamp(System.currentTimeMillis()));
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
        SensorEvent pointData = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(pointData));
        SensorEvent p3 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(compile.execute(p3));
        SensorEvent p2 = Util.genPointData("1", "4", new Timestamp(System.currentTimeMillis()));
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