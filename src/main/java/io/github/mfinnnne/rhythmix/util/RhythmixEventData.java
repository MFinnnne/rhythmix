package io.github.mfinnnne.rhythmix.util;

import cn.hutool.core.util.IdUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * <p>RhythmixEventData class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RhythmixEventData {

    /**
     * <p>Constructor for RhythmixEventData.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     * @param ts a java.sql.Timestamp object.
     */
    public RhythmixEventData(String id, String name, String value, Timestamp ts) {
        this.id = id;
        this.ts = ts;
        this.name = name;
        this.value = value;
    }

    /**
     * <p>Constructor for RhythmixEventData.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     * @param ts a java.sql.Timestamp object.
     */
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
