package io.github.mfinnnne.rhythmix.translate;

import io.github.mfinnnne.rhythmix.exception.LexicalException;
import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.execute.RhythmixExecutor;
import io.github.mfinnnne.rhythmix.pebble.TemplateEngine;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import io.github.mfinnnne.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

class FunctionExprTest {

    @Test
    void translate1() throws LexicalException, ParseException, TranslatorException {
        String code = "count(>4,3)||count(<1,3)";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode,env);
//        System.out.println(translate);
        RhythmixEventData p1 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "11", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "9", new Timestamp(System.currentTimeMillis()));
        translate.execute(p2);
        translate.execute(p2);
        boolean execute = translate.execute(p2);
        Assertions.assertTrue(execute);
    }

    @Test
    void translate2() throws LexicalException, ParseException, TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "keep(>4,100ms)||keep(<1,50ms)";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode,env);
//        System.out.println(translate);
        RhythmixEventData p1 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "11", new Timestamp(System.currentTimeMillis()+110));
        RhythmixEventData p3 = Util.genEventData("1", "9", new Timestamp(System.currentTimeMillis()));
        translate.execute(p1);
        boolean execute = translate.execute(p2);
        Assertions.assertTrue(execute);
    }

    @Test
    void translate3() throws LexicalException, ParseException, TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "delay(100ms)";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode,env);
//        System.out.println(translate);
        RhythmixEventData p1 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "11", new Timestamp(System.currentTimeMillis()+110));
        RhythmixEventData p3 = Util.genEventData("1", "9", new Timestamp(System.currentTimeMillis()));
        translate.execute(p1);
        boolean execute = translate.execute(p2);
        Assertions.assertTrue(execute);
    }


    @Test
    void translate4() throws LexicalException, ParseException, TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "keep(>4,100ms)";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        RhythmixExecutor translate = new RhythmixExecutor(translatedCode,env);
//        System.out.println(translate);
        RhythmixEventData p1 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "11", new Timestamp(System.currentTimeMillis()+110));
        RhythmixEventData p3 = Util.genEventData("1", "9", new Timestamp(System.currentTimeMillis()));
        translate.execute(p1);
        boolean execute = translate.execute(p2);
        Assertions.assertTrue(execute);
    }
}