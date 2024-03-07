package com.df.rhythmix.translate.function;

import com.df.rhythmix.util.SensorEvent;
import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.Executor;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.execute.Compiler;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

class SlopeTranslateTest {

    @Test
    void translate() throws LexicalException, ParseException, TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "slope(>4)";
        Executor compile = Compiler.compile(code);
        SensorEvent p1 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        SensorEvent p2 = Util.genPointData("1", "11", new Timestamp(System.currentTimeMillis()+100));
        SensorEvent p3 = Util.genPointData("1", "9", new Timestamp(System.currentTimeMillis()+200));
        boolean execute = compile.execute(p1, p2);
        Assertions.assertFalse(execute);
        code = "slope(>4,50ms)";
        Executor c = Compiler.compile(code);
        boolean execute1 = c.execute(p1, p2);
        Assertions.assertTrue(execute1);
    }
}