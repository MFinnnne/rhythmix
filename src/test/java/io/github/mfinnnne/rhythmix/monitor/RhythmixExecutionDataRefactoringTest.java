package io.github.mfinnnne.rhythmix.monitor;

import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.execute.RhythmixCompiler;
import io.github.mfinnnne.rhythmix.execute.RhythmixExecutor;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import io.github.mfinnnne.rhythmix.util.Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify the Monitor refactoring works correctly.
 *
 * @author MFine
 * @version 1.0
 * @since 1.1.0
 */
@DisplayName("Monitor Refactoring Verification Tests")
class RhythmixExecutionDataRefactoringTest {

    @Test
    @DisplayName("Verify monitoring works after refactoring")
    void testMonitoringAfterRefactoring() throws TranslatorException {
        // Compile executor
        String expression = "{>5}->{<3}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Verify executor is created
        assertNotNull(executor);

        // Execute events
        RhythmixEventData event1 = Util.genEventData("e1", "6", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData event2 = Util.genEventData("e2", "2", new Timestamp(System.currentTimeMillis() + 100));

        boolean result1 = executor.execute(event1);
        assertFalse(result1);

        boolean result2 = executor.execute(event2);
        assertTrue(result2);

        // Verify monitoring data is accessible
        ExecutionMonitorData data = executor.getMonitoringData();
        assertNotNull(data);
        assertEquals(2, data.getTotalEventsProcessed());
        assertEquals(1, data.getSuccessfulMatches());
        assertEquals(1, data.getFailedMatches());

        // Verify state flow metadata
        StateFlowMetadata metadata = executor.getStateFlowMetadata();
        assertNotNull(metadata);
        assertEquals(2, metadata.getTotalStates());
        assertEquals("{>5}", metadata.getStateUnitAtPosition(0));
        assertEquals("{<3}", metadata.getStateUnitAtPosition(1));

        // Verify expression is accessible
        String retrievedExpression = executor.getExpression();
        assertEquals(expression, retrievedExpression);

        // Verify record count
        assertEquals(2, executor.getRecordCount());

        // Test setMaxRecords
        executor.setMaxRecords(1);
        RhythmixEventData event3 = Util.genEventData("e3", "7", new Timestamp(System.currentTimeMillis() + 200));
        executor.execute(event3);

        // Should only have 1 record now (FIFO)
        assertEquals(1, executor.getRecordCount());

        // Test reset
        executor.resetMonitoring();
        ExecutionMonitorData dataAfterReset = executor.getMonitoringData();
        assertEquals(0, dataAfterReset.getTotalEventsProcessed());
        assertEquals(0, executor.getRecordCount());

        // Test printReport (should not throw exception)
        assertDoesNotThrow(executor::printReport);
    }

    @Test
    @DisplayName("Verify backward compatibility with old constructor")
    void testBackwardCompatibility() throws TranslatorException {
        // Test that old code still works
        String expression = ">10";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        RhythmixEventData event = Util.genEventData("e1", "15", new Timestamp(System.currentTimeMillis()));
        boolean result = executor.execute(event);

        assertTrue(result);

        // Monitoring should still work
        ExecutionMonitorData data = executor.getMonitoringData();
        assertNotNull(data);
        assertEquals(1, data.getTotalEventsProcessed());
    }

    @Test
    @DisplayName("Verify Monitor class encapsulation")
    void testMonitorEncapsulation() throws TranslatorException {
        String expression = "{>0}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Execute some events
        for (int i = 0; i < 5; i++) {
            RhythmixEventData event = Util.genEventData("e" + i, String.valueOf(i + 1),
                    new Timestamp(System.currentTimeMillis() + i * 100));
            executor.execute(event);
        }

        // Verify all monitoring methods work through delegation
        ExecutionMonitorData data = executor.getMonitoringData();
        assertEquals(5, data.getTotalEventsProcessed());
        assertEquals(5, data.getSuccessfulMatches());
        assertEquals(0, data.getFailedMatches());

        // Verify timing data
        assertNotNull(data.getTotalExecutionTimeNanos());
        assertNotNull(data.getAverageExecutionTimeNanos());
        assertTrue(data.getTotalExecutionTimeNanos() > 0);
        assertTrue(data.getAverageExecutionTimeNanos() > 0);
    }
}

