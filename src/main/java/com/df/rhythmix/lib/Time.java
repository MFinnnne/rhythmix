package com.df.rhythmix.lib;

import com.googlecode.aviator.annotation.Import;
import com.googlecode.aviator.annotation.ImportScope;

import java.sql.Timestamp;

@Import(ns = "ts", scopes = {ImportScope.Static})
public class Time {

    public static Timestamp ms2Ts(long time) {
        return new Timestamp(time);
    }

    public static long ts2Ms(Timestamp time) {
        return time.getTime();
    }

    public static Timestamp addMs(Timestamp ori, long time) {
        long newTime = ori.getTime() + time;
        return new Timestamp(newTime);
    }

    public static Timestamp subMs(Timestamp ori, long time) {
        return addMs(ori, -time);
    }

}
