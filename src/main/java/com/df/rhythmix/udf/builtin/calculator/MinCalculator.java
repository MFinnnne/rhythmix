package com.df.rhythmix.udf.builtin.calculator;

import com.df.rhythmix.udf.CalculatorUDF;
import com.df.rhythmix.util.EventData;

import java.util.List;

public class MinCalculator implements CalculatorUDF {
    @Override
    public String getName() {
        return "mincalc";
    }

    @Override
    public Number calculate(List<EventData> values) {
        // Handle null or empty input
        if (values == null || values.isEmpty()) {
            return 0;
        }

        double min = Double.POSITIVE_INFINITY;
        boolean hasValidNumber = false;

        // Iterate through all EventData objects
        for (EventData eventData : values) {
            if (eventData == null) {
                continue;
            }

            // Get the numeric value from EventData
            Object value = eventData.getValue();
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
