package com.df.rhythmix.translate;

import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.RhythmixExecutor;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.util.RhythmixEventData;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.sql.Timestamp;

@Execution(ExecutionMode.CONCURRENT)
class ArrowExprTest {


    @Test
    void translate1() throws LexicalException, ParseException, TranslatorException, IOException {
        String code = "{>=1||<-3}->{>10}";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode, env);

        RhythmixEventData p1 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "11", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "9", new Timestamp(System.currentTimeMillis()));
        boolean execute = translate.execute(p1, p2);
        Assertions.assertTrue(execute);
        Assertions.assertFalse(translate.execute(p1, p3));
    }

    @Test
    void translate2() throws LexicalException, ParseException, TranslatorException, IOException {
        String code = "{(-1,5)}->{>10}";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode, env);
        RhythmixEventData p1 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "11", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "9", new Timestamp(System.currentTimeMillis()));
        boolean execute = translate.execute(p1, p2);
        Assertions.assertTrue(execute);
        Assertions.assertFalse(translate.execute(p1, p3));
    }

    @Test
    void translate3() throws LexicalException, ParseException, TranslatorException, IOException {
        String code = "{(-1,5)||(10,20)}->{>10}";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode, env);
        RhythmixEventData p1 = Util.genEventData("1", "15", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "11", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "9", new Timestamp(System.currentTimeMillis()));
        boolean execute = translate.execute(p1, p2);
        Assertions.assertTrue(execute);
        Assertions.assertFalse(translate.execute(p1, p3));
    }

    @Test
    void translate4() throws LexicalException, ParseException, TranslatorException, IOException {
        String code = "{==3||==4}->{!=10}";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode, env);
        RhythmixEventData p1 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "11", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        boolean execute = translate.execute(p1, p2);
        Assertions.assertTrue(execute);
        Assertions.assertFalse(translate.execute(p2, p3));
        Assertions.assertFalse(translate.execute(p1, p3));
    }

    @Test
    void translate5() throws LexicalException, ParseException, TranslatorException, IOException {
        String code = "{((1,5]||==10)&&!=7}->{!=10}";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode, env);
        RhythmixEventData p1 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "7", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        boolean execute = translate.execute(p1, p2);
        Assertions.assertTrue(execute);
        Assertions.assertFalse(translate.execute(p2, p3));
        Assertions.assertFalse(translate.execute(p1, p3));
    }


    @Test
    void translate6() throws LexicalException, ParseException, TranslatorException, IOException {

        TemplateEngine.enableDebugModel(true);

        String code = "{keep(>3,100ms)}->{!=10}";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode, env);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        RhythmixEventData p1 = Util.genEventData("1", "10", ts);
        RhythmixEventData p2 = Util.genEventData("1", "7", Util.addMs(ts, 100));
        RhythmixEventData p3 = Util.genEventData("1", "7", Util.addMs(ts, 100));
        boolean execute = translate.execute(p1, p2, p3);
        Assertions.assertTrue(execute);
    }

    @Test
    void translate7() throws LexicalException, ParseException, TranslatorException, IOException {

        TemplateEngine.enableDebugModel(true);
        String code = "{delay(100ms)}->{!=10}";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode, env);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        RhythmixEventData p1 = Util.genEventData("1", "10", ts);
        RhythmixEventData p2 = Util.genEventData("1", "7", Util.addMs(ts, 100));
        RhythmixEventData p3 = Util.genEventData("1", "7", Util.addMs(ts, 100));
        boolean execute = translate.execute(p1, p2, p3);
        Assertions.assertTrue(execute);
    }

    @Test
    void translate8() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "{!=10}->{delay(100ms)}";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode, env);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        RhythmixEventData p1 = Util.genEventData("1", "9", ts);
        RhythmixEventData p2 = Util.genEventData("1", "7", Util.addMs(ts, 100));
        RhythmixEventData p3 = Util.genEventData("1", "7", Util.addMs(ts, 200));
        boolean execute = translate.execute(p1, p2, p3);
        Assertions.assertTrue(execute);
    }

    @Test
    void translate9() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "{count(>1,3)}->{delay(100ms)}";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode, env);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        RhythmixEventData p1 = Util.genEventData("1", "9", ts);
        RhythmixEventData p2 = Util.genEventData("1", "7", Util.addMs(ts, 100));
        RhythmixEventData p3 = Util.genEventData("1", "7", Util.addMs(ts, 200));
        RhythmixEventData p4 = Util.genEventData("1", "7", Util.addMs(ts, 340));
        translate.execute(p1);
        translate.execute(p2);
        translate.execute(p3);
        boolean execute = translate.execute(p4);
        Assertions.assertTrue(execute);
    }

    @Test
    void translate10() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "{count(>1,3)}->{delay(100ms)}->{delay(100ms)}";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode, env);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        RhythmixEventData p1 = Util.genEventData("1", "9", ts);
        RhythmixEventData p2 = Util.genEventData("1", "7", Util.addMs(ts, 100));
        RhythmixEventData p3 = Util.genEventData("1", "7", Util.addMs(ts, 200));
        RhythmixEventData p4 = Util.genEventData("1", "7", Util.addMs(ts, 350));
        RhythmixEventData p5 = Util.genEventData("1", "7", Util.addMs(ts, 550));
        boolean execute = translate.execute(p1, p2, p3, p4, p5);
        Assertions.assertTrue(execute);
    }

    @Test
    void translate11() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "{count().meet(==5)||filter(>1).window(3).sum().meet(>10)}->{(-1,5]}->{delay(100ms)}->{keep(>10,100ms)}";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode, env);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        //第一种情况
        RhythmixEventData p1 = Util.genEventData("1", "9", ts);
        RhythmixEventData p2 = Util.genEventData("1", "7", Util.addMs(ts, 100));
        RhythmixEventData p6 = Util.genEventData("1", "3", Util.addMs(ts, 550));
        RhythmixEventData p7 = Util.genEventData("1", "3", Util.addMs(ts, 750));
        RhythmixEventData p8 = Util.genEventData("1", "11", Util.addMs(ts, 750));
        RhythmixEventData p9 = Util.genEventData("1", "11", Util.addMs(ts, 950));
        boolean execute1 = translate.execute(p1, p2, p1, p2, p1);
        Assertions.assertFalse(execute1);
        boolean execute = translate.execute(p6);
        Assertions.assertFalse(execute);
        boolean execute2 = translate.execute(p7);
        Assertions.assertFalse(execute2);
        boolean execute3 = translate.execute(p8, p9);
        Assertions.assertTrue(execute3);

        boolean execute4 = translate.execute(p8, p8, p8);
        Assertions.assertFalse(execute4);
        execute = translate.execute(p6);
        Assertions.assertFalse(execute);
        execute2 = translate.execute(p7);
        Assertions.assertFalse(execute2);
        execute3 = translate.execute(p8, p9);
        Assertions.assertTrue(execute3);
    }

    @Test
    void translate12() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "{(-1,5]}->{keep(>2,100ms)}||{(7,12]}->{keep(>10,100ms)}";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode, env);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        //第一种情况
        RhythmixEventData p1 = Util.genEventData("1", "3", ts);
        RhythmixEventData p2 = Util.genEventData("1", "3", Util.addMs(ts, 110));
        RhythmixEventData p3 = Util.genEventData("1", "8", Util.addMs(ts, 250));
        RhythmixEventData p8 = Util.genEventData("1", "11", Util.addMs(ts, 750));
        RhythmixEventData p9 = Util.genEventData("1", "11", Util.addMs(ts, 950));
        boolean execute1 = translate.execute(p3, p8, p9);
        Assertions.assertTrue(execute1);

        execute1 = translate.execute(p1, p1, p2);
        Assertions.assertTrue(execute1);

    }
}