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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
public class HitRateTest {
    @Test
    void test1() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);

        String code = "collect().hitRate((1,5)).meet((>=0.5))";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        FerrumExecutor executor = new FerrumExecutor(transCode,env);;
        PointData p1 = Util.genPointData("1", "3", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "7", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "10", new Timestamp(System.currentTimeMillis()));
        boolean execute2 = executor.execute(p1);
        Assertions.assertTrue(execute2);
        boolean execute1 = executor.execute(p2);
        Assertions.assertFalse(execute1);
        boolean execute = executor.execute(p3);
        Assertions.assertFalse(execute);
    }
}
