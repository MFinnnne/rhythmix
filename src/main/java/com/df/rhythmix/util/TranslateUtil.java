package com.df.rhythmix.util;

import com.df.rhythmix.exception.TranslatorException;

public class TranslateUtil {

    public static long toMs(long time, String unit) throws TranslatorException {
        switch (unit) {
            case "ms":
                return time;
            case "s":
                return time * 1000;
            case "m":
                return time * 1000 * 60;
            case "h":
                return time * 1000 * 3600;
            case "d":
                return time * 1000 * 3600 * 24;
            default:
                throw new TranslatorException("This time format type is not supported");
        }
    }


}
