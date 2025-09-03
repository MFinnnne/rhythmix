package com.df.rhythmix.udf.builtin.calculator;

import com.df.rhythmix.udf.CalculatorUDF;
import com.df.rhythmix.util.RhythmixEventData;

import java.util.List;

public class MaxCalculator implements CalculatorUDF {
    @Override
    public String getName() {
        return "maxcalc";
    }

    @Override
    public Number calculate(List<RhythmixEventData> values) {
        if (values == null || values.isEmpty()) {
            return 0;
        }

        double max = Double.NEGATIVE_INFINITY;
        boolean hasValidNumber = false;

        for (RhythmixEventData rhythmixEventData : values) {
            if (rhythmixEventData == null) {
                continue;
            }
            Object value = rhythmixEventData.getValue();
            if (value == null) {
                continue;
            }

            try {
                double num;
                num = Double.parseDouble(value.toString());
                if (Double.isNaN(num)) {
                    continue;
                }
                if (num > max) {
                    max = num;
                }
                hasValidNumber = true;

            } catch (NumberFormatException ignored) {
            }
        }

        if (!hasValidNumber) {
            return 0;
        }
        if (max == Math.floor(max) && !Double.isInfinite(max)) {
            return (long) max;
        } else {
            return max;
        }
    }
}
