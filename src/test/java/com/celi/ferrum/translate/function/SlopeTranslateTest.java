package com.celi.ferrum.translate.function;

import cii.da.message.codec.model.PointData;
import com.celi.ferrum.exception.LexicalException;
import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.execute.FerrumExecutor;
import com.celi.ferrum.pebble.TemplateEngine;
import com.celi.ferrum.execute.FerrumCompiler;
import com.celi.ferrum.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

class SlopeTranslateTest {

    @Test
    void translate() throws LexicalException, ParseException, TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "slope(>4)";
        FerrumExecutor compile = FerrumCompiler.compile(code);
        PointData p1 = Util.genPointData("1", "1", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "11", new Timestamp(System.currentTimeMillis()+100));
        PointData p3 = Util.genPointData("1", "9", new Timestamp(System.currentTimeMillis()+200));
        boolean execute = compile.execute(p1, p2);
        Assertions.assertFalse(execute);
        code = "slope(>4,50ms)";
        FerrumExecutor c = FerrumCompiler.compile(code);
        boolean execute1 = c.execute(p1, p2);
        Assertions.assertTrue(execute1);
    }
}