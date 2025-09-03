package com.df.rhythmix.translate.chain;

import com.df.rhythmix.util.RhythmixEventData;
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

class FilterTest {
    @Test
    void test1() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(>3).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p4 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        executor.execute(p2);
        boolean execute = executor.execute(p3);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p4);
        Assertions.assertTrue(execute1);
        boolean execute2 = executor.execute(p4);
        Assertions.assertTrue(execute2);
    }

    @Test
    void test2() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(>=3).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p4 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        executor.execute(p2);
        boolean execute = executor.execute(p3);
        Assertions.assertTrue(execute);
        executor.execute(p4);
        executor.execute(p4);
    }

    @Test
    void test3() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(<3).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p4 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p5 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p4);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p1);
        Assertions.assertFalse(execute1);
        boolean execute2 = executor.execute(p2);
        Assertions.assertFalse(execute2);
        boolean execute3 = executor.execute(p3);
        Assertions.assertFalse(execute3);
        boolean execute4 = executor.execute(p5);
        Assertions.assertTrue(execute4);
    }

    @Test
    void test4() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(<=3).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p4 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p4);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p1);
        Assertions.assertFalse(execute1);
        executor.execute(p2);
        boolean execute2 = executor.execute(p3);
        Assertions.assertTrue(execute2);
        executor.execute(p4);
    }


    @Test
    void test5() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(==3).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p4 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p4);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
    }

    @Test
    void test6() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(!=3).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        RhythmixEventData p3 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p4 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p3);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p4);
        Assertions.assertTrue(execute1);
    }


    @Test
    void test7() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((1,4)).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        RhythmixEventData p3 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p4 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p4);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
    }

    @Test
    void test8() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((1,4]).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        RhythmixEventData p2 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "4", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p4 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p4);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
        boolean execute2 = executor.execute(p2);
        Assertions.assertTrue(execute2);
    }


    @Test
    void test9() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(((1,7]||>10)&&!=5).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        RhythmixEventData p2 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "4", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p4 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p5 = Util.genEventData("1", "8", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p5);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
        boolean execute2 = executor.execute(p2);
        Assertions.assertTrue(execute2);
        boolean execute3 = executor.execute(p4);
        Assertions.assertFalse(execute3);
    }

    @Test
    void test10() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((1,6)).sum().meet(>1)||filter(>9).sum().meet((12,30))";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        RhythmixEventData p2 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "4", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p4 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p5 = Util.genEventData("1", "8", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p6 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p5);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
        boolean execute2 = executor.execute(p2);
        Assertions.assertTrue(execute2);
        boolean execute3 = executor.execute(p4);
        Assertions.assertTrue(execute3);
        boolean execute4 = executor.execute(p6);
        Assertions.assertFalse(execute4);
        boolean execute5 = executor.execute(p6);
        Assertions.assertTrue(execute5);
    }
}