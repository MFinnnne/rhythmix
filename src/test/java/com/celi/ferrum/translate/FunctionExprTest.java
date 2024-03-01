package com.celi.ferrum.translate;

import cii.da.message.codec.model.PointData;
import com.celi.ferrum.exception.LexicalException;
import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.execute.FerrumExecutor;
import com.celi.ferrum.pebble.TemplateEngine;
import com.celi.ferrum.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

class FunctionExprTest {

    @Test
    void translate1() throws LexicalException, ParseException, TranslatorException {
        String code = "count(>4,3)||count(<1,3)";
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        FerrumExecutor translate = new FerrumExecutor(translatedCode,env);
//        System.out.println(translate);
        PointData p1 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "11", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "9", new Timestamp(System.currentTimeMillis()));
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
        FerrumExecutor translate = new FerrumExecutor(translatedCode,env);
//        System.out.println(translate);
        PointData p1 = Util.genPointData("1", "5", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "11", new Timestamp(System.currentTimeMillis()+110));
        PointData p3 = Util.genPointData("1", "9", new Timestamp(System.currentTimeMillis()));
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
        FerrumExecutor translate = new FerrumExecutor(translatedCode,env);
//        System.out.println(translate);
        PointData p1 = Util.genPointData("1", "5", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "11", new Timestamp(System.currentTimeMillis()+110));
        PointData p3 = Util.genPointData("1", "9", new Timestamp(System.currentTimeMillis()));
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
        FerrumExecutor translate = new FerrumExecutor(translatedCode,env);
//        System.out.println(translate);
        PointData p1 = Util.genPointData("1", "5", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "11", new Timestamp(System.currentTimeMillis()+110));
        PointData p3 = Util.genPointData("1", "9", new Timestamp(System.currentTimeMillis()));
        translate.execute(p1);
        boolean execute = translate.execute(p2);
        Assertions.assertTrue(execute);
    }
}