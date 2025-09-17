package com.df.rhythmix.translate.chain;

import com.df.rhythmix.execute.RhythmixCompiler;
import com.df.rhythmix.execute.RhythmixExecutor;
import com.df.rhythmix.util.RhythmixEventData;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

class MeetTest {

    @Test
    void translate() throws TranslatorException{
        TemplateEngine.enableDebugModel(true);
        String code = "take(0,1).limit(100).sum().meet((<5||(8,12])&&!=10)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);
        RhythmixEventData p2 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p4 = Util.genEventData("1", "11", new Timestamp(System.currentTimeMillis()));
        boolean execute = rhythmixExecutor.execute(p2);
        Assertions.assertTrue(execute);
        boolean execute1 = rhythmixExecutor.execute(p3);
        Assertions.assertFalse(execute1);
        boolean execute2 = rhythmixExecutor.execute(p4);
        Assertions.assertFalse(execute2);
    }
}