package io.github.mfinnnne.rhythmix.util;

import io.github.mfinnnne.rhythmix.exception.TranslatorException;

/**
 * <p>TranslateUtil class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
public class TranslateUtil {

    /**
     * <p>toMs.</p>
     *
     * @param time a long.
     * @param unit a {@link java.lang.String} object.
     * @return a long.
     * @throws TranslatorException if any.
     */
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
