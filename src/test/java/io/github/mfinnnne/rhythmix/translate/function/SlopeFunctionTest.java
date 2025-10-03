package io.github.mfinnnne.rhythmix.translate.function;

import io.github.mfinnnne.rhythmix.execute.RhythmixExecutor;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.pebble.TemplateEngine;
import io.github.mfinnnne.rhythmix.execute.RhythmixCompiler;
import io.github.mfinnnne.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

class SlopeFunctionTest {

    @Test
    void translate() throws  TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "slope(>4)";
        RhythmixExecutor compile = RhythmixCompiler.compile(code);
        RhythmixEventData p1 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "11", new Timestamp(System.currentTimeMillis()+100));
        RhythmixEventData p3 = Util.genEventData("1", "9", new Timestamp(System.currentTimeMillis()+200));
        boolean execute = compile.execute(p1, p2);
        Assertions.assertFalse(execute);
        code = "slope(>4,50ms)";
        RhythmixExecutor c = RhythmixCompiler.compile(code);
        boolean execute1 = c.execute(p1, p2);
        Assertions.assertTrue(execute1);
    }
}