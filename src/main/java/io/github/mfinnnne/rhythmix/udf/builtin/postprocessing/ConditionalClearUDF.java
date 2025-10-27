package io.github.mfinnnne.rhythmix.udf.builtin.postprocessing;

import io.github.mfinnnne.rhythmix.udf.ChainPostProcessingUDF;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;

import java.util.List;

/**
 * Built-in conditional clear UDF that returns true only if the calculation result exceeds a threshold.
 * This will be auto-discovered and registered at startup.
 *
 * Usage in expressions: conditionalClear()
 *
 * This post-processing function returns true only when the calculation result is greater than 100.0.
 * It's useful for conditional clearing based on calculated values in chain expressions.
 *
 * @author MFine
 * @version 1.0
 */
public class ConditionalClearUDF implements ChainPostProcessingUDF {

    /**
     * The threshold value for conditional clearing
     */
    private static final double THRESHOLD = 100.0;

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "conditionalClear";
    }

    /** {@inheritDoc} */
    @Override
    public boolean handle(List<RhythmixEventData> aggregatedData, Number calculationResult) {
        if (calculationResult == null) {
            return false;
        }

        try {
            double value = calculationResult.doubleValue();
            return value > THRESHOLD;
        } catch (Exception e) {
            return false;
        }
    }
}

