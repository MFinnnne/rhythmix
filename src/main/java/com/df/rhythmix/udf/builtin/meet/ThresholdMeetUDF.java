package com.df.rhythmix.udf.builtin.meet;

import com.df.rhythmix.udf.MeetUDF;

/**
 * Built-in threshold meet UDF that checks if a calculated value meets a predefined threshold.
 * This will be auto-discovered and registered at startup.
 *
 * Usage in expressions: thresholdMeet()
 *
 * This meet function checks if the calculated value is greater than or equal to 10.
 * It's useful for basic threshold validation in chain expressions.
 *
 * @author MFine
 * @version 1.0
 */
public class ThresholdMeetUDF implements MeetUDF {

    /**
     * The default threshold value
     */
    private static final double DEFAULT_THRESHOLD = 10.0;

    @Override
    public String getName() {
        return "thresholdMeet";
    }

    @Override
    public boolean meet(Number calculatedValue) {

        try {

            return calculatedValue.doubleValue() >= DEFAULT_THRESHOLD;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
