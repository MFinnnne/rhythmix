package com.df.rhythmix.udf;

import com.df.rhythmix.util.EventData;

import java.util.List;

public interface CalculatorUDF {
    String getName();

    Number calculate(List<EventData> values);

}
