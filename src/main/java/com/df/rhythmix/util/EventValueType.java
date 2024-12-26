package com.df.rhythmix.util;

/**
 * @author MFine
 * @version 1.0
 * @date 2024/12/22 18:29
 **/
public enum EventValueType {
    STRING("string", "STRING"),
    BOOL("bool", "BOOL"),
    INT("int", "INT"),
    FLOAT("float", "FLOAT");


    private final String title;
    private final String code;

    EventValueType(String title, String code) {
        this.title = title;
        this.code = code;
    }
}
