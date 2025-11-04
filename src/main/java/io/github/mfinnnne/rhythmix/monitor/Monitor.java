package io.github.mfinnnne.rhythmix.monitor;

import io.github.mfinnnne.rhythmix.translate.EnvProxy;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Encapsulates all monitoring functionality for RhythmixExecutor.
 * <p>
 * This class is responsible for:
 * - Recording event execution data
 * - Tracking state positions in the execution flow
 * - Measuring execution timing
 * - Managing execution records with FIFO strategy
 * - Generating monitoring reports
 *
 * @author MFine
 * @version 1.0
 * @since 1.1.0
 */
@Slf4j
public class Monitor {

    /**
     * The original expression string (before translation).
     */
    private final String expression;

    /**
     * Metadata about the state flow structure.
     */
    private final StateFlowMetadata stateFlowMetadata;

    /**
     * Reference to the executor's environment proxy for accessing state position.
     */
    private final EnvProxy envProxy;

    /**
     * List of execution records (FIFO queue with size limit).
     */
    private final LinkedList<ExecutionRecord> executionRecords = new LinkedList<>();

    /**
     * Maximum number of execution records to keep in memory.
     */
    private int maxRecords = 1000;

    /**
     * Total number of events processed (including those removed from records).
     */
    private int totalEventsProcessed = 0;

    /**
     * Count of successful matches.
     */
    private int successfulMatches = 0;

    /**
     * Count of failed matches.
     */
    private int failedMatches = 0;

    /**
     * Total execution time in nanoseconds.
     */
    private long totalExecutionTimeNanos = 0;

    /**
     * Key for monitoring state position in the environment.
     */
    private static final String MONITOR_STATE_POSITION_KEY = "__monitor_state_position__";

    /**
     * Constructs a Monitor instance.
     *
     * @param expression the original expression string
     * @param envProxy   the environment proxy for accessing state position
     */
    public Monitor(String expression, EnvProxy envProxy) {
        this.expression = expression;
        this.envProxy = envProxy;
        this.stateFlowMetadata = initializeStateFlowMetadata(expression);
        initializeMonitoringVariable();
    }

    /**
     * Initializes the state flow metadata by parsing the expression.
     */
    private StateFlowMetadata initializeStateFlowMetadata(String expression) {
        if (expression != null && !expression.isEmpty()) {
            return ExpressionParser.parse(expression);
        } else {
            // Create empty metadata if no expression provided
            return StateFlowMetadata.builder()
                    .expression("")
                    .stateUnits(new ArrayList<>())
                    .totalStates(0)
                    .statePositionMap(new HashMap<>())
                    .build();
        }
    }

    /**
     * Initializes the monitoring variable in the environment.
     */
    private void initializeMonitoringVariable() {
        envProxy.rawPut(MONITOR_STATE_POSITION_KEY, 0);
    }

    /**
     * Records an execution event.
     *
     * @param event                      the event data
     * @param result                     the execution result
     * @param startTime                  the start time in nanoseconds
     * @param endTime                    the end time in nanoseconds
     * @param duration                   the execution duration in nanoseconds
     * @param statePositionBeforeExec    the state position before/during execution
     * @param statePositionAfterExec     the state position after execution
     */
    public synchronized void recordExecution(Object event, boolean result, long startTime, long endTime, long duration,
                                            Integer statePositionBeforeExec, Integer statePositionAfterExec) {
        // Extract event data
        RhythmixEventData eventData = null;
        String eventId = null;

        if (event instanceof RhythmixEventData) {
            eventData = (RhythmixEventData) event;
            eventId = eventData.getId();
        } else {
            eventId = "unknown-" + System.currentTimeMillis();
        }

        // Get state unit expression at the before-execution position
        String stateUnit = stateFlowMetadata != null ? stateFlowMetadata.getStateUnitAtPosition(statePositionBeforeExec) : null;

        // Create execution record
        ExecutionRecord record = ExecutionRecord.builder()
                .eventId(eventId)
                .eventData(eventData)
                .executionResult(result)
                .startTimeNanos(startTime)
                .endTimeNanos(endTime)
                .executionDurationNanos(duration)
                .currentStatePosition(statePositionBeforeExec)
                .stateUnitAtPosition(stateUnit)
                .statePositionAfterExecution(statePositionAfterExec)
                .build();

        // Add record with FIFO strategy
        while (executionRecords.size() >= maxRecords) {
            executionRecords.removeFirst(); // Remove oldest record
        }
        executionRecords.addLast(record);

        // Update statistics
        totalEventsProcessed++;
        if (result) {
            successfulMatches++;
        } else {
            failedMatches++;
        }
        totalExecutionTimeNanos += duration;
    }

    /**
     * Gets the current state position from the executor's environment.
     *
     * @return the current state position, or 0 if not found
     */
    private Integer getCurrentStatePosition() {
        try {
            Object position = envProxy.rawGet(MONITOR_STATE_POSITION_KEY);
            if (position instanceof Integer) {
                return (Integer) position;
            } else if (position instanceof Long) {
                return ((Long) position).intValue();
            } else if (position instanceof Number) {
                return ((Number) position).intValue();
            }
        } catch (Exception e) {
            log.warn("Failed to get current state position from environment", e);
        }
        return 0;
    }

    /**
     * Gets all monitoring data.
     *
     * @return ExecutionMonitorData containing all collected data
     */
    public synchronized ExecutionMonitorData getMonitoringData() {
        double avgTime = totalEventsProcessed > 0
                ? (double) totalExecutionTimeNanos / totalEventsProcessed
                : 0.0;

        return ExecutionMonitorData.builder()
                .stateFlowMetadata(stateFlowMetadata)
                .executionRecords(new ArrayList<>(executionRecords))
                .totalEventsProcessed(totalEventsProcessed)
                .successfulMatches(successfulMatches)
                .failedMatches(failedMatches)
                .totalExecutionTimeNanos(totalExecutionTimeNanos)
                .averageExecutionTimeNanos(avgTime)
                .build();
    }

    /**
     * Resets all monitoring data.
     * This clears all execution records and resets statistics.
     */
    public synchronized void resetMonitoring() {
        executionRecords.clear();
        totalEventsProcessed = 0;
        successfulMatches = 0;
        failedMatches = 0;
        totalExecutionTimeNanos = 0;

        // Reset state position in environment
        envProxy.rawPut(MONITOR_STATE_POSITION_KEY, 0);
    }

    /**
     * Prints a human-readable monitoring report to the console.
     */
    public void printReport() {
        ExecutionMonitorData data = getMonitoringData();
        String report = ExecutionReportFormatter.generateReport(data);
        System.out.println(report);
    }

    /**
     * Sets the maximum number of records to keep in memory.
     *
     * @param maxRecords the maximum number of records
     */
    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }

    /**
     * Gets the current number of stored execution records.
     *
     * @return the number of execution records
     */
    public synchronized int getRecordCount() {
        return executionRecords.size();
    }

    /**
     * Gets the original expression string.
     *
     * @return the expression string
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Gets the state flow metadata.
     *
     * @return the state flow metadata
     */
    public StateFlowMetadata getStateFlowMetadata() {
        return stateFlowMetadata;
    }
}
