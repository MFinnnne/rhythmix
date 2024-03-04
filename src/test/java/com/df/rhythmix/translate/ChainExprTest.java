package com.df.rhythmix.translate;

import com.df.rhythmix.util.SensorEvent;
import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.FerrumExecutor;
import com.df.rhythmix.lib.Register;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Timestamp;

class ChainExprTest {
    @BeforeAll
    static void beforeAll() {
        Register.importFunction();
    }


    @Test
    void translate() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(5).take(0,2).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        SensorEvent p1 = Util.genPointData("1", "0", new Timestamp(System.currentTimeMillis()));
        SensorEvent p2 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        SensorEvent p3 = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p1);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
        executor.execute(p2);
        boolean execute2 = executor.execute(p2);
        Assertions.assertTrue(execute2);

        executor.execute(p1);
        executor.execute(p1);
        executor.execute(p1);
        executor.execute(p1);
        boolean execute3 = executor.execute(p1);
        Assertions.assertFalse(execute3);

        executor.execute(p2);
        boolean execute4 = executor.execute(p2);
        Assertions.assertFalse(execute4);
        executor.execute(p2);
        executor.execute(p2);
        boolean execute5 = executor.execute(p2);
        Assertions.assertTrue(execute5);
    }

    @Test
    void translate2() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).collect().limit(500ms).take(-3,-1).sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        SensorEvent p1 = Util.genPointData("1", "0", new Timestamp(System.currentTimeMillis()));
        SensorEvent p2 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        SensorEvent p3 = Util.genPointData("1", "2", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        executor.execute(p2);
        boolean execute = executor.execute(p3);
    }

    @Test
    void translate3() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "collect().limit(500ms).take(0,3).stddev().meet(==1.414)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        SensorEvent p1 = Util.genPointData("1", "10", new Timestamp(System.currentTimeMillis()));
        SensorEvent p2 = Util.genPointData("1", "7", new Timestamp(System.currentTimeMillis()));
        SensorEvent p3 = Util.genPointData("1", "10", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        executor.execute(p2);
        boolean execute = executor.execute(p3);
        Assertions.assertTrue(execute);
    }

    @Test
    void translate4() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "collect().count().meet(==2)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        SensorEvent p1 = Util.genPointData("1", "10", new Timestamp(System.currentTimeMillis()));
        SensorEvent p2 = Util.genPointData("1", "7", new Timestamp(System.currentTimeMillis()));
        SensorEvent p3 = Util.genPointData("1", "10", new Timestamp(System.currentTimeMillis()));
        executor.execute(p1);
        boolean execute1 = executor.execute(p2);
        Assertions.assertTrue(execute1);
        boolean execute = executor.execute(p3);
        Assertions.assertFalse(execute);
    }
}