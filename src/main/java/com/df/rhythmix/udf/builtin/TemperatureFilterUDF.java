package com.df.rhythmix.udf.builtin;

import com.df.rhythmix.udf.FilterUDF;
import com.df.rhythmix.util.EventData;

/**
 * Built-in temperature filter UDF that keeps temperatures within a reasonable range.
 * This will be auto-discovered and registered at startup.
 * 
 * Usage in expressions: filter(tempFilter())
 * 
 * @author MFine
 * @version 1.0
 * @date 2025-07-18
 */
public class TemperatureFilterUDF implements FilterUDF {
    
    private static final double MIN_TEMP = -50.0;
    private static final double MAX_TEMP = 100.0;
    
    @Override
    public String getName() {
        return "tempFilter";
    }
    
    @Override
    public boolean filter(EventData event) {
        try {
            double temp = Double.parseDouble(event.getValue());
            return temp >= MIN_TEMP && temp <= MAX_TEMP;
        } catch (NumberFormatException e) {
            // Invalid temperature data should be discarded
            return false;
        }
    }
}
