package io.github.mfinnnne.rhythmix.translate.chain;

import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.execute.RhythmixExecutor;
import io.github.mfinnnne.rhythmix.pebble.TemplateEngine;
import io.github.mfinnnne.rhythmix.translate.EnvProxy;
import io.github.mfinnnne.rhythmix.translate.Translator;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import io.github.mfinnnne.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

class TakeTest {
    @Test
    void translate1() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).limit(5).take(-3,-1).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode, env);

        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        rhythmixExecutor.execute(p1);
        boolean execute = rhythmixExecutor.execute(p2);
        Assertions.assertFalse(execute);
        boolean execute2 = rhythmixExecutor.execute(p2);
        Assertions.assertFalse(execute2);
        boolean execute3 = rhythmixExecutor.execute(p2);
        Assertions.assertTrue(execute3);
        rhythmixExecutor.execute(p2);
        boolean execute1 = rhythmixExecutor.execute(p3);
        Assertions.assertFalse(execute1);
    }


    @Test
    void translate2() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).limit(5).take(0,-1).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode, env);

        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        rhythmixExecutor.execute(p1);
        rhythmixExecutor.execute(p2);
        rhythmixExecutor.execute(p2);
        boolean execute = rhythmixExecutor.execute(p2);
        Assertions.assertTrue(execute);
        rhythmixExecutor.execute(p2);
        boolean execute1 = rhythmixExecutor.execute(p3);
        Assertions.assertFalse(execute1);
    }

    @Test
    void translate3() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).limit(5).take(-3,5).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode, env);

        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        rhythmixExecutor.execute(p1);
        rhythmixExecutor.execute(p2);
        rhythmixExecutor.execute(p2);
        rhythmixExecutor.execute(p2);
        boolean execute = rhythmixExecutor.execute(p2);
        Assertions.assertTrue(execute);
        boolean execute1 = rhythmixExecutor.execute(p3);
        Assertions.assertFalse(execute1);
    }


    @Test
    void translate4() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).limit(5).take(5,3).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode, env);

        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            rhythmixExecutor.execute(p1);
            rhythmixExecutor.execute(p2);
            rhythmixExecutor.execute(p2);
        });
    }

    @Test
    void translate5() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).limit(5).take(-1,-2).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode, env);

        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            rhythmixExecutor.execute(p1);
            rhythmixExecutor.execute(p2);
            rhythmixExecutor.execute(p2);
        });
    }

    @Test
    void translate6() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).limit(5).take(-3).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode, env);

        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        rhythmixExecutor.execute(p1);
        rhythmixExecutor.execute(p2);
        boolean execute = rhythmixExecutor.execute(p2);
        Assertions.assertTrue(execute);
        rhythmixExecutor.execute(p2);
        rhythmixExecutor.execute(p2);
        boolean execute1 = rhythmixExecutor.execute(p3);
        Assertions.assertTrue(execute1);
    }

    @Test
    void translate7() throws TranslatorException{
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).limit(5).take(2).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode, env);

        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        rhythmixExecutor.execute(p1);
        rhythmixExecutor.execute(p2);
        rhythmixExecutor.execute(p2);
        boolean execute = rhythmixExecutor.execute(p2);
        Assertions.assertTrue(execute);
        rhythmixExecutor.execute(p2);
        rhythmixExecutor.execute(p3);
    }

    @Test
    void translate8() {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5).limit(5).take(5,3).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        Assertions.assertThrows(TranslatorException.class, () -> Translator.translate(code, env));
    }

}