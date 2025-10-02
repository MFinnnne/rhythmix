package com.df.rhythmix.udf;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.RhythmixCompiler;
import com.df.rhythmix.execute.RhythmixExecutor;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.udf.builtin.calculator.MaxChainCalculator;
import com.df.rhythmix.udf.builtin.calculator.MinChainCalculator;
import com.df.rhythmix.util.RhythmixEventData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.*;

/**
 * Test cases for Calculator UDF functionality with auto-import support
 */
class ChainCalculatorUDFTest {

    @BeforeAll
    static void setUp() {
        // Enable debug mode
        TemplateEngine.enableDebugModel(true);
    }


    @Test
    @DisplayName("MyMaxCalculator - Test with positive integers")
    void testMyMaxCalculatorWithPositiveIntegers() {
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MaxChainCalculator calculator = new MaxChainCalculator();

        Number result = calculator.calculate(null);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with empty list")
    void testMyMaxCalculatorWithEmptyList() {
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = new ArrayList<>();
        Number result = calculator.calculate(events);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with null EventData objects")
    void testMyMaxCalculatorWithNullEventData() {
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(createEventData(42));

        Number result = calculator.calculate(events);
        assertEquals(42L, result);
    }

    @Test
    @DisplayName("MyMaxCalculator - Test with zero values")
    void testMyMaxCalculatorWithZeroValues() {
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MaxChainCalculator calculator = new MaxChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
            createEventData(Long.MAX_VALUE),
            createEventData(1000000),
            createEventData(Long.MAX_VALUE - 1)
        );

        Number result = calculator.calculate(events);
        assertEquals((double) Long.MAX_VALUE, result.doubleValue(), 0.001);
    }

    // Helper methods for creating test data

    private RhythmixEventData createEventData(Object value) {
        RhythmixEventData rhythmixEventData = new RhythmixEventData();
        rhythmixEventData.setValue(value.toString());
        return rhythmixEventData;
    }

    private RhythmixEventData createEventDataWithNullValue() {
        RhythmixEventData rhythmixEventData = new RhythmixEventData();
        rhythmixEventData.setValue(null);
        return rhythmixEventData;
    }

    // Test cases for MinCalculator

    @Test
    @DisplayName("MinCalculator - Test getName method")
    void testMinCalculatorGetName() {
        MinChainCalculator calculator = new MinChainCalculator();
        assertEquals("mincalc", calculator.getName());
    }

    @Test
    @DisplayName("MinCalculator - Test with positive integers")
    void testMinCalculatorWithPositiveIntegers() {
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MinChainCalculator calculator = new MinChainCalculator();

        Number result = calculator.calculate(null);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("MinCalculator - Test with empty list")
    void testMinCalculatorWithEmptyList() {
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = new ArrayList<>();
        Number result = calculator.calculate(events);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("MinCalculator - Test with null EventData objects")
    void testMinCalculatorWithNullEventData() {
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(createEventData(42));

        Number result = calculator.calculate(events);
        assertEquals(42L, result);
    }

    @Test
    @DisplayName("MinCalculator - Test with zero values")
    void testMinCalculatorWithZeroValues() {
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        MinChainCalculator calculator = new MinChainCalculator();

        List<RhythmixEventData> events = Arrays.asList(
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
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        // Create test data with mixed positive and negative values
        List<RhythmixEventData> testData = Arrays.asList(
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

        Boolean result = rhythmixExecutor.execute(testData.toArray());

        // Verify the result
        assertNotNull(result, "Result should not be null");
        assertTrue(result, "Expression should return true since max(15,25,8,30,12) = 30 > 10");

        // Test with data that should fail the meet condition
        String code2 = "filter(>0).limit(3).maxcalc().meet(>50)";
        RhythmixExecutor rhythmixExecutor2 = RhythmixCompiler.compile(code2);

        List<RhythmixEventData> testData2 = Arrays.asList(
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

        Boolean result2 = rhythmixExecutor2.execute(testData2.toArray());

        assertNotNull(result2, "Result2 should not be null");
        assertFalse(result2, "Expression should return false since max(5,10,15) = 15 <= 50");

        // Test with edge case - all values filtered out
        String code3 = "filter(>100).limit(5).maxcalc().meet(>0)";
        RhythmixExecutor rhythmixExecutor3 = RhythmixCompiler.compile(code3);

        List<RhythmixEventData> testData3 = Arrays.asList(
            createEventDataWithTimestamp(5, System.currentTimeMillis()),
            createEventDataWithTimestamp(10, System.currentTimeMillis() + 1000),
            createEventDataWithTimestamp(15, System.currentTimeMillis() + 2000)
        );

        Boolean result3 = rhythmixExecutor3.execute(testData3.toArray());

        assertNotNull(result3, "Result3 should not be null");
        assertFalse(result3, "Expression should return false since no values pass filter, myMax returns 0, and 0 <= 0");
    }



    // Helper method to create EventData with timestamp
    private RhythmixEventData createEventDataWithTimestamp(Object value, long timestamp) {
        RhythmixEventData rhythmixEventData = new RhythmixEventData();
        rhythmixEventData.setValue(value.toString());
        rhythmixEventData.setTs(new Timestamp(timestamp));
        return rhythmixEventData;
    }
}
