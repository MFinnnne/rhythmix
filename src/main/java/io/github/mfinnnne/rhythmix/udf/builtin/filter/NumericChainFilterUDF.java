package io.github.mfinnnne.rhythmix.udf.builtin.filter;

import io.github.mfinnnne.rhythmix.udf.ChainFilterUDF;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;

/**
 * Built-in numeric filter UDF that keeps only numeric values.
 * This will be auto-discovered and registered at startup.
 *
 * Usage in expressions: filter(numericFilter())
 *
 * author MFine
 * version 1.0
 * date 2025-07-18
 */
public class NumericChainFilterUDF implements ChainFilterUDF {
    
    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "numericFilter";
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean filter(RhythmixEventData event) {
        try {
            Double.parseDouble(event.getValue());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
