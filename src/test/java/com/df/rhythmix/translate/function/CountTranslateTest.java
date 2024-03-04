package com.df.rhythmix.translate.function;

import com.df.rhythmix.util.SensorEvent;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.FerrumExecutor;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

class CountTranslateTest {

    @Test
    void translate() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "count!(>4,3)";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        FerrumExecutor translate = new FerrumExecutor(translatedCode, env);
        SensorEvent p1 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        SensorEvent p2 = Util.genPointData("1", "11", new Timestamp(System.currentTimeMillis()));
        SensorEvent p3 = Util.genPointData("1", "9", new Timestamp(System.currentTimeMillis()));
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
        FerrumExecutor translate = new FerrumExecutor(translatedCode, env);
        SensorEvent p1 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        SensorEvent p2 = Util.genPointData("1", "11", new Timestamp(System.currentTimeMillis()));
        SensorEvent p3 = Util.genPointData("1", "9", new Timestamp(System.currentTimeMillis()));
        translate.execute(p2);
        boolean execute2 = translate.execute(p2);
        Assertions.assertTrue(execute2);
        boolean execute3 = translate.execute(p1);
        Assertions.assertFalse(execute3);
    }
}