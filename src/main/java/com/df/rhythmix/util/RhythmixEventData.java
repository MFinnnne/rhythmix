package com.df.rhythmix.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RhythmixEventData {

    public RhythmixEventData(String id, String name, String value, Timestamp ts) {
        this.id = id;
        this.ts = ts;
        this.name = name;
        this.value = value;
    }

    public RhythmixEventData(String name, String value, Timestamp ts) {
        this.id = IdUtil.nanoId();
        this.name = name;
        this.value = value;
        this.ts = ts;
    }

    private String id;
    private String code;
    private String serialNumber;
    private String name;
    private String value;
    private Timestamp ts;

    //Extended field
    private String[] args;
}
