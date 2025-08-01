package com.df.rhythmix.udf.builtin;

import com.df.rhythmix.udf.FilterUDF;
import com.df.rhythmix.util.EventData;

/**
 * Built-in numeric filter UDF that keeps only numeric values.
 * This will be auto-discovered and registered at startup.
 * 
 * Usage in expressions: filter(numericFilter())
 * 
 * @author MFine
 * @version 1.0
 * @date 2025-07-18
 */
public class NumericFilterUDF implements FilterUDF {
    
    @Override
    public String getName() {
        return "numericFilter";
    }
    
    @Override
    public boolean filter(EventData event) {
        try {
            Double.parseDouble(event.getValue());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
