package com.df.rhythmix.udf.builtin.calculator;

import com.df.rhythmix.udf.ChainCalculatorUDF;
import com.df.rhythmix.util.RhythmixEventData;

import java.util.List;

public class MinChainCalculator implements ChainCalculatorUDF {
    @Override
    public String getName() {
        return "mincalc";
    }

    @Override
    public Number calculate(List<RhythmixEventData> values) {
        // Handle null or empty input
        if (values == null || values.isEmpty()) {
            return 0;
        }

        double min = Double.POSITIVE_INFINITY;
        boolean hasValidNumber = false;

        // Iterate through all EventData objects
        for (RhythmixEventData rhythmixEventData : values) {
            if (rhythmixEventData == null) {
                continue;
            }

            // Get the numeric value from EventData
            Object value = rhythmixEventData.getValue();
            if (value == null) {
                continue;
            }

            try {
                double num;
                if (value instanceof Number) {
                    num = ((Number) value).doubleValue();
                } else {
                    // Try to parse string representation as number
                    num = Double.parseDouble(value.toString());
                }

                // Skip NaN values
                if (Double.isNaN(num)) {
                    continue;
                }

                // Update minimum value
                if (num < min) {
                    min = num;
                }
                hasValidNumber = true;

            } catch (NumberFormatException e) {
                // Skip non-numeric values
                continue;
            }
        }

        // Return 0 if no valid numbers were found
        if (!hasValidNumber) {
            return 0;
        }

        // Return as integer if it's a whole number, otherwise as double
        if (min == Math.floor(min) && !Double.isInfinite(min)) {
            return (long) min;
        } else {
            return min;
        }
    }
}
