package com.df.rhythmix.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;

@Slf4j
public class Util {
    public static EventData genEventData(String pointId, String value, Timestamp ts) {
        EventValueType dataType = EventValueType.STRING;
        if (value.equals("true") || value.equals("false")) {
            dataType = EventValueType.BOOL;
        }
        try {
            Integer.parseInt(value);
            dataType = EventValueType.INT;
        } catch (Exception ignore) {

        }
        try {
            Float.parseFloat(value);
            dataType = EventValueType.FLOAT;
        } catch (Exception ignore) {
        }

        return new EventData(pointId, "1", value, ts, dataType);
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
