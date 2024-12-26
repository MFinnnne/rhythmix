package com.df.rhythmix.translate.chain;

import com.df.rhythmix.util.EventData;
import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.Executor;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Timestamp;

class CollectTest {
    @Test
    void test() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "collect().sum().meet(>1)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        Executor executor = new Executor(transCode,env);;
        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        EventData p3 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        EventData p4 = Util.genEventData("1", "5", new Timestamp(System.currentTimeMillis()));
        boolean execute3 = executor.execute(p1);
        Assertions.assertFalse(execute3);
        boolean execute = executor.execute(p2);
        Assertions.assertFalse(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertTrue(execute1);
        boolean execute2 = executor.execute(p4);
        Assertions.assertTrue(execute2);
    }
}