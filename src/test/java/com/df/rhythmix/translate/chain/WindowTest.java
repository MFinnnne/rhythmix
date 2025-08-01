package com.df.rhythmix.translate.chain;

import com.df.rhythmix.util.EventData;
import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.Executor;
import com.df.rhythmix.lexer.Lexer;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.translate.ChainExpr;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

class WindowTest {

    @Test
    void translate1() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).window(2).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode, env);
        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        EventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        boolean execute = executor.execute(p2);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
    }

    @Test
    void translate2() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().window(0).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        Assertions.assertThrows(TranslatorException.class, () -> {
            String transCode = Translator.translate(code, env);
        });
    }

    @Test
    void translate3() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).window(100ms).sum().meet(>=7)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode, env);
        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis() + 51));
        EventData p21 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis() + 67));
        EventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis() + 101));
        EventData p4 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis() + 150));
        EventData p5 = Util.genEventData("1", "4", new Timestamp(System.currentTimeMillis() + 250));
//        SensorEvent p5 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis() + 160));
        executor.execute(p1); //0
        boolean execute = executor.execute(p2); //1
        Assertions.assertFalse(execute);
        executor.execute(p21);//1
        boolean execute1 = executor.execute(p3);//2
        Assertions.assertFalse(execute1);
        boolean execute2 = executor.execute(p4);//3
        Assertions.assertFalse(execute2);
        execute2 = executor.execute(p5);
        Assertions.assertTrue(execute2);
    }

    @Test
    void translate4() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).window(0ms).sum().meet(>=6)";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        EnvProxy env = new EnvProxy();
        Assertions.assertThrows(TranslatorException.class, () -> {
            String transCode = ChainExpr.translate(tokens, env);
        });

    }

    @Test
    void translate5() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).window(1s).limit(5).avg().meet(<=0.5)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode, env);
        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis() + 510));
        EventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis() + 1010));
        EventData p4 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis() + 1510));
        executor.execute(p1);//0
        boolean execute = executor.execute(p2);//1
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p3);//2
        Assertions.assertTrue(execute1);
        boolean execute2 = executor.execute(p4);//3
        Assertions.assertFalse(execute2);
    }
}