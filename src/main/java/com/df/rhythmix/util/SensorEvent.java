package com.df.rhythmix.util;

import lombok.Data;

@Data
public class SensorEvent {
    private String id;
    private String name;
    private String value;
    private String valueType;
}
