package io.github.mfinnnne.rhythmix.execute;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Rhythmix expression entity
 *  @author MFine
 *  @version 1.0
 **/
@Data
public class RhythmixExpressionEntity implements Serializable {

    /**
     * Unique identifier for the expression.
     */
    private String id;

    /**
     * Name of the expression.
     */
    private String name;

    /**
     * The actual expression string.
     */
    private String expression;

    /**
     * Whether the expression is enabled for execution.
     */
    private boolean enable;

    /**
     * List of filter IDs associated with this expression.
     */
    private List<String> filterIds;



}
