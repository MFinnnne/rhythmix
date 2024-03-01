package com.celi.ferrum.util;

import com.celi.ferrum.exception.TranslatorException;

import java.util.Map;

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
                throw new TranslatorException("不支持此种时间格式类型");
        }
    }


}
