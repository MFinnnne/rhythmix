package com.df.rhythmix.udf;

import com.df.rhythmix.util.RhythmixEventData;

import java.util.List;

/**
 * <p>ChainCalculatorUDF interface.</p>
 *
 * author MFine
 * version $Id: $Id
 */
public interface ChainCalculatorUDF {
    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getName();

    /**
     * <p>calculate.</p>
     *
     * @param values a {@link java.util.List} object.
     * @return a {@link java.lang.Number} object.
     */
    Number calculate(List<RhythmixEventData> values);

}
