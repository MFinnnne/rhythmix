package com.df.rhythmix.udf.builtin.meet;

import com.df.rhythmix.udf.ChainMeetUDF;

/**
 * Built-in range meet UDF that checks if a calculated value falls within a predefined range.
 * This will be auto-discovered and registered at startup.
 * 
 * Usage in expressions: rangeMeet()
 * 
 * This meet function checks if the calculated value is between 5 and 50 (inclusive).
 * It's useful for validating that results fall within acceptable bounds.
 * 
 * @author MFine
 * @version 1.0
 */
public class RangeChainMeetUDF implements ChainMeetUDF {
    
    /**
     * The default minimum value (inclusive)
     */
    private static final double DEFAULT_MIN = 5.0;
    
    /**
     * The default maximum value (inclusive)
     */
    private static final double DEFAULT_MAX = 50.0;
    
    @Override
    public String getName() {
        return "rangeMeet";
    }
    
    @Override
    public boolean meet(Number calculatedValue) {

        
        try {
            // Convert to numeric value
            double numericValue = toNumericValue(calculatedValue);
            
            // Check if value is within the range
            return numericValue >= DEFAULT_MIN && numericValue <= DEFAULT_MAX;
            
        } catch (NumberFormatException e) {
            // Handle non-numeric values
            return false;
        }
    }
    

}
