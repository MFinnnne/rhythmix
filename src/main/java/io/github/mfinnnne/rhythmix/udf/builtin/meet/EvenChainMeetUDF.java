package io.github.mfinnnne.rhythmix.udf.builtin.meet;

import io.github.mfinnnne.rhythmix.udf.ChainMeetUDF;

/**
 * Built-in even meet UDF that checks if a calculated value is an even number.
 * This will be auto-discovered and registered at startup.
 *
 * Usage in expressions: evenMeet()
 *
 * This meet function checks if the calculated value is an even integer.
 * It's useful for validating that count or sum results are even numbers.
 *
 * author MFine
 * version 1.0
 */
public class EvenChainMeetUDF implements ChainMeetUDF {
    
    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "evenMeet";
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean meet(Number calculatedValue) {

        try {

            // Check if the value is a whole number
            if (calculatedValue.doubleValue() != Math.floor(calculatedValue.doubleValue())) {
                return false; // Not an integer
            }
            
            // Convert to long and check if even
            long longValue = calculatedValue.longValue();
            return longValue % 2 == 0;
            
        } catch (NumberFormatException e) {
            // Handle non-numeric values
            return false;
        }
    }
}
