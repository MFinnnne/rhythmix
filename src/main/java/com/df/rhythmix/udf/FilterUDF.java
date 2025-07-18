package com.df.rhythmix.udf;

import com.df.rhythmix.util.EventData;

/**
 * Filter UDF interface for custom filtering logic in Rhythmix expressions.
 * 
 * Users can implement this interface to create custom filter functions that
 * determine whether to keep or discard EventData based on custom logic.
 * 
 * @author MFine
 * @version 1.0
 * @date 2025-07-18
 */
public interface FilterUDF {
    
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
    boolean filter(EventData event);
}
