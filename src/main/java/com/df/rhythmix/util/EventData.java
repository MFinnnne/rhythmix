package com.df.rhythmix.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventData {
    private String id;
    private String name;
    private String value;
    private Timestamp ts;
    private EventValueType valueType;
}
