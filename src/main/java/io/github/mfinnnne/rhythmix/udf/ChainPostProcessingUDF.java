package io.github.mfinnnne.rhythmix.udf;

import io.github.mfinnnne.rhythmix.util.RhythmixEventData;

import java.util.List;

/**
 * Interface for Post-Processing User-Defined Functions (PostProcessingUDF).
 * <p>
 * PostProcessingUDF allows users to define custom post-processing logic that can be used
 * in chain expressions after calculation and meet conditions have been evaluated.
 * This enables complex clearing/resetting logic based on both aggregated event data
 * and calculation results.
 * <p>
 * Post-processing functions are typically used at the end of chain expressions to determine
 * whether to clear queues and reset chain results based on custom business logic.
 * <p>
 * Example usage in expressions:
 * <pre>{@code
 * filter(>0).sum().meet(>50).customClear()
 * filter(>0).avg().meet(>0).conditionalClear()
 * filter(>0).count().meet(>10).thresholdClear()
 * }
 * </pre>
 * <p>
 * Implementation example:
 * <pre>{@code
 * public class ThresholdClearUDF implements ChainPostProcessingUDF {
 *     {@literal @}Override
 *     public String getName() {
 *         return "thresholdClear";
 *     }
 *
 *     {@literal @}Override
 *     public boolean handle(List<RhythmixEventData> aggregatedData, Number calculationResult) {
 *         // Only clear if result exceeds threshold
 *         if (!isValidResult(calculationResult)) {
 *             return false;
 *         }
 *         double value = calculationResult.doubleValue();
 *         return value > 100.0;
 *     }
 * }
 * }
 * </pre>
 *
 * @author MFine
 * @version 1.0
 */
public interface ChainPostProcessingUDF {

    /**
     * Get the name of this post-processing function.
     * This name will be used to identify and call the function in expressions.
     * <p>
     * The name should be unique across all registered PostProcessingUDF implementations.
     * It's recommended to use descriptive names that indicate the post-processing behavior,
     * such as "clear", "conditionalClear", "thresholdClear", etc.
     *
     * @return the unique name of this post-processing function
     */
    String getName();

    /**
     * Handle post-processing of chain expression results.
     * <p>
     * This method receives both the aggregated data from the chain expression
     * and the calculation result, enabling complex post-processing logic that can
     * consider both event characteristics and computed values.
     * <p>
     * The implementation should:
     * <ul>
     * <li>Validate inputs using isValidData() and isValidResult() helper methods</li>
     * <li>Implement custom business logic based on both parameters</li>
     * <li>Return true to indicate successful post-processing (typically triggers clearing)</li>
     * <li>Return false to skip post-processing actions</li>
     * </ul>
     * <p>
     * Common use cases:
     * <ul>
     * <li>Clear only if result exceeds a threshold</li>
     * <li>Clear only if event count meets criteria</li>
     * <li>Clear based on combined conditions (result AND data characteristics)</li>
     * <li>Conditional clearing based on event patterns</li>
     * </ul>
     *
     * @param aggregatedData    List of RhythmixEventData from the chain expression
     * @param calculationResult The Number result from the calculation function
     * @return true if post-processing succeeded (typically triggers clearing), false otherwise
     */
    boolean handle(List<RhythmixEventData> aggregatedData, Number calculationResult);



}

