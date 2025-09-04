package com.df.rhythmix.udf;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.Compiler;
import com.df.rhythmix.execute.Executor;
import com.df.rhythmix.lib.Register;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.util.RhythmixEventData;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.*;

import java.sql.Timestamp;

/**
 * Test cases for Meet UDF functionality with auto-import support
 */
class ChainMeetUDFTest {

    @BeforeAll
    static void setUp() {
        // Enable debug mode
        TemplateEngine.enableDebugModel(true);
    }

    /**
     * Simple custom meet UDF for testing - checks if value is greater than 15
     */
    static public class CustomThresholdChainMeetUDF implements ChainMeetUDF {
        @Override
        public String getName() {
            return "customThresholdMeet";
        }

        @Override
        public boolean meet(Number calculatedValue) {
            try {
                if (calculatedValue != null) {
                    return calculatedValue.doubleValue() > 15.0;
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * Range meet UDF for testing - checks if value is between 20 and 100
     */
    static public class CustomRangeChainMeetUDF implements ChainMeetUDF {
        @Override
        public String getName() {
            return "customRangeMeet";
        }

        @Override
        public boolean meet(Number calculatedValue) {
            try {
                if (calculatedValue != null) {
                    double value = calculatedValue.doubleValue();
                    return value >= 20.0 && value <= 100.0;
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }
    }

    @Test
    @DisplayName("Test built-in ThresholdMeetUDF - threshold checking")
    void testBuiltInThresholdMeetUDF() throws TranslatorException {
        // Test built-in thresholdMeet (threshold >= 10)
        String code = "filter(>0).sum().thresholdMeet()";
        Executor executor = Compiler.compile(code);

        // Test data - sum should be >= 10 to pass thresholdMeet
        RhythmixEventData event1 = Util.genEventData("sensor1", "3", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData event2 = Util.genEventData("sensor2", "4", new Timestamp(System.currentTimeMillis() + 100));
        RhythmixEventData event3 = Util.genEventData("sensor3", "5", new Timestamp(System.currentTimeMillis() + 200));

        boolean result = false;
        result = executor.execute(event1); // Sum = 3
        Assertions.assertFalse(result); // 3 < 10, should not meet threshold

        result = executor.execute(event2); // Sum = 3 + 4 = 7
        Assertions.assertFalse(result); // 7 < 10, should not meet threshold

        result = executor.execute(event3); // Sum = 3 + 4 + 5 = 12
        Assertions.assertTrue(result); // 12 >= 10, should meet threshold
    }

    @Test
    @DisplayName("Test built-in RangeMeetUDF - range checking")
    void testBuiltInRangeMeetUDF() throws TranslatorException {
        // Test built-in rangeMeet (range 5-50)
        String code = "filter(>0).avg().rangeMeet()";
        Executor executor = Compiler.compile(code);

        // Test data - average should be between 5-50 to pass rangeMeet
        RhythmixEventData event1 = Util.genEventData("sensor1", "10", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData event2 = Util.genEventData("sensor2", "20", new Timestamp(System.currentTimeMillis() + 100));
        RhythmixEventData event3 = Util.genEventData("sensor3", "30", new Timestamp(System.currentTimeMillis() + 200));

        boolean result = false;
        result = executor.execute(event1); // Avg = 10
        Assertions.assertTrue(result); // 10 is in range [5, 50]

        result = executor.execute(event2); // Avg = (10 + 20) / 2 = 15
        Assertions.assertTrue(result); // 15 is in range [5, 50]

        result = executor.execute(event3); // Avg = (10 + 20 + 30) / 3 = 20
        Assertions.assertTrue(result); // 20 is in range [5, 50]
    }

    @Test
    @DisplayName("Test built-in PositiveMeetUDF - positive value checking")
    void testBuiltInPositiveMeetUDF() throws TranslatorException {
        // Test built-in positiveMeet (value > 0)
        String code = "filter(>-100).sum().positiveMeet()";
        Executor executor = Compiler.compile(code);

        // Test data with negative values - sum should be > 0 to pass positiveMeet
        RhythmixEventData event1 = Util.genEventData("sensor1", "-5", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData event2 = Util.genEventData("sensor2", "-3", new Timestamp(System.currentTimeMillis() + 100));
        RhythmixEventData event3 = Util.genEventData("sensor3", "10", new Timestamp(System.currentTimeMillis() + 200));

        boolean result = false;
        result = executor.execute(event1); // Sum = -5
        Assertions.assertFalse(result); // -5 <= 0, should not meet positive condition

        result = executor.execute(event2); // Sum = -5 + (-3) = -8
        Assertions.assertFalse(result); // -8 <= 0, should not meet positive condition

        result = executor.execute(event3); // Sum = -5 + (-3) + 10 = 2
        Assertions.assertTrue(result); // 2 > 0, should meet positive condition
    }

    @Test
    @DisplayName("Test built-in EvenMeetUDF - even number checking")
    void testBuiltInEvenMeetUDF() throws TranslatorException {
        // Test built-in evenMeet (value is even integer)
        String code = "filter(>0).count().evenMeet()";
        Executor executor = Compiler.compile(code);

        // Test data - count should be even to pass evenMeet
        RhythmixEventData event1 = Util.genEventData("sensor1", "10", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData event2 = Util.genEventData("sensor2", "20", new Timestamp(System.currentTimeMillis() + 100));
        RhythmixEventData event3 = Util.genEventData("sensor3", "30", new Timestamp(System.currentTimeMillis() + 200));
        RhythmixEventData event4 = Util.genEventData("sensor4", "40", new Timestamp(System.currentTimeMillis() + 300));

        boolean result = false;
        result = executor.execute(event1); // Count = 1 (odd)
        Assertions.assertFalse(result); // 1 is odd, should not meet even condition

        result = executor.execute(event2); // Count = 2 (even)
        Assertions.assertTrue(result); // 2 is even, should meet even condition

        result = executor.execute(event3); // Count = 3 (odd)
        Assertions.assertFalse(result); // 3 is odd, should not meet even condition

        result = executor.execute(event4); // Count = 4 (even)
        Assertions.assertTrue(result); // 4 is even, should meet even condition
    }

    @Test
    @DisplayName("Test custom MeetUDF - custom threshold checking")
    void testCustomMeetUDF() throws TranslatorException {
        // Register custom meet UDF
        MeetUDFRegistry.registerMeetUDF(new CustomThresholdChainMeetUDF());

        // Test custom thresholdMeet (threshold > 15)
        String code = "filter(>0).sum().customThresholdMeet()";
        Executor executor = Compiler.compile(code);

        // Test data - sum should be > 15 to pass customThresholdMeet
        RhythmixEventData event1 = Util.genEventData("sensor1", "5", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData event2 = Util.genEventData("sensor2", "6", new Timestamp(System.currentTimeMillis() + 100));
        RhythmixEventData event3 = Util.genEventData("sensor3", "7", new Timestamp(System.currentTimeMillis() + 200));

        boolean result = false;
        result = executor.execute(event1); // Sum = 5
        Assertions.assertFalse(result); // 5 <= 15, should not meet custom threshold

        result = executor.execute(event2); // Sum = 5 + 6 = 11
        Assertions.assertFalse(result); // 11 <= 15, should not meet custom threshold

        result = executor.execute(event3); // Sum = 5 + 6 + 7 = 18
        Assertions.assertTrue(result); // 18 > 15, should meet custom threshold
    }

    @Test
    @DisplayName("Test custom RangeMeetUDF - custom range checking")
    void testCustomRangeMeetUDF() throws TranslatorException {
        // Register custom range meet UDF
        MeetUDFRegistry.registerMeetUDF(new CustomRangeChainMeetUDF());

        // Test custom rangeMeet (range 20-100)
        String code = "filter(>0).avg().customRangeMeet()";
        Executor executor = Compiler.compile(code);

        // Test data - average should be between 20-100 to pass customRangeMeet
        RhythmixEventData event1 = Util.genEventData("sensor1", "10", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData event2 = Util.genEventData("sensor2", "30", new Timestamp(System.currentTimeMillis() + 100));
        RhythmixEventData event3 = Util.genEventData("sensor3", "50", new Timestamp(System.currentTimeMillis() + 200));

        boolean result = false;
        result = executor.execute(event1); // Avg = 10
        Assertions.assertFalse(result); // 10 < 20, should not meet range

        result = executor.execute(event2); // Avg = (10 + 30) / 2 = 20
        Assertions.assertTrue(result); // 20 is in range [20, 100]

        result = executor.execute(event3); // Avg = (10 + 30 + 50) / 3 = 30
        Assertions.assertTrue(result); // 30 is in range [20, 100]
    }

    @Test
    @DisplayName("Test complex chain expression - multiple function composition")
    void testComplexChainExpressionWithMeetUDF() throws TranslatorException {
        // Test complex chain: filter -> limit -> sum -> thresholdMeet
        String code = "filter(>5).limit(3).sum().thresholdMeet()";
        Executor executor = Compiler.compile(code);

        // Test data - only values > 5 will be filtered, limited to 3, then summed
        RhythmixEventData event1 = Util.genEventData("sensor1", "3", new Timestamp(System.currentTimeMillis())); // Filtered out
        RhythmixEventData event2 = Util.genEventData("sensor2", "6", new Timestamp(System.currentTimeMillis() + 100)); // Included
        RhythmixEventData event3 = Util.genEventData("sensor3", "7", new Timestamp(System.currentTimeMillis() + 200)); // Included
        RhythmixEventData event4 = Util.genEventData("sensor4", "8", new Timestamp(System.currentTimeMillis() + 300)); // Included
        RhythmixEventData event5 = Util.genEventData("sensor5", "9", new Timestamp(System.currentTimeMillis() + 400)); // Limited out

        boolean result = false;
        result = executor.execute(event1); // Sum = 0 (filtered out)
        Assertions.assertFalse(result); // 0 < 10, should not meet threshold

        result = executor.execute(event2); // Sum = 6
        Assertions.assertFalse(result); // 6 < 10, should not meet threshold

        result = executor.execute(event3); // Sum = 6 + 7 = 13
        Assertions.assertTrue(result); // 13 >= 10, should meet threshold

        result = executor.execute(event4); // Sum = 8 = 8 (clear calculation list when the method return true)
        Assertions.assertFalse(result); // 9 < 10, should meet threshold

        result = executor.execute(event5); // Sum =7 + 8 = 15 (event5 limited out)
        Assertions.assertTrue(result); // 15 >= 10, should meet threshold
    }

    @Test
    @DisplayName("Test edge cases - empty data and extreme values")
    void testEdgeCasesWithMeetUDF() throws TranslatorException {
        // Test with no data passing filter
        String code1 = "filter(>100).sum().thresholdMeet()";
        Executor executor1 = Compiler.compile(code1);

        RhythmixEventData event1 = Util.genEventData("sensor1", "5", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData event2 = Util.genEventData("sensor2", "10", new Timestamp(System.currentTimeMillis() + 100));

        boolean result1 = false;
        result1 = executor1.execute(event1); // Sum = 0 (no data > 100)
        Assertions.assertFalse(result1); // 0 < 10, should not meet threshold

        result1 = executor1.execute(event2); // Sum = 0 (no data > 100)
        Assertions.assertFalse(result1); // 0 < 10, should not meet threshold

        // Test with exactly threshold value
        String code2 = "filter(>0).sum().thresholdMeet()";
        Executor executor2 = Compiler.compile(code2);

        RhythmixEventData event3 = Util.genEventData("sensor3", "10", new Timestamp(System.currentTimeMillis()));

        boolean result2 = false;
        result2 = executor2.execute(event3); // Sum = 10
        Assertions.assertTrue(result2); // 10 >= 10, should meet threshold (exactly at boundary)
    }

    @Test
    @DisplayName("Test MeetUDF auto-registration functionality")
    void testMeetUDFAutoRegistration() throws TranslatorException {
        // Test that built-in MeetUDFs are automatically registered
        Register.importFunction();

        // Verify that built-in MeetUDFs are registered
        Assertions.assertTrue(MeetUDFRegistry.isRegistered("thresholdMeet"));
        Assertions.assertTrue(MeetUDFRegistry.isRegistered("rangeMeet"));
        Assertions.assertTrue(MeetUDFRegistry.isRegistered("positiveMeet"));
        Assertions.assertTrue(MeetUDFRegistry.isRegistered("evenMeet"));

        // Test that we can use them in expressions
        String code = "filter(>0).sum().thresholdMeet()";
        Executor executor = Compiler.compile(code);

        RhythmixEventData event = Util.genEventData("sensor", "15", new Timestamp(System.currentTimeMillis()));
        boolean result = executor.execute(event);
        Assertions.assertTrue(result); // 15 >= 10, should meet threshold
    }
}
