package io.github.mfinnnne.rhythmix.monitor;

import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Records a single event's execution data.
 * <p>
 * This class captures all relevant information about a single event execution,
 * including the event data, execution result, timing information, and state position.
 *
 * @author MFine
 * @version 1.0
 * @since 1.1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionRecord {

    /**
     * Unique identifier from RhythmixEventData.
     */
    private String eventId;

    /**
     * The original event data that was processed.
     */
    private RhythmixEventData eventData;

    /**
     * The result of the execution (true if conditions met, false otherwise).
     */
    private Boolean executionResult;

    /**
     * Timestamp when execution started (in nanoseconds).
     */
    private Long startTimeNanos;

    /**
     * Timestamp when execution ended (in nanoseconds).
     */
    private Long endTimeNanos;

    /**
     * Duration of execution in nanoseconds.
     */
    private Long executionDurationNanos;

    /**
     * The current state unit position in the execution flow (0-based index) at the time of execution.
     * This represents the position BEFORE or DURING the event processing.
     * For example, in "{a}->{b}->{c}", position 0 is "{a}", position 1 is "{b}", position 2 is "{c}".
     */
    private Integer currentStatePosition;

    /**
     * The actual state unit expression at the current position.
     * For example, "{a}", "{count(>4,3)}", etc.
     */
    private String stateUnitAtPosition;

    /**
     * The state unit position AFTER the current event has been executed (0-based index).
     * This represents where the executor moved to after processing this event.
     * This allows tracking state transitions: if currentStatePosition=0 and statePositionAfterExecution=1,
     * it means the event caused a transition from state 0 to state 1.
     * If both positions are the same, no state transition occurred.
     */
    private Integer statePositionAfterExecution;

    /**
     * Calculates and returns the execution duration in milliseconds.
     *
     * @return execution duration in milliseconds
     */
    public Double getExecutionDurationMillis() {
        if (executionDurationNanos == null) {
            return null;
        }
        return executionDurationNanos / 1_000_000.0;
    }

    /**
     * Returns a human-readable string representation of this execution record.
     *
     * @return formatted string with execution details
     */
    @Override
    public String toString() {
        return String.format(
            "ExecutionRecord{eventId='%s', result=%s, duration=%.3fms, statePosition=%d->%d, stateUnit='%s'}",
            eventId,
            executionResult,
            getExecutionDurationMillis(),
            currentStatePosition,
            statePositionAfterExecution,
            stateUnitAtPosition
        );
    }
}

