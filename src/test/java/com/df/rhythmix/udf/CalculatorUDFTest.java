package com.df.rhythmix.udf;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.Compiler;
import com.df.rhythmix.execute.Executor;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.udf.builtin.calculator.MaxCalculator;
import com.df.rhythmix.udf.builtin.calculator.MinCalculator;
import com.df.rhythmix.util.EventData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.*;

/**
 * Test cases for Calculator UDF functionality with auto-import support
 */
class CalculatorUDFTest {

    @BeforeAll
    static void setUp() {
        // Enable debug mode
        TemplateEngine.enableDebugModel(true);
    }


    @Test
    @DisplayName("MyMaxCalculator - Test with positive integers")
    void testMyMaxCalculatorWithPositiveIntegers() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(5),
            createEventData(10),
            createEventData(3),
            createEventData(15),
            createEventData(7)
        );

        Number result = calculator.calculate(events);
        assertEquals(15L, result);
        assertTrue(result instanceof Long);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with negative numbers")
    void testMyMaxCalculatorWithNegativeNumbers() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(-5),
            createEventData(-10),
            createEventData(-3),
            createEventData(-15),
            createEventData(-7)
        );

        Number result = calculator.calculate(events);
        assertEquals(-3L, result);
        assertTrue(result instanceof Long);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with mixed positive and negative numbers")
    void testMyMaxCalculatorWithMixedNumbers() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(-5),
            createEventData(10),
            createEventData(-3),
            createEventData(15),
            createEventData(-7)
        );

        Number result = calculator.calculate(events);
        assertEquals(15L, result);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with decimal numbers")
    void testMyMaxCalculatorWithDecimals() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(5.5),
            createEventData(10.2),
            createEventData(3.7),
            createEventData(15.9),
            createEventData(7.1)
        );

        Number result = calculator.calculate(events);
        assertEquals(15.9, result.doubleValue(), 0.001);
        assertTrue(result instanceof Double);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with mixed integers and decimals")
    void testMyMaxCalculatorWithMixedTypes() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(5),      // Integer
            createEventData(10.5),   // Double
            createEventData(3),      // Integer
            createEventData(15.2),   // Double
            createEventData(7)       // Integer
        );

        Number result = calculator.calculate(events);
        assertEquals(15.2, result.doubleValue(), 0.001);
        assertTrue(result instanceof Double);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with string numbers")
    void testMyMaxCalculatorWithStringNumbers() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData("5"),
            createEventData("10"),
            createEventData("3"),
            createEventData("15"),
            createEventData("7")
        );

        Number result = calculator.calculate(events);
        assertEquals(15L, result);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with mixed string and numeric values")
    void testMyMaxCalculatorWithMixedStringAndNumeric() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(5),
            createEventData("10"),
            createEventData(3.5),
            createEventData("15.7"),
            createEventData(7)
        );

        Number result = calculator.calculate(events);
        assertEquals(15.7, result.doubleValue(), 0.001);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with null input")
    void testMyMaxCalculatorWithNullInput() {
        MaxCalculator calculator = new MaxCalculator();

        Number result = calculator.calculate(null);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with empty list")
    void testMyMaxCalculatorWithEmptyList() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = new ArrayList<>();
        Number result = calculator.calculate(events);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with null EventData objects")
    void testMyMaxCalculatorWithNullEventData() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(5),
            null,
            createEventData(10),
            null,
            createEventData(3)
        );

        Number result = calculator.calculate(events);
        assertEquals(10L, result);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with null values in EventData")
    void testMyMaxCalculatorWithNullValues() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(5),
            createEventDataWithNullValue(),
            createEventData(10),
            createEventDataWithNullValue(),
            createEventData(3)
        );

        Number result = calculator.calculate(events);
        assertEquals(10L, result);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with invalid string values")
    void testMyMaxCalculatorWithInvalidStrings() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(5),
            createEventData("abc"),      // Invalid string
            createEventData(10),
            createEventData("xyz"),      // Invalid string
            createEventData(3)
        );

        Number result = calculator.calculate(events);
        assertEquals(10L, result);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with all invalid values")
    void testMyMaxCalculatorWithAllInvalidValues() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData("abc"),
            createEventData("xyz"),
            createEventDataWithNullValue(),
            null
        );

        Number result = calculator.calculate(events);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with NaN values")
    void testMyMaxCalculatorWithNaNValues() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(5),
            createEventData(Double.NaN),  // NaN value
            createEventData(10),
            createEventData(3)
        );

        Number result = calculator.calculate(events);
        assertEquals(10L, result);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with infinite values")
    void testMyMaxCalculatorWithInfiniteValues() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(5),
            createEventData(Double.POSITIVE_INFINITY),
            createEventData(10),
            createEventData(3)
        );

        Number result = calculator.calculate(events);
        assertEquals(Double.POSITIVE_INFINITY, result.doubleValue());
        assertTrue(result instanceof Double);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with negative infinite values")
    void testMyMaxCalculatorWithNegativeInfiniteValues() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(5),
            createEventData(Double.NEGATIVE_INFINITY),
            createEventData(10),
            createEventData(3)
        );

        Number result = calculator.calculate(events);
        assertEquals(10L, result);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with single value")
    void testMyMaxCalculatorWithSingleValue() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(createEventData(42));

        Number result = calculator.calculate(events);
        assertEquals(42L, result);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with zero values")
    void testMyMaxCalculatorWithZeroValues() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(0),
            createEventData(-5),
            createEventData(0),
            createEventData(-10)
        );

        Number result = calculator.calculate(events);
        assertEquals(0L, result);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with very large numbers")
    void testMyMaxCalculatorWithLargeNumbers() {
        MaxCalculator calculator = new MaxCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(Long.MAX_VALUE),
            createEventData(1000000),
            createEventData(Long.MAX_VALUE - 1)
        );

        Number result = calculator.calculate(events);
        assertEquals((double) Long.MAX_VALUE, result.doubleValue(), 0.001);
    }

    // Helper methods for creating test data

    private EventData createEventData(Object value) {
        EventData eventData = new EventData();
        eventData.setValue(value.toString());
        return eventData;
    }

    private EventData createEventDataWithNullValue() {
        EventData eventData = new EventData();
        eventData.setValue(null);
        return eventData;
    }

    // Test cases for MinCalculator

    @Test
    @DisplayName("MinCalculator - Test getName method")
    void testMinCalculatorGetName() {
        MinCalculator calculator = new MinCalculator();
        assertEquals("mincalc", calculator.getName());
    }

    @Test
    @DisplayName("MinCalculator - Test with positive integers")
    void testMinCalculatorWithPositiveIntegers() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(15),
            createEventData(10),
            createEventData(3),
            createEventData(25),
            createEventData(7)
        );

        Number result = calculator.calculate(events);
        assertEquals(3L, result);
        assertTrue(result instanceof Long);
    }

    @Test
    @DisplayName("MinCalculator - Test with negative numbers")
    void testMinCalculatorWithNegativeNumbers() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(-5),
            createEventData(-10),
            createEventData(-3),
            createEventData(-15),
            createEventData(-7)
        );

        Number result = calculator.calculate(events);
        assertEquals(-15L, result);
        assertTrue(result instanceof Long);
    }

    @Test
    @DisplayName("MinCalculator - Test with mixed positive and negative numbers")
    void testMinCalculatorWithMixedNumbers() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(-5),
            createEventData(10),
            createEventData(-3),
            createEventData(15),
            createEventData(-7)
        );

        Number result = calculator.calculate(events);
        assertEquals(-7L, result);
    }

    @Test
    @DisplayName("MinCalculator - Test with decimal numbers")
    void testMinCalculatorWithDecimals() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(5.5),
            createEventData(10.2),
            createEventData(3.7),
            createEventData(15.9),
            createEventData(7.1)
        );

        Number result = calculator.calculate(events);
        assertEquals(3.7, result.doubleValue(), 0.001);
        assertTrue(result instanceof Double);
    }

    @Test
    @DisplayName("MinCalculator - Test with mixed integers and decimals")
    void testMinCalculatorWithMixedTypes() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(5),      // Integer
            createEventData(10.5),   // Double
            createEventData(3),      // Integer
            createEventData(2.8),    // Double (minimum)
            createEventData(7)       // Integer
        );

        Number result = calculator.calculate(events);
        assertEquals(2.8, result.doubleValue(), 0.001);
        assertTrue(result instanceof Double);
    }

    @Test
    @DisplayName("MinCalculator - Test with string numbers")
    void testMinCalculatorWithStringNumbers() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData("15"),
            createEventData("10"),
            createEventData("3"),
            createEventData("25"),
            createEventData("7")
        );

        Number result = calculator.calculate(events);
        assertEquals(3L, result);
    }

    @Test
    @DisplayName("MinCalculator - Test with mixed string and numeric values")
    void testMinCalculatorWithMixedStringAndNumeric() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(5),
            createEventData("10"),
            createEventData(3.5),
            createEventData("2.3"),  // Minimum value
            createEventData(7)
        );

        Number result = calculator.calculate(events);
        assertEquals(2.3, result.doubleValue(), 0.001);
    }

    @Test
    @DisplayName("MinCalculator - Test with null input")
    void testMinCalculatorWithNullInput() {
        MinCalculator calculator = new MinCalculator();

        Number result = calculator.calculate(null);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("MinCalculator - Test with empty list")
    void testMinCalculatorWithEmptyList() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = new ArrayList<>();
        Number result = calculator.calculate(events);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("MinCalculator - Test with null EventData objects")
    void testMinCalculatorWithNullEventData() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(15),
            null,
            createEventData(3),  // Minimum
            null,
            createEventData(10)
        );

        Number result = calculator.calculate(events);
        assertEquals(3L, result);
    }

    @Test
    @DisplayName("MinCalculator - Test with null values in EventData")
    void testMinCalculatorWithNullValues() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(15),
            createEventDataWithNullValue(),
            createEventData(3),  // Minimum
            createEventDataWithNullValue(),
            createEventData(10)
        );

        Number result = calculator.calculate(events);
        assertEquals(3L, result);
    }

    @Test
    @DisplayName("MinCalculator - Test with invalid string values")
    void testMinCalculatorWithInvalidStrings() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(15),
            createEventData("abc"),      // Invalid string
            createEventData(3),          // Minimum
            createEventData("xyz"),      // Invalid string
            createEventData(10)
        );

        Number result = calculator.calculate(events);
        assertEquals(3L, result);
    }

    @Test
    @DisplayName("MinCalculator - Test with all invalid values")
    void testMinCalculatorWithAllInvalidValues() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData("abc"),
            createEventData("xyz"),
            createEventDataWithNullValue(),
            null
        );

        Number result = calculator.calculate(events);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("MinCalculator - Test with NaN values")
    void testMinCalculatorWithNaNValues() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(15),
            createEventData(Double.NaN),  // NaN value
            createEventData(3),           // Minimum
            createEventData(10)
        );

        Number result = calculator.calculate(events);
        assertEquals(3L, result);
    }

    @Test
    @DisplayName("MinCalculator - Test with infinite values")
    void testMinCalculatorWithInfiniteValues() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(15),
            createEventData(Double.NEGATIVE_INFINITY),  // Minimum (negative infinity)
            createEventData(3),
            createEventData(10)
        );

        Number result = calculator.calculate(events);
        assertEquals(Double.NEGATIVE_INFINITY, result.doubleValue());
        assertTrue(result instanceof Double);
    }

    @Test
    @DisplayName("MinCalculator - Test with positive infinite values")
    void testMinCalculatorWithPositiveInfiniteValues() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(15),
            createEventData(Double.POSITIVE_INFINITY),
            createEventData(3),  // Minimum
            createEventData(10)
        );

        Number result = calculator.calculate(events);
        assertEquals(3L, result);
    }

    @Test
    @DisplayName("MinCalculator - Test with single value")
    void testMinCalculatorWithSingleValue() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(createEventData(42));

        Number result = calculator.calculate(events);
        assertEquals(42L, result);
    }

    @Test
    @DisplayName("MinCalculator - Test with zero values")
    void testMinCalculatorWithZeroValues() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(0),   // Minimum
            createEventData(5),
            createEventData(0),   // Also minimum
            createEventData(10)
        );

        Number result = calculator.calculate(events);
        assertEquals(0L, result);
    }

    @Test
    @DisplayName("MinCalculator - Test with very small numbers")
    void testMinCalculatorWithSmallNumbers() {
        MinCalculator calculator = new MinCalculator();

        List<EventData> events = Arrays.asList(
            createEventData(Long.MIN_VALUE),  // Minimum (most negative long)
            createEventData(1000000),
            createEventData(Long.MIN_VALUE + 1)
        );

        Number result = calculator.calculate(events);
        assertEquals((double) Long.MIN_VALUE, result.doubleValue(), 0.001);
    }

    @Test
    @DisplayName("Test customized CalculatorUDF")
    void testCustomizedCalculatorUDF() throws TranslatorException {
        String code = "filter(>0).limit(5).maxcalc().meet(>10)";
        Executor executor = Compiler.compile(code);

        // Create test data with mixed positive and negative values
        List<EventData> testData = Arrays.asList(
            createEventDataWithTimestamp(15, System.currentTimeMillis()),      // Should pass filter (>0)
            createEventDataWithTimestamp(-5, System.currentTimeMillis() + 1000), // Should be filtered out (<= 0)
            createEventDataWithTimestamp(25, System.currentTimeMillis() + 2000), // Should pass filter (>0)
            createEventDataWithTimestamp(8, System.currentTimeMillis() + 3000),  // Should pass filter (>0)
            createEventDataWithTimestamp(-2, System.currentTimeMillis() + 4000), // Should be filtered out (<= 0)
            createEventDataWithTimestamp(30, System.currentTimeMillis() + 5000), // Should pass filter (>0)
            createEventDataWithTimestamp(12, System.currentTimeMillis() + 6000), // Should pass filter (>0)
            createEventDataWithTimestamp(18, System.currentTimeMillis() + 7000), // Should pass filter (>0) but limited
            createEventDataWithTimestamp(22, System.currentTimeMillis() + 8000), // Should pass filter (>0) but limited
            createEventDataWithTimestamp(35, System.currentTimeMillis() + 9000)  // Should pass filter (>0) but limited
        );

        // Execute the expression
        // Expected flow:
        // 1. filter(>0) should keep: [15, 25, 8, 30, 12, 18, 22, 35]
        // 2. limit(5) should keep first 5: [15, 25, 8, 30, 12]
        // 3. myMax() should return: 30 (maximum of the 5 values)
        // 4. meet(>10) should return true since 30 > 10

        Boolean result = executor.execute(testData.toArray());

        // Verify the result
        assertNotNull(result, "Result should not be null");
        assertTrue(result, "Expression should return true since max(15,25,8,30,12) = 30 > 10");

        // Test with data that should fail the meet condition
        String code2 = "filter(>0).limit(3).maxcalc().meet(>50)";
        Executor executor2 = Compiler.compile(code2);

        List<EventData> testData2 = Arrays.asList(
            createEventDataWithTimestamp(5, System.currentTimeMillis()),
            createEventDataWithTimestamp(10, System.currentTimeMillis() + 1000),
            createEventDataWithTimestamp(15, System.currentTimeMillis() + 2000),
            createEventDataWithTimestamp(20, System.currentTimeMillis() + 3000)
        );

        // Expected flow:
        // 1. filter(>0) should keep: [5, 10, 15, 20]
        // 2. limit(3) should keep first 3: [5, 10, 15]
        // 3. myMax() should return: 15 (maximum of the 3 values)
        // 4. meet(>50) should return false since 15 <= 50

        Boolean result2 = executor2.execute(testData2.toArray());

        assertNotNull(result2, "Result2 should not be null");
        assertFalse(result2, "Expression should return false since max(5,10,15) = 15 <= 50");

        // Test with edge case - all values filtered out
        String code3 = "filter(>100).limit(5).maxcalc().meet(>0)";
        Executor executor3 = Compiler.compile(code3);

        List<EventData> testData3 = Arrays.asList(
            createEventDataWithTimestamp(5, System.currentTimeMillis()),
            createEventDataWithTimestamp(10, System.currentTimeMillis() + 1000),
            createEventDataWithTimestamp(15, System.currentTimeMillis() + 2000)
        );

        Boolean result3 = executor3.execute(testData3.toArray());

        assertNotNull(result3, "Result3 should not be null");
        assertFalse(result3, "Expression should return false since no values pass filter, myMax returns 0, and 0 <= 0");
    }



    // Helper method to create EventData with timestamp
    private EventData createEventDataWithTimestamp(Object value, long timestamp) {
        EventData eventData = new EventData();
        eventData.setValue(value.toString());
        eventData.setTs(new Timestamp(timestamp));
        return eventData;
    }
}
