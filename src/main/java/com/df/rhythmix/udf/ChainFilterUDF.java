package com.df.rhythmix.udf;

import com.df.rhythmix.util.RhythmixEventData;

import java.util.List;

/**
 * Filter UDF interface for custom filtering logic in Rhythmix expressions.
 *
 * Users can implement this interface to create custom filter functions that
 * determine whether to keep or discard EventData based on custom logic.
 * Supports both individual event filtering and batch list filtering.
 *
 * @author MFine
 * @version 1.0
 * @date 2025-07-18
 */
public interface ChainFilterUDF {

    /**
     * Returns a unique name for the filter UDF that users can reference in expressions.
     * This name will be used in Rhythmix expressions like: filter(myCustomFilter())
     *
     * @return A unique identifier for this filter UDF
     */
    String getName();

    /**
     * Determines whether to keep or discard the given EventData.
     *
     * @param event The EventData object to be filtered
     * @return true to keep the data, false to discard it
     */
    default boolean filter(RhythmixEventData event) {
        return true;
    }

    /**
     * Filters a list of EventData objects, keeping only those that pass the filter condition.
     * Default implementation applies the single-event filter method to each event in the list.
     *
     * @param events The list of EventData objects to be filtered
     * @return A new list containing only the EventData objects that passed the filter
     */
    default List<RhythmixEventData> filter(List<RhythmixEventData> events) {
        return events;
    }
}
