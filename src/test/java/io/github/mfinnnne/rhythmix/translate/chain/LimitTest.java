package io.github.mfinnnne.rhythmix.translate.chain;

import io.github.mfinnnne.rhythmix.execute.RhythmixCompiler;
import io.github.mfinnnne.rhythmix.execute.RhythmixExecutor;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import io.github.mfinnnne.rhythmix.exception.LexicalException;
import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.pebble.TemplateEngine;
import io.github.mfinnnne.rhythmix.translate.EnvProxy;
import io.github.mfinnnne.rhythmix.translate.Translator;
import io.github.mfinnnne.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LimitTest {
    @Test
    void test1() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(((1,7]||>10)&&!=5).window(2).sum().meet(>1)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);
        RhythmixEventData p2 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "4", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p4 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p5 = Util.genEventData("1", "8", new Timestamp(System.currentTimeMillis()));
        rhythmixExecutor.execute(p5);
        rhythmixExecutor.execute(p3);
        rhythmixExecutor.execute(p2);
        rhythmixExecutor.execute(p4);
    }

    @Test
    void test2() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(((1,7]||>10)&&!=5).window(100ms).sum().meet(>1)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);
        RhythmixEventData p2 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "4", new Timestamp(System.currentTimeMillis() + 50));
        RhythmixEventData p4 = Util.genEventData("1", "11", new Timestamp(System.currentTimeMillis() + 110));
        boolean execute = rhythmixExecutor.execute(p2);
        Assertions.assertFalse(execute);
        boolean execute1 = rhythmixExecutor.execute(p3);
        Assertions.assertFalse(execute1);
        boolean execute2 = rhythmixExecutor.execute(p4);
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
        String code = "filter(((1,7]||>10)&&!=5).limit(0ms).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        Assertions.assertThrows(TranslatorException.class,()->{
            String transCode = Translator.translate(code, env);
        });

    }

    @Test
    void test5() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).limit(2).take(-3,-1).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode,env);;
        RhythmixEventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertDoesNotThrow(()->{
            rhythmixExecutor.execute(p1);
            rhythmixExecutor.execute(p2);
            rhythmixExecutor.execute(p2);

        });
    }

    @Test
    void limitAndWindowCanNotBeUsedTogether() {
        String code = "filter((-5,5)).limit(2).window(100ms).sum().meet(>1)";
        Assertions.assertThrows(TranslatorException.class,()->{
            RhythmixCompiler.compile(code);
        });
    }

    @Test
    void limitCanNotFollowedBySamplingFunction() {
        String code = "filter((-5,5)).window(100ms).limit(2).sum().meet(>1)";
        Assertions.assertThrows(TranslatorException.class,()->{
            RhythmixCompiler.compile(code);
        });
        String code1 = "filter((-5,5)).take(0,1).limit(2).sum().meet(>1)";
        Assertions.assertThrows(TranslatorException.class,()->{
            RhythmixCompiler.compile(code1);
        });
    }
}