package io.github.mfinnnne.rhythmix.monitor;

import io.github.mfinnnne.rhythmix.execute.RhythmixExpressionEntity;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;

/**
 * Comprehensive monitoring interface for third-party integrations with Rhythmix.
 * <p>
 * This interface provides callback methods for monitoring the complete lifecycle of
 * Rhythmix expressions and their execution. Implementations can use these callbacks
 * to integrate with external monitoring systems, logging frameworks, metrics collectors,
 * or custom analytics platforms.
 * <p>
 * All methods have default implementations that do nothing, allowing implementers to
 * override only the callbacks they need. This follows the principle of least surprise
 * and makes the interface easy to adopt incrementally.
 * <p>
 * <b>Thread Safety:</b> Implementations should be thread-safe as callbacks may be
 * invoked from multiple threads concurrently, especially during batch execution.
 * <p>
 * <b>Exception Handling:</b> Implementations should not throw exceptions from callback
 * methods. Any exceptions thrown will be caught and logged, but will not interrupt
 * the normal execution flow of Rhythmix.
 * <p>
 * <b>Performance Considerations:</b> Callback methods should execute quickly to avoid
 * impacting expression execution performance. For expensive operations (e.g., network
 * calls, database writes), consider using asynchronous processing or buffering.
 *
 * @author MFine
 * @version 1.0
 * @since 1.2.0
 */
public interface RhythmixMonitor {

    // ==================== Expression Lifecycle Callbacks ====================

    /**
     * Called when a new expression is created.
     * <p>
     * This callback is invoked after an expression has been successfully parsed and
     * compiled, but before it is used for execution. It provides an opportunity to
     * register the expression with external systems, initialize resources, or perform
     * validation.
     *
     * @param entity the expression entity containing metadata about the created expression
     */
    default void onExpressionCreated(RhythmixExpressionEntity entity) {
        // Default: no-op
    }

    /**
     * Called when an existing expression is updated.
     * <p>
     * This callback is invoked when an expression's definition or metadata is modified.
     * The entity parameter contains the updated state. Implementations can use this to
     * track expression changes, invalidate caches, or notify dependent systems.
     *
     * @param entity the expression entity containing the updated expression metadata
     */
    default void onExpressionUpdated(RhythmixExpressionEntity entity) {
        // Default: no-op
    }

    /**
     * Called when an expression is deleted.
     * <p>
     * This callback is invoked before an expression is removed from the system.
     * Implementations can use this to clean up resources, remove registrations,
     * or archive historical data associated with the expression.
     *
     * @param entity the expression entity being deleted
     */
    default void onExpressionDeleted(RhythmixExpressionEntity entity) {
        // Default: no-op
    }


    // ==================== Execution Lifecycle Callbacks ====================

    /**
     * Called immediately before an expression is executed against an event.
     * <p>
     * This callback is invoked before the expression evaluation begins. The execution
     * data contains the expression and current state, but execution results are not
     * yet available. This is useful for pre-execution logging, resource allocation,
     * or starting timers.
     *
     * @param expression    the expression entity that will be executed
     * @param eventData     the event data that will be executed against the expression
     */
    default void onBeforeExecution(RhythmixExpressionEntity expression, RhythmixEventData eventData) {
        // Default: no-op
    }

    /**
     * Called immediately after an expression execution completes.
     * <p>
     * This callback is invoked after the expression has been evaluated against an event,
     * regardless of the result. The execution data contains the complete execution record
     * including timing information, result, and state transitions. This is the primary
     * callback for collecting execution metrics and results.
     *
     * @param expression    the expression entity that was executed
     * @param eventData     the event data that was executed against the expression
     */
    default void onAfterExecution(RhythmixExpressionEntity expression, RhythmixEventData eventData) {
        // Default: no-op
    }

    /**
     * Called when an expression execution returns true (successful match).
     * <p>
     * This callback is invoked immediately after a successful expression evaluation
     * that returns true, indicating the event matched the expression criteria. This
     * is a critical business event that typically triggers downstream actions such as
     * alerts, notifications, or workflow execution. Unlike onAfterExecution which is
     * called for all executions, this callback is only invoked for successful matches.
     * <p>
     * This callback is invoked before the environment is reset, allowing access to
     * the complete execution state at the moment of success.
     *
     * @param expression    the expression entity that was executed
     * @param eventData the execution context of the successful execution
     */
    default void onExecutionSuccess(RhythmixExpressionEntity expression, RhythmixEventData eventData) {
        // Default: no-op
    }

    /**
     * Called when the state position changes during expression execution.
     * <p>
     * This callback is invoked when the executor transitions from one state unit to
     * another in a multi-state expression (e.g., "{a}-&gt;{b}-&gt;{c}"). It provides
     * visibility into the state machine progression and can be used for debugging
     * complex state flows or tracking partial matches.
     *
     * @param expression       the expression entity that was executed
     * @param previousPosition  the state position before the transition (0-based index)
     * @param currentPosition   the state position after the transition (0-based index)
     * @param executionData     the execution context at the time of transition
     */
    default void onStatePositionChanged(RhythmixExpressionEntity expression,
                                        int previousPosition,
                                        int currentPosition,
                                        RhythmixExecutionData executionData) {
        // Default: no-op
    }

    // ==================== Error Handling Callbacks ====================

    /**
     * Called when an expression compilation error occurs.
     * <p>
     * This callback is invoked when an expression fails to compile due to syntax
     * errors, semantic errors, or other compilation issues. The exception contains
     * detailed position information and error messages. This is critical for
     * tracking expression quality and debugging user-provided expressions.
     *
     * @param entity    the expression entity that failed to compile
     * @param exception the compilation exception with position and error details
     */
    default void onCompilationError(RhythmixExpressionEntity entity, String exception) {
        // Default: no-op
    }

    /**
     * Called when a runtime error occurs during expression execution.
     * <p>
     * This callback is invoked when an unexpected error occurs during expression
     * evaluation, such as type errors, null pointer exceptions, or UDF failures.
     * Unlike compilation errors, these occur at runtime and may be transient or
     * data-dependent.
     *
     * @param entity        the expression entity that failed during execution
     * @param executionData the execution context when the error occurred
     * @param error         the error that was thrown during execution
     */
    default void onExecutionError(RhythmixExpressionEntity entity,RhythmixExecutionData executionData, Throwable error) {
        // Default: no-op
    }



}
