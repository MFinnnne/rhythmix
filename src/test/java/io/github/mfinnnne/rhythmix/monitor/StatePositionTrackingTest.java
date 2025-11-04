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
 * Test class to verify state position tracking before and after execution.
 *
 * @author MFine
 * @version 1.0
 * @since 1.1.0
 */
@DisplayName("State Position Tracking Tests")
class StatePositionTrackingTest {

    @Test
    @DisplayName("Test state position tracking in two-state arrow expression")
    void testTwoStatePositionTracking() throws TranslatorException {
        // Expression: {>5}->{<3}
        // State 0: {>5}, State 1: {<3}
        String expression = "{>5}->{<3}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Event 1: value=6, should match {>5}, transition from state 0 to state 1
        RhythmixEventData event1 = Util.genEventData("e1", "6", new Timestamp(System.currentTimeMillis()));
        boolean result1 = executor.execute(event1);
        assertFalse(result1, "First event should not complete the flow");

        // Verify state positions for event 1
        ExecutionMonitorData data1 = executor.getMonitoringData();
        List<ExecutionRecord> records1 = data1.getExecutionRecords();
        assertEquals(1, records1.size());

        ExecutionRecord record1 = records1.get(0);
        assertEquals(0, record1.getCurrentStatePosition(), "Event 1 should start at position 0");
        assertEquals(1, record1.getStatePositionAfterExecution(), "Event 1 should transition to position 1");
        assertEquals("{>5}", record1.getStateUnitAtPosition());

        // Event 2: value=2, should match {<3}, complete the flow and reset to state 0
        RhythmixEventData event2 = Util.genEventData("e2", "2", new Timestamp(System.currentTimeMillis() + 100));
        boolean result2 = executor.execute(event2);
        assertTrue(result2, "Second event should complete the flow");

        // Verify state positions for event 2
        ExecutionMonitorData data2 = executor.getMonitoringData();
        List<ExecutionRecord> records2 = data2.getExecutionRecords();
        assertEquals(2, records2.size());

        ExecutionRecord record2 = records2.get(1);
        assertEquals(1, record2.getCurrentStatePosition(), "Event 2 should start at position 1");
        assertEquals(0, record2.getStatePositionAfterExecution(), "Event 2 should reset to position 0 after completion");
    }

    @Test
    @DisplayName("Test state position tracking in three-state arrow expression")
    void testThreeStatePositionTracking() throws TranslatorException {
        // Expression: {>10}->{<5}->{==3}
        // State 0: {>10}, State 1: {<5}, State 2: {==3}
        String expression = "{>10}->{<5}->{==3}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Event 1: value=15, should match {>10}, transition 0->1
        RhythmixEventData event1 = Util.genEventData("e1", "15", new Timestamp(System.currentTimeMillis()));
        executor.execute(event1);

        ExecutionRecord record1 = executor.getMonitoringData().getExecutionRecords().get(0);
        assertEquals(0, record1.getCurrentStatePosition());
        assertEquals(1, record1.getStatePositionAfterExecution());

        // Event 2: value=4, should match {<5}, transition 1->2
        RhythmixEventData event2 = Util.genEventData("e2", "4", new Timestamp(System.currentTimeMillis() + 100));
        executor.execute(event2);

        ExecutionRecord record2 = executor.getMonitoringData().getExecutionRecords().get(1);
        assertEquals(1, record2.getCurrentStatePosition());
        assertEquals(2, record2.getStatePositionAfterExecution());

        // Event 3: value=3, should match {==3}, complete and reset 2->0
        RhythmixEventData event3 = Util.genEventData("e3", "3", new Timestamp(System.currentTimeMillis() + 200));
        boolean result3 = executor.execute(event3);
        assertTrue(result3);

        ExecutionRecord record3 = executor.getMonitoringData().getExecutionRecords().get(2);
        assertEquals(2, record3.getCurrentStatePosition());
        assertEquals(0, record3.getStatePositionAfterExecution());
    }

    @Test
    @DisplayName("Test state position when event does not match")
    void testStatePositionNoMatch() throws TranslatorException {
        String expression = "{>5}->{<3}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Event 1: value=6, matches {>5}, transition 0->1
        RhythmixEventData event1 = Util.genEventData("e1", "6", new Timestamp(System.currentTimeMillis()));
        executor.execute(event1);

        // Event 2: value=10, does NOT match {<3}, should stay at position 1
        RhythmixEventData event2 = Util.genEventData("e2", "10", new Timestamp(System.currentTimeMillis() + 100));
        boolean result2 = executor.execute(event2);
        assertFalse(result2);

        ExecutionRecord record2 = executor.getMonitoringData().getExecutionRecords().get(1);
        assertEquals(1, record2.getCurrentStatePosition(), "Should be at position 1 before execution");
        assertEquals(1, record2.getStatePositionAfterExecution(), "Should remain at position 1 after failed match");
    }

    @Test
    @DisplayName("Test state position in single state expression")
    void testSingleStatePosition() throws TranslatorException {
        String expression = ">5";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Event: value=10, should match and reset
        RhythmixEventData event = Util.genEventData("e1", "10", new Timestamp(System.currentTimeMillis()));
        boolean result = executor.execute(event);
        assertTrue(result);

        ExecutionRecord record = executor.getMonitoringData().getExecutionRecords().get(0);
        assertEquals(0, record.getCurrentStatePosition());
        assertEquals(0, record.getStatePositionAfterExecution());
    }

    @Test
    @DisplayName("Test toString includes state transition")
    void testToStringWithStateTransition() throws TranslatorException {
        String expression = "{>5}->{<3}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        RhythmixEventData event = Util.genEventData("e1", "6", new Timestamp(System.currentTimeMillis()));
        executor.execute(event);

        ExecutionRecord record = executor.getMonitoringData().getExecutionRecords().get(0);
        String recordString = record.toString();

        // Verify the toString includes the state transition (0->1)
        assertTrue(recordString.contains("0->1"), "toString should show state transition");
        assertTrue(recordString.contains("eventId='e1'"), "toString should include event ID");
    }
}

