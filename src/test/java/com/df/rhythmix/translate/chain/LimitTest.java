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

import static org.junit.jupiter.api.Assertions.assertEquals;

class LimitTest {
    @Test
    void test1() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(((1,7]||>10)&&!=5).collect().limit(2).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        EventData p2 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        EventData p3 = Util.genEventData("1", "4", new Timestamp(System.currentTimeMillis()));
        EventData p4 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        EventData p5 = Util.genEventData("1", "8", new Timestamp(System.currentTimeMillis()));
        executor.execute(p5);
        executor.execute(p3);
        executor.execute(p2);
        executor.execute(p4);
    }

    @Test
    void test2() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(((1,7]||>10)&&!=5).collect().limit(100ms).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        EventData p2 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        EventData p3 = Util.genEventData("1", "4", new Timestamp(System.currentTimeMillis() + 50));
        EventData p4 = Util.genEventData("1", "11", new Timestamp(System.currentTimeMillis() + 110));
        boolean execute = executor.execute(p2);
        Assertions.assertTrue(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
        boolean execute2 = executor.execute(p4);
        Assertions.assertTrue(execute2);
    }

    @Test
    void test3() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(((1,7]||>10)&&!=5).collect().limit(0).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        Assertions.assertThrows(TranslatorException.class,()->{
            String transCode = Translator.translate(code, env);
        });

    }

    @Test
    void test4() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(((1,7]||>10)&&!=5).collect().limit(0ms).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        Assertions.assertThrows(TranslatorException.class,()->{
            String transCode = Translator.translate(code, env);
        });

    }

    @Test
    void test5() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(2).take(-3,-1).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertDoesNotThrow(()->{
            executor.execute(p1);
            executor.execute(p2);
            executor.execute(p2);

        });
    }
}