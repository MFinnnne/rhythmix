package io.github.mfinnnne.rhythmix.udf.builtin.meet;

import io.github.mfinnnne.rhythmix.udf.ChainMeetUDF;

/**
 * Built-in positive meet UDF that checks if a calculated value is positive.
 * This will be auto-discovered and registered at startup.
 *
 * Usage in expressions: positiveMeet()
 *
 * This meet function checks if the calculated value is greater than 0.
 * It's useful for validating that calculation results are positive values.
 *
 * author MFine
 * version 1.0
 */
public class PositiveChainMeetUDF implements ChainMeetUDF {
    
    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "positiveMeet";
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean meet(Number calculatedValue) {
        try {
            // Convert to numeric value
            double numericValue = toNumericValue(calculatedValue);
            
            // Check if value is positive (greater than 0)
            return numericValue > 0.0;
            
        } catch (NumberFormatException e) {
            // Handle non-numeric values
            return false;
        }
    }
}
