package io.github.mfinnnne.rhythmix.translate.chain;

import io.github.mfinnnne.rhythmix.exception.LexicalException;
import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.execute.RhythmixCompiler;
import io.github.mfinnnne.rhythmix.execute.RhythmixExecutor;
import io.github.mfinnnne.rhythmix.lib.Register;
import io.github.mfinnnne.rhythmix.pebble.TemplateEngine;
import io.github.mfinnnne.rhythmix.translate.EnvProxy;
import io.github.mfinnnne.rhythmix.translate.Translator;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import io.github.mfinnnne.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for the Clear post-processing operator.
 *
 * @author MFine
 */
public class ClearTest {

    @BeforeAll
    static void beforeAll() {
        Register.importFunction();
    }

    @Test
    @DisplayName("Test clear operator after meet")
    void testClearOperatorAfterMeet() throws LexicalException, ParseException, TranslatorException, IOException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).limit(5).sum().meet(>1).clear()";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode, env);

        RhythmixEventData p1 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        // First execution: sum = 1, not > 1, should return false
        boolean execute1 = rhythmixExecutor.execute(p1);
        Assertions.assertFalse(execute1);
        List<String> queueData = Util.getRawProcessedQueueData(rhythmixExecutor);
        assertThat(queueData).isNotEmpty();
        // Second execution: sum = 3, > 1, should return true and clear queues
        boolean execute2 = rhythmixExecutor.execute(p2);
        Assertions.assertTrue(execute2);
        queueData = Util.getRawProcessedQueueData(rhythmixExecutor);
        assertThat(queueData).isEmpty();
        // After clear, the queues should be empty, so next execution starts fresh
        // Third execution: sum = 1, not > 1, should return false
        boolean execute3 = rhythmixExecutor.execute(p1);
        Assertions.assertFalse(execute3);
        queueData = Util.getRawProcessedQueueData(rhythmixExecutor);
        assertThat(queueData).isNotEmpty();
    }

    @Test
    @DisplayName("Test clear operator translation")
    void testClearOperatorTranslation() throws TranslatorException {
        String code = "count().meet(>5).clear()";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);
        Assertions.assertNotNull(rhythmixExecutor);
    }

    @Test
    @DisplayName("Test clear operator must be at the end")
    void testClearOperatorMustBeAtEnd() {
        // clear() can only appear at the end of a chain expression
        String code = "filter().clear().sum().meet(>1)";
        Assertions.assertThrows(TranslatorException.class, () -> {
            RhythmixCompiler.compile(code);
        });
    }

    @Test
    @DisplayName("Test clear operator after different end operators")
    void testClearAfterDifferentEndOperators() throws TranslatorException {
        // Test clear after meet
        String code1 = "filter().sum().meet(>1).clear()";
        RhythmixExecutor executor1 = RhythmixCompiler.compile(code1);
        Assertions.assertNotNull(executor1);
    }
}

