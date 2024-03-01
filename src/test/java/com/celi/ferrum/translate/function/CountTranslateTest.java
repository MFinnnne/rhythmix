package com.celi.ferrum.translate.function;

import cii.da.message.codec.model.PointData;
import com.celi.ferrum.exception.LexicalException;
import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.execute.FerrumExecutor;
import com.celi.ferrum.pebble.TemplateEngine;
import com.celi.ferrum.translate.EnvProxy;
import com.celi.ferrum.translate.Translator;
import com.celi.ferrum.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class CountTranslateTest {

    @Test
    void translate() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "count!(>4,3)";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        FerrumExecutor translate = new FerrumExecutor(translatedCode, env);
        PointData p1 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "11", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "9", new Timestamp(System.currentTimeMillis()));
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
        PointData p1 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "11", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "9", new Timestamp(System.currentTimeMillis()));
        translate.execute(p2);
        boolean execute2 = translate.execute(p2);
        Assertions.assertTrue(execute2);
        boolean execute3 = translate.execute(p1);
        Assertions.assertFalse(execute3);
    }
}