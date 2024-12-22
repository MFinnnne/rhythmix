package com.df.rhythmix.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;

import com.df.rhythmix.util.SensorEvent;

@Slf4j
public class Util {
    public static SensorEvent genPointData(String pointId, String value, Timestamp ts) {
        SensorEventValueType dataType = SensorEventValueType.STRING;
        if (value.equals("true") || value.equals("false")) {
            dataType = SensorEventValueType.BOOL;
        }
        try {
            Integer.parseInt(value);
            dataType = SensorEventValueType.INT;
        } catch (Exception ignore) {

        }
        try {
            Float.parseFloat(value);
            dataType = SensorEventValueType.FLOAT;
        } catch (Exception ignore) {
        }

        return new SensorEvent(pointId, "1", value, ts, dataType);
    }

    public static Timestamp addSeconds(Timestamp oriTs, long sec) {
        long now = oriTs.getTime() + sec * 1000;
        return new Timestamp(now);
    }

    public static Timestamp addMs(Timestamp oriTs, long sec) {
        long now = oriTs.getTime() + sec;
        return new Timestamp(now);
    }

    public static Timestamp reduceSeconds(Timestamp oriTs, long sec) {
        long now = oriTs.getTime() - sec * 1000;
        return new Timestamp(now);
    }

}
