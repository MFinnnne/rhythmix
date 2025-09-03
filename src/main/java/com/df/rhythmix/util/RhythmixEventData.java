package com.df.rhythmix.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RhythmixEventData {

    public RhythmixEventData(String id, String name, String value, Timestamp ts) {
        this.id = id;
        this.ts = ts;
        this.name = name;
        this.value = value;
    }

    private String id;
    private String name;
    private String value;
    private Timestamp ts;

    //Extended field
    private String[] args;
}
