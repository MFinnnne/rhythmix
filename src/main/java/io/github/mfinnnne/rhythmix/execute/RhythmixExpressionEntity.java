package io.github.mfinnnne.rhythmix.execute;

import lombok.Data;

import java.util.List;

/**
 * Rhythmix expression entity
 *  @author MFine
 *  @date 2025/11/8 16:04
 *  @version 1.0
 **/
@Data
public class RhythmixExpressionEntity {
    private String id;
    private String name;
    private String expression;
    private boolean enable;
    private List<String> filterIds;
}
