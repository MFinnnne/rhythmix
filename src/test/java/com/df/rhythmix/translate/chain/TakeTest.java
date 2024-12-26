package com.df.rhythmix.translate.chain;

import com.df.rhythmix.util.EventData;
import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.Executor;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Timestamp;

class TakeTest {
    @Test
    void translate1() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).take(-3,-1).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        EventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        boolean execute =  executor.execute(p2);
        Assertions.assertFalse(execute);
        boolean execute2 = executor.execute(p2);
        Assertions.assertFalse(execute2);
        boolean execute3 = executor.execute(p2);
        Assertions.assertTrue(execute3);
        executor.execute(p2);
        boolean execute1 = executor.execute(p3);
        Assertions.assertFalse(execute1);
    }


    @Test
    void translate2() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).take(0,-1).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        EventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        executor.execute(p2);
        executor.execute(p2);
        boolean execute = executor.execute(p2);
        Assertions.assertTrue(execute);
        executor.execute(p2);
        boolean execute1 = executor.execute(p3);
        Assertions.assertFalse(execute1);
    }

    @Test
    void translate3() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).take(-3,5).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        EventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        executor.execute(p2);
        executor.execute(p2);
        executor.execute(p2);
        boolean execute = executor.execute(p2);
        Assertions.assertTrue(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertFalse(execute1);
    }


    @Test
    void translate4() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).take(5,3).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            executor.execute(p1);
            executor.execute(p2);
            executor.execute(p2);
        });
    }

    @Test
    void translate5() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).take(-1,-2).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            executor.execute(p1);
            executor.execute(p2);
            executor.execute(p2);
        });
    }

    @Test
    void translate6() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).take(-3).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        EventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        executor.execute(p2);
        boolean execute = executor.execute(p2);
        Assertions.assertTrue(execute);
        executor.execute(p2);
        executor.execute(p2);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
    }

    @Test
    void translate7() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).take(2).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        EventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        executor.execute(p2);
        executor.execute(p2);
        boolean execute = executor.execute(p2);
        Assertions.assertTrue(execute);
        executor.execute(p2);
        executor.execute(p3);
    }

}