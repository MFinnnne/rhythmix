package com.df.rhythmix.translate;

import com.df.rhythmix.execute.RhythmixCompiler;
import com.df.rhythmix.execute.RhythmixExecutor;
import com.df.rhythmix.util.RhythmixEventData;
import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.lib.Register;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Timestamp;

class ChainExprTest {
    @BeforeAll
    static void beforeAll() {
        Register.importFunction();
    }


    @Test
    void translate() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).limit(5).take(0,2).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode, env);
        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        boolean execute = rhythmixExecutor.execute(p1);
        Assertions.assertFalse(execute);
        boolean execute1 = rhythmixExecutor.execute(p3);
        Assertions.assertTrue(execute1);
        rhythmixExecutor.execute(p2);
        boolean execute2 = rhythmixExecutor.execute(p2);
        Assertions.assertTrue(execute2);

        rhythmixExecutor.execute(p1);
        rhythmixExecutor.execute(p1);
        rhythmixExecutor.execute(p1);
        rhythmixExecutor.execute(p1);
        boolean execute3 = rhythmixExecutor.execute(p1);
        Assertions.assertFalse(execute3);

        rhythmixExecutor.execute(p2);
        boolean execute4 = rhythmixExecutor.execute(p2);
        Assertions.assertFalse(execute4);
        rhythmixExecutor.execute(p2);
        rhythmixExecutor.execute(p2);
        boolean execute5 = rhythmixExecutor.execute(p2);
        Assertions.assertTrue(execute5);
    }

    @Test
    void translate2() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).limit(500ms).take(-3,-1).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode, env);
        ;
        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        rhythmixExecutor.execute(p1);
        rhythmixExecutor.execute(p2);
        boolean execute = rhythmixExecutor.execute(p3);
    }

    @Test
    void translate3() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "limit(500ms).take(0,3).stddev().meet((1.414,1.415))";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);
        RhythmixEventData p1 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "7", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        rhythmixExecutor.execute(p1);
        rhythmixExecutor.execute(p2);
        boolean execute = rhythmixExecutor.execute(p3);
        Assertions.assertTrue(execute);
    }


    @Test
    void translate4() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "count().meet(==2)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode, env);
        ;
        RhythmixEventData p1 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "7", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        rhythmixExecutor.execute(p1);
        boolean execute1 = rhythmixExecutor.execute(p2);
        Assertions.assertTrue(execute1);
        boolean execute = rhythmixExecutor.execute(p3);
        Assertions.assertFalse(execute);
    }

    @Test
    void translate5() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).limit(500ms).take(-3,-1).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode, env);
        ;
        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        rhythmixExecutor.execute(p1);
        rhythmixExecutor.execute(p2);
        boolean execute = rhythmixExecutor.execute(p3);
    }

    @Test
    void translate6() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "limit(500ms).take(-3,-1).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode, env);
        ;
        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        rhythmixExecutor.execute(p1);
        rhythmixExecutor.execute(p2);
        boolean execute = rhythmixExecutor.execute(p3);
    }

    @Test
    void translate7() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "limit(500ms).window(400ms).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        Assertions.assertThrows(TranslatorException.class, () -> {
            String transCode = Translator.translate(code, env);
        });
    }

    @Test
    void translate8() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "window(400ms).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode, env);
        ;
        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        rhythmixExecutor.execute(p1);
        rhythmixExecutor.execute(p2);
        boolean execute = rhythmixExecutor.execute(p3);
    }


    @Test
    void translate9() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "limit(500ms).take(0,3).stddev().meet((1.414,<1.415))";
        Assertions.assertThrows(TranslatorException.class, () -> {
            RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);
        });
    }
}