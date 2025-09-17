package com.df.rhythmix.translate.chain;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.RhythmixExecutor;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import com.df.rhythmix.util.RhythmixEventData;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

public class HitRateTest {
    @Test
    void test1() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);

        String code = "hitRate((1,5)).meet((>=0.5))";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode,env);;
        RhythmixEventData p1 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "7", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        boolean execute2 = rhythmixExecutor.execute(p1);
        Assertions.assertTrue(execute2);
        boolean execute1 = rhythmixExecutor.execute(p2);
        Assertions.assertFalse(execute1);
        boolean execute = rhythmixExecutor.execute(p3);
        Assertions.assertFalse(execute);
    }
}
