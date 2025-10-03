package io.github.mfinnnne.rhythmix.udf.builtin.filter;

import io.github.mfinnnne.rhythmix.udf.ChainFilterUDF;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;

/**
 * Built-in positive value filter UDF that keeps only positive numeric values.
 * This will be auto-discovered and registered at startup.
 *
 * Usage in expressions: filter(positiveFilter())
 *
 * author MFine
 * version 1.0
 * date 2025-07-18
 */
public class PositiveChainFilterUDF implements ChainFilterUDF {
    
    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "positiveFilter";
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean filter(RhythmixEventData event) {
        try {
            double value = Double.parseDouble(event.getValue());
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
