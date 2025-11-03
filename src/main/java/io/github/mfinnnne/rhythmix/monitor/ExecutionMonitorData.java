package io.github.mfinnnne.rhythmix.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregates all monitoring information.
 * <p>
 * This class contains the complete monitoring data including state flow metadata,
 * execution records, and calculated statistics.
 *
 * @author MFine
 * @version 1.0
 * @since 1.1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionMonitorData {

    /**
     * Metadata about the state flow structure.
     */
    private StateFlowMetadata stateFlowMetadata;

    /**
     * List of all recorded execution records.
     */
    @Builder.Default
    private List<ExecutionRecord> executionRecords = new ArrayList<>();

    /**
     * Total number of events processed.
     */
    private Integer totalEventsProcessed;

    /**
     * Count of successful matches (execution result = true).
     */
    private Integer successfulMatches;

    /**
     * Count of failed matches (execution result = false).
     */
    private Integer failedMatches;

    /**
     * Total execution time in nanoseconds (sum of all execution durations).
     */
    private Long totalExecutionTimeNanos;

    /**
     * Average execution time in nanoseconds.
     */
    private Double averageExecutionTimeNanos;

    /**
     * Gets the average execution time in milliseconds.
     *
     * @return average execution time in milliseconds
     */
    public Double getAverageExecutionTimeMillis() {
        if (averageExecutionTimeNanos == null) {
            return null;
        }
        return averageExecutionTimeNanos / 1_000_000.0;
    }

    /**
     * Gets the total execution time in milliseconds.
     *
     * @return total execution time in milliseconds
     */
    public Double getTotalExecutionTimeMillis() {
        if (totalExecutionTimeNanos == null) {
            return null;
        }
        return totalExecutionTimeNanos / 1_000_000.0;
    }

    /**
     * Gets the success rate as a percentage.
     *
     * @return success rate (0-100)
     */
    public Double getSuccessRate() {
        if (totalEventsProcessed == null || totalEventsProcessed == 0) {
            return 0.0;
        }
        return (successfulMatches * 100.0) / totalEventsProcessed;
    }

    /**
     * Gets execution records filtered by result.
     *
     * @param result the result to filter by (true for successful, false for failed)
     * @return list of execution records with the specified result
     */
    public List<ExecutionRecord> getRecordsByResult(boolean result) {
        return executionRecords.stream()
                .filter(record -> record.getExecutionResult() != null && record.getExecutionResult() == result)
                .collect(Collectors.toList());
    }

    /**
     * Gets execution records filtered by state position.
     *
     * @param position the state position to filter by
     * @return list of execution records at the specified position
     */
    public List<ExecutionRecord> getRecordsByStatePosition(int position) {
        return executionRecords.stream()
                .filter(record -> record.getCurrentStatePosition() != null && record.getCurrentStatePosition() == position)
                .collect(Collectors.toList());
    }

    /**
     * Gets the minimum execution time in nanoseconds.
     *
     * @return minimum execution time, or null if no records
     */
    public Long getMinExecutionTimeNanos() {
        return executionRecords.stream()
                .map(ExecutionRecord::getExecutionDurationNanos)
                .filter(duration -> duration != null)
                .min(Long::compareTo)
                .orElse(null);
    }

    /**
     * Gets the maximum execution time in nanoseconds.
     *
     * @return maximum execution time, or null if no records
     */
    public Long getMaxExecutionTimeNanos() {
        return executionRecords.stream()
                .map(ExecutionRecord::getExecutionDurationNanos)
                .filter(duration -> duration != null)
                .max(Long::compareTo)
                .orElse(null);
    }
}

