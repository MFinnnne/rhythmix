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

class FilterTest {
    @Test
    void test1() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(>3).collect().sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p1 = Util.genPointData("1", "0", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        PointData p4 = Util.genPointData("1", "5", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        executor.execute(p2);
        boolean execute = executor.execute(p3);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p4);
        Assertions.assertTrue(execute1);
        boolean execute2 = executor.execute(p4);
        Assertions.assertTrue(execute2);
    }

    @Test
    void test2() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(>=3).collect().sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p1 = Util.genPointData("1", "0", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        PointData p4 = Util.genPointData("1", "5", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        executor.execute(p2);
        boolean execute = executor.execute(p3);
        Assertions.assertTrue(execute);
        executor.execute(p4);
        executor.execute(p4);
    }

    @Test
    void test3() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(<3).collect().sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p1 = Util.genPointData("1", "0", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        PointData p4 = Util.genPointData("1", "5", new Timestamp(System.currentTimeMillis()));
        PointData p5 = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p4);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p1);
        Assertions.assertFalse(execute1);
        boolean execute2 = executor.execute(p2);
        Assertions.assertFalse(execute2);
        boolean execute3 = executor.execute(p3);
        Assertions.assertFalse(execute3);
        boolean execute4 = executor.execute(p5);
        Assertions.assertTrue(execute4);
    }

    @Test
    void test4() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(<=3).collect().sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p1 = Util.genPointData("1", "0", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        PointData p4 = Util.genPointData("1", "5", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p4);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p1);
        Assertions.assertFalse(execute1);
        executor.execute(p2);
        boolean execute2 = executor.execute(p3);
        Assertions.assertTrue(execute2);
        executor.execute(p4);
    }


    @Test
    void test5() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(==3).collect().sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p1 = Util.genPointData("1", "0", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        PointData p4 = Util.genPointData("1", "5", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p4);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
    }

    @Test
    void test6() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(!=3).collect().sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p3 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        PointData p4 = Util.genPointData("1", "5", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p3);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p4);
        Assertions.assertTrue(execute1);
    }


    @Test
    void test7() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((1,4)).collect().sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p3 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        PointData p4 = Util.genPointData("1", "5", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p4);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
    }

    @Test
    void test8() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((1,4]).collect().sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p2 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "4", new Timestamp(System.currentTimeMillis()));
        PointData p4 = Util.genPointData("1", "5", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p4);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
        boolean execute2 = executor.execute(p2);
        Assertions.assertTrue(execute2);
    }


    @Test
    void test9() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(((1,7]||>10)&&!=5).collect().sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p2 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "4", new Timestamp(System.currentTimeMillis()));
        PointData p4 = Util.genPointData("1", "5", new Timestamp(System.currentTimeMillis()));
        PointData p5 = Util.genPointData("1", "8", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p5);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
        boolean execute2 = executor.execute(p2);
        Assertions.assertTrue(execute2);
        boolean execute3 = executor.execute(p4);
        Assertions.assertFalse(execute3);
    }

    @Test
    void test10() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((1,6)).collect().sum().meet(>1)||filter(>9).collect().sum().meet((12,30))";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p2 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "4", new Timestamp(System.currentTimeMillis()));
        PointData p4 = Util.genPointData("1", "5", new Timestamp(System.currentTimeMillis()));
        PointData p5 = Util.genPointData("1", "8", new Timestamp(System.currentTimeMillis()));
        PointData p6 = Util.genPointData("1", "10", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p5);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
        boolean execute2 = executor.execute(p2);
        Assertions.assertTrue(execute2);
        boolean execute3 = executor.execute(p4);
        Assertions.assertTrue(execute3);
        boolean execute4 = executor.execute(p6);
        Assertions.assertFalse(execute4);
        boolean execute5 = executor.execute(p6);
        Assertions.assertTrue(execute5);
    }
}