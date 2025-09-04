package com.df.rhythmix.udf.builtin.filter;

import com.df.rhythmix.udf.ChainFilterUDF;
import com.df.rhythmix.util.RhythmixEventData;

/**
 * Built-in positive value filter UDF that keeps only positive numeric values.
 * This will be auto-discovered and registered at startup.
 * 
 * Usage in expressions: filter(positiveFilter())
 * 
 * @author MFine
 * @version 1.0
 * @date 2025-07-18
 */
public class PositiveChainFilterUDF implements ChainFilterUDF {
    
    @Override
    public String getName() {
        return "positiveFilter";
    }
    
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
