package com.df.rhythmix.translate.function;

import com.df.rhythmix.util.RhythmixEventData;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.Executor;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

class CountFunctionTest {

    @Test
    void translate() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "count!(>4,3)";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        Executor translate = new Executor(translatedCode, env);
        RhythmixEventData p1 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "11", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "9", new Timestamp(System.currentTimeMillis()));
        translate.execute(p2);
        translate.execute(p2);
        translate.execute(p1);

        boolean execute = translate.execute(p2);
        Assertions.assertFalse(execute);
        translate.execute(p2);
        boolean execute1 = translate.execute(p2);
        Assertions.assertTrue(execute1);
    }

    @Test
    void translate2() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "count!(>4,3)||count(>10,2)";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        Executor translate = new Executor(translatedCode, env);
        RhythmixEventData p1 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "11", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "9", new Timestamp(System.currentTimeMillis()));
        translate.execute(p2);
        boolean execute2 = translate.execute(p2);
        Assertions.assertTrue(execute2);
        boolean execute3 = translate.execute(p1);
        Assertions.assertFalse(execute3);
    }

    @Test
    void countStrictResetsOnFailure() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        Executor ex = new Executor(Translator.translate("count!(>4,3)", env), env);
        long t = System.currentTimeMillis();
        boolean r;
        r = ex.execute(Util.genEventData("1", "5", new Timestamp(t)));
        Assertions.assertFalse(r);
        r = ex.execute(Util.genEventData("1", "6", new Timestamp(t + 1)));
        Assertions.assertFalse(r);
        r = ex.execute(Util.genEventData("1", "2", new Timestamp(t + 2)));
        Assertions.assertFalse(r);
        r = ex.execute(Util.genEventData("1", "7", new Timestamp(t + 3)));
        Assertions.assertFalse(r);
        r = ex.execute(Util.genEventData("1", "8", new Timestamp(t + 4)));
        Assertions.assertFalse(r);
        r = ex.execute(Util.genEventData("1", "9", new Timestamp(t + 5)));
        Assertions.assertTrue(r);
    }

    @Test
    void countStrictExactNThenReset() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        Executor ex = new Executor(Translator.translate("count!(>10,2)", env), env);
        long t = System.currentTimeMillis();
        boolean r;
        r = ex.execute(Util.genEventData("1", "11", new Timestamp(t)));
        Assertions.assertFalse(r);
        r = ex.execute(Util.genEventData("1", "12", new Timestamp(t + 1)));
        Assertions.assertTrue(r);
        r = ex.execute(Util.genEventData("1", "13", new Timestamp(t + 2)));
        Assertions.assertFalse(r);
    }

    @Test
    void countStrictWithRangeExpr() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        Executor ex = new Executor(Translator.translate("count!([10,20],3)", env), env);
        long t = System.currentTimeMillis();
        boolean r;
        r = ex.execute(Util.genEventData("1", "15", new Timestamp(t)));
        Assertions.assertFalse(r);
        r = ex.execute(Util.genEventData("1", "17", new Timestamp(t + 1)));
        Assertions.assertFalse(r);
        r = ex.execute(Util.genEventData("1", "19", new Timestamp(t + 2)));
        Assertions.assertTrue(r);
        r = ex.execute(Util.genEventData("1", "8", new Timestamp(t + 3)));
        Assertions.assertFalse(r);
    }

    @Test
    void countStrictWithOrCombination() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        Executor ex = new Executor(Translator.translate("count!(>4,3)||count!(<0,2)", env), env);
        long t = System.currentTimeMillis();
        boolean r;
        r = ex.execute(Util.genEventData("1", "-1", new Timestamp(t)));
        Assertions.assertFalse(r);
        r = ex.execute(Util.genEventData("1", "-2", new Timestamp(t + 1)));
        Assertions.assertTrue(r);
    }
}
