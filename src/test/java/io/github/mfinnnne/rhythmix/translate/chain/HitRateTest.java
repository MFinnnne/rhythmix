package io.github.mfinnnne.rhythmix.translate.chain;

import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.execute.RhythmixCompiler;
import io.github.mfinnnne.rhythmix.execute.RhythmixExecutor;
import io.github.mfinnnne.rhythmix.pebble.TemplateEngine;
import io.github.mfinnnne.rhythmix.translate.EnvProxy;
import io.github.mfinnnne.rhythmix.translate.Translator;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import io.github.mfinnnne.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

public class HitRateTest {
    @Test
    void test1() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);

        String code = "hitRate((1,5)).meet((>=0.5))";

        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);
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
