package io.github.mfinnnne.rhythmix.execute;

import io.github.mfinnnne.rhythmix.config.RhythmixConfig;
import io.github.mfinnnne.rhythmix.lib.AviatorConfig;
import io.github.mfinnnne.rhythmix.monitor.*;
import io.github.mfinnnne.rhythmix.translate.EnvProxy;
import io.github.mfinnnne.rhythmix.util.AviatorFunctionUtil;
import com.googlecode.aviator.Expression;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Executes compiled Rhythmix code with built-in monitoring capabilities.
 * <p>
 * This class takes the translated code and the environment from the {@link RhythmixCompiler}
 * and executes it against event data. It manages the execution state and environment,
 * including resetting the environment after a successful execution that returns {@code true}.
 * <p>
 * Monitoring is automatically enabled and tracks:
 * - Event data and processing results
 * - State unit positions in the execution flow
 * - Runtime measurements for each data item
 * <p>
 * Thread-safety: methods {@link #execute(Object)} and {@link #execute(Object...)} are synchronized
 * to avoid concurrent access to the shared {@link EnvProxy}.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
@AllArgsConstructor
public class RhythmixExecutor {

    @Getter
    private String code;


    @Getter
    @Setter
    private EnvProxy envProxy;

    @Setter
    private HashMap<String, Object> originalEnv = new HashMap<>();

    /**
     * Monitor instance for tracking execution data and performance.
     */
    @Getter
    private RhythmixExecutionData rhythmixExecutionData;

    /**
     * Default constructor.
     */
    public RhythmixExecutor() {
    }

    /**
     * Constructs a RhythmixExecutor with the translated code and execution environment.
     *
     * @param code the translated code string
     * @param env  the execution environment proxy
     */
    public RhythmixExecutor(String code, EnvProxy env) {
        this(code, env, null);
    }

    /**
     * Constructs a RhythmixExecutor with the translated code, execution environment, and original expression.
     *
     * @param code       the translated code string
     * @param env        the execution environment proxy
     * @param expression the original expression string (before translation)
     */
    public RhythmixExecutor(String code, EnvProxy env, String expression) {
        this.code = code;
        this.envProxy = env;

        this.originalEnv.putAll(this.envProxy.getEnv());
        AviatorConfig.operatorOverloading();

        // Initialize monitoring
        this.rhythmixExecutionData = new RhythmixExecutionData(expression, env);
    }

    /**
     * Gets a fresh copy of the original environment.
     * This is used to reset the environment state. Any {@link LinkedList} instances
     * are replaced with new, empty instances to clear their state.
     *
     * @return a clean initial environment map
     */
    public HashMap<String, Object> getOriginalEnv() {
        this.originalEnv.forEach((k, v) -> {
            if (v instanceof LinkedList) {
                this.originalEnv.put(k, new LinkedList<>());
            }
        });
        return originalEnv;
    }

    /**
     * Executes the compiled code against a single event with monitoring.
     * The environment is reset if the execution returns {@code true}.
     *
     * @param event the event data to process
     * @return {@code true} if the expression's conditions are met; {@code false} otherwise
     */
    public synchronized boolean execute(Object event) {
        long startTime = System.nanoTime();

        // Capture state position before execution
        Integer statePositionBefore = getStatePositionFromEnv();

        this.envProxy.rawPut("event", event);
        Expression expr = AviatorFunctionUtil.getExpr(code);
        Boolean res =(Boolean) expr.execute(envProxy.getEnv());
        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        // Capture state position after execution
        Integer statePositionAfter = getStatePositionFromEnv();

        // Record execution for monitoring (delegate to monitor)
        if (rhythmixExecutionData != null) {
            rhythmixExecutionData.recordExecution(event, res, startTime, endTime, duration, statePositionBefore, statePositionAfter);
        }

        if (res) {
            resetEnv();
        }
        return res;
    }

    /**
     * Gets the current state position from the environment.
     *
     * @return the current state position, or 0 if not found
     */
    private Integer getStatePositionFromEnv() {
        try {
            Object position = envProxy.rawGet("__monitor_state_position__");
            if (position instanceof Integer) {
                return (Integer) position;
            } else if (position instanceof Long) {
                return ((Long) position).intValue();
            } else if (position instanceof Number) {
                return ((Number) position).intValue();
            }
        } catch (Exception e) {
            // Return 0 if unable to get position
        }
        return 0;
    }

    /**
     * Resets the execution environment to its original state.
     * This clears any accumulated state from previous executions.
     */
    public void resetEnv() {
        this.envProxy.getEnv().clear();
        this.envProxy.getEnv().putAll(this.getOriginalEnv());
    }

    /**
     * Executes the compiled code against a sequence of events with monitoring.
     * The environment is reset if the final execution returns {@code true}.
     *
     * @param events a variable number of event data objects to process in sequence
     * @return {@code true} if the expression's conditions are met after processing all events; {@code false} otherwise
     */
    public synchronized boolean execute(Object... events) {
        boolean res = false;
        for (Object event : events) {
            res = execute(event);
        }
        return res;
    }

    // ==================== Monitoring Delegation Methods ====================

    /**
     * Gets all monitoring data.
     * Delegates to the Monitor instance.
     *
     * @return ExecutionMonitorData containing all collected data
     * @since 1.1.0
     */
    public ExecutionMonitorData getMonitoringData() {
        if (rhythmixExecutionData != null) {
            return rhythmixExecutionData.getMonitoringData();
        }
        // Return empty data if monitor is not initialized
        return ExecutionMonitorData.builder()
                .stateFlowMetadata(StateFlowMetadata.builder().build())
                .executionRecords(new ArrayList<>())
                .totalEventsProcessed(0)
                .successfulMatches(0)
                .failedMatches(0)
                .totalExecutionTimeNanos(0L)
                .averageExecutionTimeNanos(0.0)
                .build();
    }

    /**
     * Resets all monitoring data.
     * Delegates to the Monitor instance.
     *
     * @since 1.1.0
     */
    public void resetMonitoring() {
        if (rhythmixExecutionData != null) {
            rhythmixExecutionData.resetMonitoring();
        }
    }

    /**
     * Prints a human-readable monitoring report to the console.
     * Delegates to the Monitor instance.
     *
     * @since 1.1.0
     */
    public void printReport() {
        if (rhythmixExecutionData != null) {
            rhythmixExecutionData.printReport();
        }
    }

    /**
     * Sets the maximum number of records to keep in memory.
     * Delegates to the Monitor instance.
     *
     * @param maxRecords the maximum number of records
     * @since 1.1.0
     */
    public void setMaxRecords(int maxRecords) {
        if (rhythmixExecutionData != null) {
            rhythmixExecutionData.setMaxRecords(maxRecords);
        }
    }

    /**
     * Gets the current number of stored execution records.
     * Delegates to the Monitor instance.
     *
     * @return the number of execution records
     * @since 1.1.0
     */
    public int getRecordCount() {
        if (rhythmixExecutionData != null) {
            return rhythmixExecutionData.getRecordCount();
        }
        return 0;
    }

    /**
     * Gets the original expression string.
     * Delegates to the Monitor instance.
     *
     * @return the expression string
     * @since 1.1.0
     */
    public String getExpression() {
        if (rhythmixExecutionData != null) {
            return rhythmixExecutionData.getExpression();
        }
        return null;
    }

    /**
     * Gets the state flow metadata.
     * Delegates to the Monitor instance.
     *
     * @return the state flow metadata
     * @since 1.1.0
     */
    public StateFlowMetadata getStateFlowMetadata() {
        if (rhythmixExecutionData != null) {
            return rhythmixExecutionData.getStateFlowMetadata();
        }
        return null;
    }
}
