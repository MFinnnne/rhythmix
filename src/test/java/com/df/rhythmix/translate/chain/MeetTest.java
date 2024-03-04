package com.df.rhythmix.translate.chain;

import com.df.rhythmix.util.SensorEvent;
import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.FerrumExecutor;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Timestamp;

class MeetTest {

    @Test
    void translate() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "collect().limit(100).take(0,1).sum().meet((<5||(8,12])&&!=10)";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        SensorEvent p2 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        SensorEvent p3 = Util.genPointData("1", "10", new Timestamp(System.currentTimeMillis()));
        SensorEvent p4 = Util.genPointData("1", "11", new Timestamp(System.currentTimeMillis()));
        boolean execute = executor.execute(p2);
        Assertions.assertTrue(execute);
        boolean execute1 = executor.execute(p3);
        Assertions.assertFalse(execute1);
        boolean execute2 = executor.execute(p4);
        Assertions.assertFalse(execute2);
    }
}