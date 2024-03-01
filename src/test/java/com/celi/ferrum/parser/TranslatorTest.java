package com.celi.ferrum.parser;

import cii.da.message.codec.model.PointData;
import cn.hutool.core.util.RandomUtil;
import com.celi.ferrum.exception.LexicalException;
import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.execute.FerrumExecutor;
import com.celi.ferrum.lexer.Lexer;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.pebble.TemplateEngine;
import com.celi.ferrum.translate.EnvProxy;
import com.celi.ferrum.execute.FerrumCompiler;
import com.celi.ferrum.translate.Translator;
import com.celi.ferrum.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

class TranslatorTest {

    @Test
    void translateGEAndLE() throws LexicalException, TranslatorException, IOException {
        String code1 = "[1,2]";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code1, env).replaceAll("\\r\\n|\\s+","");
        Assertions.assertEquals("usejava.util.*;(long(event.value)>=1&&long(event.value)<=2)", translate);

    }

    @Test
    void translateGEAndLess() throws LexicalException, TranslatorException, IOException {
        String code1 = "[1.0,2.0)";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code1, env).replaceAll("\\r\\n|\\s+","");
        Assertions.assertEquals("usejava.util.*;(double(event.value)>=1.0&&double(event.value)<2.0)", translate);
    }

    @Test
    void translateGreaterAndL() throws LexicalException, TranslatorException, IOException {
        String code1 = "(1,2]";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code1, env).replaceAll("\\r\\n|\\s+","");
        Assertions.assertEquals("usejava.util.*;(long(event.value)>1&&long(event.value)<=2)", translate);
    }

    @Test
    void translateGreaterAndLess() throws LexicalException, TranslatorException, IOException {
        String code1 = "(1,2)";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code1, env).replaceAll("\\r\\n|\\s+","");
        Assertions.assertEquals("usejava.util.*;(long(event.value)>1&&long(event.value)<2)", translate);
    }

    @Test
    void testTranslateMutExprByInteger() throws LexicalException, TranslatorException, IOException {
        String code = "<1,2,3>";
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code, env);
        FerrumExecutor build =new FerrumExecutor(translate,env);
        PointData point1 = new PointData("point_1", "point_1", "1", "point_1", new Timestamp(System.currentTimeMillis()), "point_3", "float", "1",
                "s", "fail", RandomUtil.randomInt(10) + "", "nj", "fail", "k1", "k2", "k3", "k4", "k5");


        env.put("event", point1);
        Object execute = build.execute(point1);
        Assertions.assertFalse(((boolean) execute));

        point1.setValue("2");
        Object execute1 = build.execute(point1);
        Assertions.assertFalse(((boolean) execute1));


        point1.setValue("3");
        Object execute2 = build.execute(point1);
        Assertions.assertTrue(((boolean) execute2));
    }


    @Test
    void testTranslateMutExprByDouble() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "<1.0,2.0,3.0>";
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code, env);
        FerrumExecutor build = new FerrumExecutor(translate,env);
        PointData point1 = new PointData("point_1", "point_1", "1", "point_1", new Timestamp(System.currentTimeMillis()), "point_3",
                "float", "1.0", "s", "fail", RandomUtil.randomInt(10) + "", "nj", "fail", "k1", "k2", "k3", "k4", "k5");
        PointData point2 = new PointData("point_1", "point_1", "1", "point_1", new Timestamp(System.currentTimeMillis()), "point_3",
                "float", "2.0", "s", "fail", RandomUtil.randomInt(10) + "", "nj", "fail", "k1", "k2", "k3", "k4", "k5");
        PointData point3 = new PointData("point_1", "point_1", "1", "point_1", new Timestamp(System.currentTimeMillis()), "point_3",
                "float", "3.0", "s", "fail", RandomUtil.randomInt(10) + "", "nj", "fail", "k1", "k2", "k3", "k4", "k5");
        boolean execute = build.execute(point1);
        Assertions.assertFalse(execute);
        boolean execute1 = build.execute(point2);
        Assertions.assertFalse(execute1);
        boolean execute2 = build.execute(point3);
        Assertions.assertTrue(execute2);

    }

    @Test
    void testTranslateMutExprByString() throws LexicalException, TranslatorException, IOException {
        String code = "<'1','2','3'>";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code, env);
        FerrumExecutor build = new FerrumExecutor(translate,env);
        PointData point1 = new PointData("point_1", "point_1", "1", "point_1", new Timestamp(System.currentTimeMillis()), "point_3",
                "float", "1", "s", "fail", RandomUtil.randomInt(10) + "", "nj", "fail", "k1", "k2", "k3", "k4", "k5");


        env.put("event", point1);
        boolean execute = build.execute(point1);
        Assertions.assertFalse(execute);

        point1.setValue("2");
        boolean execute1 = build.execute(point1);
        Assertions.assertFalse(execute1);


        point1.setValue("3");
        boolean execute2 = build.execute(point1);
        Assertions.assertTrue(execute2);
    }


    @Test
    void shouldThrowTranslateException() throws LexicalException, TranslatorException, IOException {
        Assertions.assertThrows(TranslatorException.class, () -> {
            String code = "asd<'1',2,'3'>";
            Lexer lexer = new Lexer();
            ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
            FerrumExecutor translate = FerrumCompiler.compile(code);
        });
        Assertions.assertThrows(TranslatorException.class, () -> {
            String code = "<,2,'3'>";
            Lexer lexer = new Lexer();
            ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
            FerrumExecutor translate = FerrumCompiler.compile(code);
        });
    }

    @Test
    void translateCompareExpr() throws LexicalException, TranslatorException, IOException {
        String code = ">3";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code, env).replaceAll("\\r\\n|\\s+","");
        Assertions.assertEquals("usejava.util.*;(long(event.value)>3)", translate);
    }

    @Test
    void translateCompareExpr1() throws LexicalException, TranslatorException, IOException {
        String code = ">=3.0";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code, env).replaceAll("\\r\\n|\\s+","");
        Assertions.assertEquals("usejava.util.*;(double(event.value)>=3.0)", translate);
    }

    @Test
    void translateCompareExpr2() throws LexicalException, TranslatorException, IOException {
        String code = "<3.0";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code, env).replaceAll("\\r\\n|\\s+","");
        Assertions.assertEquals("usejava.util.*;(double(event.value)<3.0)", translate);
    }

    @Test
    void translateCompareExpr3() throws LexicalException, TranslatorException, IOException {
        String code = "<=3";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code, env).replaceAll("\\r\\n|\\s+","");
        Assertions.assertEquals("usejava.util.*;(long(event.value)<=3)", translate);
    }


    @Test
    void translateCompareExpr4() throws LexicalException, TranslatorException, IOException {
        String code = "==3.0";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code, env).replaceAll("\\r\\n|\\s+","");
        Assertions.assertEquals("usejava.util.*;(double(event.value)==3.0)", translate);
    }

    @Test
    void translateCompareExpr5() throws LexicalException, TranslatorException, IOException {
        String code = "!=3";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code, env).replaceAll("\\r\\n|\\s+","");
        Assertions.assertEquals("usejava.util.*;(long(event.value)!=3)", translate);
    }


    @Test
    void arrowExprTest1() throws LexicalException, TranslatorException, IOException {
        String code = "{==3}->{==5}";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code, env);
        FerrumExecutor build = new FerrumExecutor(translate,env);

        PointData p1 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(build.execute(p1, p2));
    }

    @Test
    void arrowExprTest2() throws LexicalException, TranslatorException, IOException {
        String code = "{==3}->{==5}";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code, env);
        FerrumExecutor build = new FerrumExecutor(translate,env);
        PointData p1 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(build.execute(p1, p2));
    }

    @Test
    void arrowExprTest3() throws LexicalException, TranslatorException, IOException {
        String code = "{>2}->{>4}";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translate = Translator.translate(code, env);
        FerrumExecutor build = new FerrumExecutor(translate,env);
        PointData p1 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(build.execute(p1, p2));
    }


}