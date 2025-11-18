package io.github.mfinnnne.rhythmix.monitor;

import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.execute.RhythmixCompiler;
import io.github.mfinnnne.rhythmix.execute.RhythmixExecutor;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import io.github.mfinnnne.rhythmix.util.Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for RhythmixExecutor monitoring functionality.
 *
 * @author MFine
 * @version 1.0
 * @since 1.1.0
 */
@DisplayName("RhythmixExecutor Monitoring Tests")
class ExecutorRhythmixExecutionDataTest {

    @Test
    @DisplayName("Test basic monitoring with simple arrow expression")
    void testBasicMonitoring() throws TranslatorException {
        // Compile with monitoring (automatically enabled)
        String expression = "{>5}->{<3}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Create test data
        RhythmixEventData event1 = Util.genEventData("event1", "6", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData event2 = Util.genEventData("event2", "2", new Timestamp(System.currentTimeMillis() + 100));

        // Execute events
        boolean result1 = executor.execute(event1);
        assertFalse(result1, "First event should not complete the flow");

        boolean result2 = executor.execute(event2);
        assertTrue(result2, "Second event should complete the flow");

        // Get monitoring data
        ExecutionMonitorData data = executor.getMonitoringData();

        // Verify statistics
        assertEquals(2, data.getTotalEventsProcessed(), "Should have processed 2 events");
        assertEquals(1, data.getSuccessfulMatches(), "Should have 1 successful match");
        assertEquals(1, data.getFailedMatches(), "Should have 1 failed match");
        assertEquals(50.0, data.getSuccessRate(), 0.01, "Success rate should be 50%");

        // Verify execution records
        assertEquals(2, data.getExecutionRecords().size(), "Should have 2 execution records");

        // Verify state flow metadata
        StateFlowMetadata metadata = data.getStateFlowMetadata();
        assertEquals(expression, metadata.getExpression());
        assertEquals(2, metadata.getTotalStates());
        assertEquals("{>5}", metadata.getStateUnitAtPosition(0));
        assertEquals("{<3}", metadata.getStateUnitAtPosition(1));
    }

    @Test
    @DisplayName("Test monitoring with three-state arrow expression")
    void testThreeStateMonitoring() throws TranslatorException {
        String expression = "{>10}->{<5}->{==3}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Create test data
        RhythmixEventData event1 = Util.genEventData("e1", "15", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData event2 = Util.genEventData("e2", "4", new Timestamp(System.currentTimeMillis() + 100));
        RhythmixEventData event3 = Util.genEventData("e3", "3", new Timestamp(System.currentTimeMillis() + 200));

        // Execute events
        executor.execute(event1);
        executor.execute(event2);
        boolean result = executor.execute(event3);

        assertTrue(result, "Should complete the three-state flow");

        // Get monitoring data
        ExecutionMonitorData data = executor.getMonitoringData();

        assertEquals(3, data.getTotalEventsProcessed());
        assertEquals(1, data.getSuccessfulMatches());
        assertEquals(2, data.getFailedMatches());

        // Verify state metadata
        assertEquals(3, data.getStateFlowMetadata().getTotalStates());
    }

    @Test
    @DisplayName("Test monitoring with max records limit")
    void testMaxRecordsLimit() throws TranslatorException {
        String expression = "{>0}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);
        executor.setMaxRecords(5); // Set max 5 records

        // Execute 10 events
        for (int i = 0; i < 10; i++) {
            RhythmixEventData event = Util.genEventData("event" + i, String.valueOf(i + 1),
                    new Timestamp(System.currentTimeMillis() + i * 100));
            executor.execute(event);
        }

        // Get monitoring data
        ExecutionMonitorData data = executor.getMonitoringData();

        // Should have processed 10 events
        assertEquals(10, data.getTotalEventsProcessed());

        // But only keep 5 records (FIFO)
        assertEquals(5, data.getExecutionRecords().size());

        // Verify the records are the most recent ones (events 5-9)
        List<ExecutionRecord> records = data.getExecutionRecords();
        assertEquals("event5", records.get(0).getEventId());
        assertEquals("event9", records.get(4).getEventId());
    }

    @Test
    @DisplayName("Test reset functionality")
    void testReset() throws TranslatorException {
        String expression = "{>5}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Execute some events
        executor.execute(Util.genEventData("e1", "6", new Timestamp(System.currentTimeMillis())));
        executor.execute(Util.genEventData("e2", "7", new Timestamp(System.currentTimeMillis() + 100)));

        // Verify data exists
        ExecutionMonitorData dataBefore = executor.getMonitoringData();
        assertEquals(2, dataBefore.getTotalEventsProcessed());

        // Reset
        executor.resetMonitoring();

        // Verify data is cleared
        ExecutionMonitorData dataAfter = executor.getMonitoringData();
        assertEquals(0, dataAfter.getTotalEventsProcessed());
        assertEquals(0, dataAfter.getExecutionRecords().size());
        assertEquals(0, dataAfter.getSuccessfulMatches());
        assertEquals(0, dataAfter.getFailedMatches());
    }

    @Test
    @DisplayName("Test timing measurements")
    void testTimingMeasurements() throws TranslatorException {
        String expression = "{>0}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Execute an event
        RhythmixEventData event = Util.genEventData("e1", "5", new Timestamp(System.currentTimeMillis()));
        executor.execute(event);

        // Get monitoring data
        ExecutionMonitorData data = executor.getMonitoringData();

        // Verify timing data exists
        assertNotNull(data.getTotalExecutionTimeNanos());
        assertNotNull(data.getAverageExecutionTimeNanos());
        assertTrue(data.getTotalExecutionTimeNanos() > 0, "Total execution time should be positive");
        assertTrue(data.getAverageExecutionTimeNanos() > 0, "Average execution time should be positive");

        // Verify execution record has timing data
        ExecutionRecord record = data.getExecutionRecords().get(0);
        assertNotNull(record.getExecutionDurationNanos());
        assertNotNull(record.getExecutionDurationMillis());
        assertTrue(record.getExecutionDurationNanos() > 0);
    }

    @Test
    @DisplayName("Test state position tracking")
    void testStatePositionTracking() throws TranslatorException {
        String expression = "{>5}->{<3}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Execute first event (should be at position 0)
        RhythmixEventData event1 = Util.genEventData("e1", "6", new Timestamp(System.currentTimeMillis()));
        executor.execute(event1);

        ExecutionMonitorData data1 = executor.getMonitoringData();
        ExecutionRecord record1 = data1.getExecutionRecords().get(0);

        // First event should be at position 0 or 1 depending on whether it matched
        assertNotNull(record1.getCurrentStatePosition());
        assertEquals("{>5}", record1.getStateUnitAtPosition());

        // Execute second event (should be at position 1)
        RhythmixEventData event2 = Util.genEventData("e2", "2", new Timestamp(System.currentTimeMillis() + 100));
        executor.execute(event2);

        ExecutionMonitorData data2 = executor.getMonitoringData();
        ExecutionRecord record2 = data2.getExecutionRecords().get(1);

        assertNotNull(record2.getCurrentStatePosition());
        // After successful completion, position should reset
    }

    @Test
    @DisplayName("Test batch execution")
    void testBatchExecution() throws TranslatorException {
        String expression = "{>5}->{<3}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Create test data
        RhythmixEventData event1 = Util.genEventData("e1", "6", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData event2 = Util.genEventData("e2", "2", new Timestamp(System.currentTimeMillis() + 100));

        // Execute batch
        boolean result = executor.execute(event1, event2);
        assertTrue(result);

        // Verify both events were recorded
        ExecutionMonitorData data = executor.getMonitoringData();
        assertEquals(2, data.getTotalEventsProcessed());
        assertEquals(2, data.getExecutionRecords().size());
    }

    @Test
    @DisplayName("Test report generation")
    void testReportGeneration() throws TranslatorException {
        String expression = "{>5}->{<3}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Execute some events
        executor.execute(Util.genEventData("e1", "6", new Timestamp(System.currentTimeMillis())));
        executor.execute(Util.genEventData("e2", "2", new Timestamp(System.currentTimeMillis() + 100)));

        // This should not throw an exception
        assertDoesNotThrow(() -> executor.printReport());
    }

    @Test
    @DisplayName("Test single state expression")
    void testSingleStateExpression() throws TranslatorException {
        String expression = ">5";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Execute event
        RhythmixEventData event = Util.genEventData("e1", "6", new Timestamp(System.currentTimeMillis()));
        boolean result = executor.execute(event);

        assertTrue(result);

        // Verify state metadata
        StateFlowMetadata metadata = executor.getMonitoringData().getStateFlowMetadata();
        assertEquals(1, metadata.getTotalStates());
        assertEquals(expression, metadata.getExpression());
    }
}

