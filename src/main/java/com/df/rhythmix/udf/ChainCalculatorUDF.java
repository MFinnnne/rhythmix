package com.df.rhythmix.udf;

import com.df.rhythmix.util.RhythmixEventData;

import java.util.List;

public interface ChainCalculatorUDF {
    String getName();

    Number calculate(List<RhythmixEventData> values);

}
