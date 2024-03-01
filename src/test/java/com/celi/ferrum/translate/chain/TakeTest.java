package com.celi.ferrum.translate.chain;

import cii.da.message.codec.model.PointData;
import com.celi.ferrum.exception.LexicalException;
import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.execute.FerrumExecutor;
import com.celi.ferrum.lexer.Lexer;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.pebble.TemplateEngine;
import com.celi.ferrum.translate.ChainExpr;
import com.celi.ferrum.translate.EnvProxy;
import com.celi.ferrum.translate.Translator;
import com.celi.ferrum.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TakeTest {
    @Test
    void translate1() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).take(-3,-1).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p1 = Util.genPointData("1", "0", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        boolean execute =  executor.execute(p2);
        Assertions.assertFalse(execute);
        boolean execute2 = executor.execute(p2);
        Assertions.assertFalse(execute2);
        boolean execute3 = executor.execute(p2);
        Assertions.assertTrue(execute3);
        executor.execute(p2);
        boolean execute1 = executor.execute(p3);
        Assertions.assertFalse(execute1);
    }


    @Test
    void translate2() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).take(0,-1).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p1 = Util.genPointData("1", "0", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        executor.execute(p2);
        executor.execute(p2);
        boolean execute = executor.execute(p2);
        Assertions.assertTrue(execute);
        executor.execute(p2);
        boolean execute1 = executor.execute(p3);
        Assertions.assertFalse(execute1);
    }

    @Test
    void translate3() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).take(-3,5).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p1 = Util.genPointData("1", "0", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        executor.execute(p2);
        executor.execute(p2);
        executor.execute(p2);
        boolean execute = executor.execute(p2);
        Assertions.assertTrue(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertFalse(execute1);
    }


    @Test
    void translate4() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).take(5,3).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p1 = Util.genPointData("1", "0", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            executor.execute(p1);
            executor.execute(p2);
            executor.execute(p2);
        });
    }

    @Test
    void translate5() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).take(-1,-2).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p1 = Util.genPointData("1", "0", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            executor.execute(p1);
            executor.execute(p2);
            executor.execute(p2);
        });
    }

    @Test
    void translate6() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).take(-3).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p1 = Util.genPointData("1", "0", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        executor.execute(p2);
        boolean execute = executor.execute(p2);
        Assertions.assertTrue(execute);
        executor.execute(p2);
        executor.execute(p2);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
    }

    @Test
    void translate7() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).take(2).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p1 = Util.genPointData("1", "0", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        executor.execute(p2);
        executor.execute(p2);
        boolean execute = executor.execute(p2);
        Assertions.assertTrue(execute);
        executor.execute(p2);
        executor.execute(p3);
    }

}