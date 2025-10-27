package io.github.mfinnnne.rhythmix.udf.builtin.postprocessing;

import io.github.mfinnnne.rhythmix.udf.ChainPostProcessingUDF;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for built-in post-processing UDF implementations
 */
public class BuiltInPostProcessingUDFTest {

    @Test
    @DisplayName("Test DefaultClearUDF - always returns true")
    void testDefaultClearUDFAlwaysReturnsTrue() {
        ChainPostProcessingUDF defaultClear = new DefaultClearUDF();
        
        assertEquals("clear", defaultClear.getName());
        
        // Test with various inputs - should always return true
        assertTrue(defaultClear.handle(new ArrayList<>(), 0.0));
        assertTrue(defaultClear.handle(new ArrayList<>(), 100.0));
        assertTrue(defaultClear.handle(new ArrayList<>(), -50.0));
        assertTrue(defaultClear.handle(new ArrayList<>(), null));
        
        // Test with non-empty aggregated data
        List<RhythmixEventData> data = new ArrayList<>();
        data.add(new RhythmixEventData("test", "value", new Timestamp(System.currentTimeMillis())));
        assertTrue(defaultClear.handle(data, 50.0));
        assertTrue(defaultClear.handle(data, null));
    }

    @Test
    @DisplayName("Test ConditionalClearUDF - returns true when result > 100.0")
    void testConditionalClearUDFThresholdLogic() {
        ChainPostProcessingUDF conditionalClear = new ConditionalClearUDF();
        
        assertEquals("conditionalClear", conditionalClear.getName());
        
        // Test above threshold
        assertTrue(conditionalClear.handle(new ArrayList<>(), 150.0));
        assertTrue(conditionalClear.handle(new ArrayList<>(), 100.1));
        assertTrue(conditionalClear.handle(new ArrayList<>(), 1000.0));
        
        // Test at or below threshold
        assertFalse(conditionalClear.handle(new ArrayList<>(), 100.0));
        assertFalse(conditionalClear.handle(new ArrayList<>(), 50.0));
        assertFalse(conditionalClear.handle(new ArrayList<>(), 0.0));
        assertFalse(conditionalClear.handle(new ArrayList<>(), -100.0));
    }

    @Test
    @DisplayName("Test ConditionalClearUDF - handles null result")
    void testConditionalClearUDFNullResult() {
        ChainPostProcessingUDF conditionalClear = new ConditionalClearUDF();
        
        // Null result should return false
        assertFalse(conditionalClear.handle(new ArrayList<>(), null));
    }

    @Test
    @DisplayName("Test ConditionalClearUDF - with aggregated data")
    void testConditionalClearUDFWithAggregatedData() {
        ChainPostProcessingUDF conditionalClear = new ConditionalClearUDF();
        
        List<RhythmixEventData> data = new ArrayList<>();
        data.add(new RhythmixEventData("test1", "value1", new Timestamp(System.currentTimeMillis())));
        data.add(new RhythmixEventData("test2", "value2", new Timestamp(System.currentTimeMillis())));
        
        // Should still check threshold regardless of aggregated data
        assertTrue(conditionalClear.handle(data, 150.0));
        assertFalse(conditionalClear.handle(data, 50.0));
    }



    @Test
    @DisplayName("Test DefaultClearUDF - edge cases")
    void testDefaultClearUDFEdgeCases() {
        ChainPostProcessingUDF defaultClear = new DefaultClearUDF();
        
        // Test with extreme values
        assertTrue(defaultClear.handle(new ArrayList<>(), Double.MAX_VALUE));
        assertTrue(defaultClear.handle(new ArrayList<>(), Double.MIN_VALUE));
        assertTrue(defaultClear.handle(new ArrayList<>(), Double.POSITIVE_INFINITY));
        assertTrue(defaultClear.handle(new ArrayList<>(), Double.NEGATIVE_INFINITY));
    }

    @Test
    @DisplayName("Test ConditionalClearUDF - edge cases")
    void testConditionalClearUDFEdgeCases() {
        ChainPostProcessingUDF conditionalClear = new ConditionalClearUDF();
        
        // Test with extreme values
        assertTrue(conditionalClear.handle(new ArrayList<>(), Double.MAX_VALUE));
        assertTrue(conditionalClear.handle(new ArrayList<>(), Double.POSITIVE_INFINITY));
        assertFalse(conditionalClear.handle(new ArrayList<>(), Double.MIN_VALUE));
        assertFalse(conditionalClear.handle(new ArrayList<>(), Double.NEGATIVE_INFINITY));
    }

    @Test
    @DisplayName("Test ConditionalClearUDF - boundary values")
    void testConditionalClearUDFBoundaryValues() {
        ChainPostProcessingUDF conditionalClear = new ConditionalClearUDF();
        
        // Test exact boundary
        assertFalse(conditionalClear.handle(new ArrayList<>(), 100.0));
        assertTrue(conditionalClear.handle(new ArrayList<>(), 100.00001));
        
        // Test near boundary
        assertFalse(conditionalClear.handle(new ArrayList<>(), 99.99999));
        assertTrue(conditionalClear.handle(new ArrayList<>(), 100.00001));
    }

    @Test
    @DisplayName("Test DefaultClearUDF - with different number types")
    void testDefaultClearUDFDifferentNumberTypes() {
        ChainPostProcessingUDF defaultClear = new DefaultClearUDF();
        
        // Test with different Number types
        assertTrue(defaultClear.handle(new ArrayList<>(), 100));           // Integer
        assertTrue(defaultClear.handle(new ArrayList<>(), 100L));          // Long
        assertTrue(defaultClear.handle(new ArrayList<>(), 100.0f));        // Float
        assertTrue(defaultClear.handle(new ArrayList<>(), 100.0));         // Double
    }

    @Test
    @DisplayName("Test ConditionalClearUDF - with different number types")
    void testConditionalClearUDFDifferentNumberTypes() {
        ChainPostProcessingUDF conditionalClear = new ConditionalClearUDF();
        
        // Test with different Number types
        assertTrue(conditionalClear.handle(new ArrayList<>(), 150));       // Integer
        assertTrue(conditionalClear.handle(new ArrayList<>(), 150L));      // Long
        assertTrue(conditionalClear.handle(new ArrayList<>(), 150.0f));    // Float
        assertTrue(conditionalClear.handle(new ArrayList<>(), 150.0));     // Double
        
        assertFalse(conditionalClear.handle(new ArrayList<>(), 50));       // Integer
        assertFalse(conditionalClear.handle(new ArrayList<>(), 50L));      // Long
        assertFalse(conditionalClear.handle(new ArrayList<>(), 50.0f));    // Float
        assertFalse(conditionalClear.handle(new ArrayList<>(), 50.0));     // Double
    }
}

