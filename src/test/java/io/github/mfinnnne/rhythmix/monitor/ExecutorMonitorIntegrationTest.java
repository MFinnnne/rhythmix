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
 * Integration tests for RhythmixExecutor monitoring demonstrating real-world scenarios.
 *
 * @author MFine
 * @version 1.0
 * @since 1.1.0
 */
@DisplayName("RhythmixExecutor Monitoring Integration Tests")
class ExecutorMonitorIntegrationTest {

    @Test
    @DisplayName("Temperature Sensor Monitoring - State Transition from Normal to Alert")
    void testTemperatureSensorMonitoring() throws TranslatorException {
        // Scenario: Monitor temperature sensor for state transition from normal (<30) to high (>35)
        String expression = "{<30}->{>35}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Simulate temperature readings
        RhythmixEventData temp1 = RhythmixEventData.builder()
                .name("temperature")
                .value("25.5")
                .ts(new Timestamp(System.currentTimeMillis()))
                .build();

        RhythmixEventData temp2 = RhythmixEventData.builder()
                .name("temperature")
                .value("38.2")
                .ts(new Timestamp(System.currentTimeMillis() + 1000))
                .build();

        // Execute monitoring
        boolean result1 = executor.execute(temp1);
        assertFalse(result1, "First reading should not trigger alert");

        boolean result2 = executor.execute(temp2);
        assertTrue(result2, "Second reading should trigger alert (state transition complete)");

        // Analyze monitoring data
        ExecutionMonitorData data = executor.getMonitoringData();

        assertEquals(2, data.getTotalEventsProcessed());
        assertEquals(1, data.getSuccessfulMatches());
        assertEquals(50.0, data.getSuccessRate(), 0.01);

        // Print report
        System.out.println("\n=== Temperature Sensor Monitoring Report ===");
        executor.printReport();
    }

    @Test
    @DisplayName("Product Quality Control - Three-Stage Inspection")
    void testProductQualityControl() throws TranslatorException {
        // Scenario: Monitor product quality through three stages: initial check, processing, final inspection
        String expression = "{[95,105]}->{[90,110]}->{[95,105]}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);
        executor.setMaxRecords(100);

        // Simulate product measurements
        RhythmixEventData product1 = Util.genEventData("product1", "100", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData product2 = Util.genEventData("product2", "105", new Timestamp(System.currentTimeMillis() + 100));
        RhythmixEventData product3 = Util.genEventData("product3", "98", new Timestamp(System.currentTimeMillis() + 200));

        // Execute monitoring
        executor.execute(product1);
        executor.execute(product2);
        boolean result = executor.execute(product3);

        assertTrue(result, "Product should pass all three quality stages");

        // Analyze monitoring data
        ExecutionMonitorData data = executor.getMonitoringData();

        assertEquals(3, data.getTotalEventsProcessed());
        assertEquals(3, data.getStateFlowMetadata().getTotalStates());

        // Verify each stage was recorded
        List<ExecutionRecord> records = data.getExecutionRecords();
        assertEquals(3, records.size());

        System.out.println("\n=== Product Quality Control Report ===");
        executor.printReport();
    }

    @Test
    @DisplayName("Network Latency Monitoring - Response Time Tracking")
    void testNetworkLatencyMonitoring() throws TranslatorException {
        // Scenario: Monitor network response times for degradation
        String expression = "{<500}->{>1000}";
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);

        // Simulate network response times (in milliseconds)
        RhythmixEventData response1 = Util.genEventData("api_call_1", "150", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData response2 = Util.genEventData("api_call_2", "1200", new Timestamp(System.currentTimeMillis() + 1000));

        // Execute monitoring
        boolean result1 = executor.execute(response1);
        assertFalse(result1, "Normal response time should not trigger alert");

        boolean result2 = executor.execute(response2);
        assertTrue(result2, "High latency should trigger alert");

        // Analyze timing statistics
        ExecutionMonitorData data = executor.getMonitoringData();

        assertNotNull(data.getAverageExecutionTimeMillis());
        assertNotNull(data.getTotalExecutionTimeMillis());
        assertTrue(data.getAverageExecutionTimeMillis() > 0);

        System.out.println("\n=== Network Latency Monitoring Report ===");
        executor.printReport();
    }

    @Test
    @DisplayName("Long-Running Monitoring with Record Limit")
    void testLongRunningMonitoring() throws TranslatorException {
        // Scenario: Monitor a system over time with limited memory
        String expression = "{>50}";
        int maxRecords = 10;
        RhythmixExecutor executor = RhythmixCompiler.compile(expression);
        executor.setMaxRecords(maxRecords);

        // Simulate 100 events
        for (int i = 0; i < 100; i++) {
            RhythmixEventData event = Util.genEventData("event_" + i, String.valueOf(51 + i),
                    new Timestamp(System.currentTimeMillis() + i * 10));
            executor.execute(event);
        }

        // Verify statistics
        ExecutionMonitorData data = executor.getMonitoringData();

        assertEquals(100, data.getTotalEventsProcessed(), "Should track all 100 events");
        assertEquals(maxRecords, data.getExecutionRecords().size(), "Should only keep last 10 records");

        // Verify FIFO behavior - should have events 90-99
        List<ExecutionRecord> records = data.getExecutionRecords();
        assertEquals("event_90", records.get(0).getEventId());
        assertEquals("event_99", records.get(9).getEventId());

        System.out.println("\n=== Long-Running Monitoring Report ===");
        System.out.println("Total events processed: " + data.getTotalEventsProcessed());
        System.out.println("Records kept in memory: " + data.getExecutionRecords().size());
        System.out.println("Average execution time: " + String.format("%.3f ms", data.getAverageExecutionTimeMillis()));
    }
}

