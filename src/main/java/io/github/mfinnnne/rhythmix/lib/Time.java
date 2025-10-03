package io.github.mfinnnne.rhythmix.lib;

import com.googlecode.aviator.annotation.Import;
import com.googlecode.aviator.annotation.ImportScope;

import java.sql.Timestamp;

/**
 * Provides time-related utility functions for use within Aviator expressions.
 * <p>
 * This class is imported as a static namespace 'ts', making its methods available
 * for timestamp manipulation in Rhythmix expressions (e.g., {@code ts.addMs(event.ts, 1000)}).
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
@Import(ns = "ts", scopes = {ImportScope.Static})
public class Time {

    /**
     * Converts milliseconds since the epoch to a {@link Timestamp}.
     *
     * @param time the time in milliseconds
     * @return a {@link java.sql.Timestamp} representing the given time
     */
    public static Timestamp ms2Ts(long time) {
        return new Timestamp(time);
    }

    /**
     * Converts a {@link Timestamp} to milliseconds since the epoch.
     *
     * @param time a {@link java.sql.Timestamp}
     * @return milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public static long ts2Ms(Timestamp time) {
        return time.getTime();
    }

    /**
     * Adds a specified number of milliseconds to a timestamp.
     *
     * @param ori  the original {@link java.sql.Timestamp}
     * @param time the number of milliseconds to add
     * @return a new {@link java.sql.Timestamp} with the added time
     */
    public static Timestamp addMs(Timestamp ori, long time) {
        long newTime = ori.getTime() + time;
        return new Timestamp(newTime);
    }

    /**
     * Subtracts a specified number of milliseconds from a timestamp.
     *
     * @param ori  the original {@link java.sql.Timestamp}
     * @param time the number of milliseconds to subtract
     * @return a new {@link java.sql.Timestamp} with the subtracted time
     */
    public static Timestamp subMs(Timestamp ori, long time) {
        return addMs(ori, -time);
    }

}
