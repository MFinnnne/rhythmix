package io.github.mfinnnne.rhythmix.udf.builtin.postprocessing;

import io.github.mfinnnne.rhythmix.udf.ChainPostProcessingUDF;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;

import java.util.List;

/**
 * Built-in default clear UDF that always returns true.
 * This will be auto-discovered and registered at startup.
 *
 * Usage in expressions: clear()
 *
 * This post-processing function always returns true, indicating that clearing
 * should always proceed. It serves as the default behavior for chain expressions
 * and is useful for unconditional clearing of queues and resetting chain results.
 *
 * @author MFine
 * @version 1.0
 */
public class DefaultClearUDF implements ChainPostProcessingUDF {

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "clear";
    }

    /** {@inheritDoc} */
    @Override
    public boolean handle(List<RhythmixEventData> aggregatedData, Number calculationResult) {
        return true;
    }
}

