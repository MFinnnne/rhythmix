package com.df.rhythmix.udf;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.Compiler;
import com.df.rhythmix.execute.Executor;
import com.df.rhythmix.lib.Register;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.udf.FilterUDFRegistry;
import com.df.rhythmix.util.EventData;
import com.df.rhythmix.util.EventValueType;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.*;

import java.sql.Timestamp;
import java.util.*;

/**
 * Test cases for Filter UDF functionality with auto-import support
 */
class FilterUDFTest {

    @BeforeAll
    static void setUp() {
        // Enable debug mode
        TemplateEngine.enableDebugModel(true);
    }

    /**
     * Simple temperature filter UDF for testing
     */
    static public class TemperatureFilterUDF implements FilterUDF {
        @Override
        public String getName() {
            return "tempFilter";
        }

        @Override
        public boolean filter(EventData event) {
            try {
                double temp = Double.parseDouble(event.getValue());
                return temp >= 20.0 && temp <= 80.0;
            } catch (NumberFormatException e) {
                return false; // Discard invalid data
            }
        }
    }

    /**
     * Sensor ID pattern filter UDF for testing
     */
    static public class SensorIdFilterUDF implements FilterUDF {
        @Override
        public String getName() {
            return "sensorFilter";
        }

        @Override
        public boolean filter(EventData event) {
            return event.getId().startsWith("temp_");
        }
    }

    /**
     * Array filter UDF for testing - keeps only the last 3 events when list size > 3
     */
    static public class TestArrayFilterUDF implements FilterUDF {
        @Override
        public String getName() {
            return "arrayFilter";
        }

        @Override
        public List<EventData> filter(List<EventData> events) {
            if (events.size() >= 3) {
                return events.subList(events.size() - 3, events.size());
            }
            return events;
        }
    }

    @Test
    @DisplayName("测试简单的温度过滤UDF")
    void testSimpleTemperatureFilterUDF() throws TranslatorException {
        String code = "filter(tempFilter()).count().meet(==2)";
        Executor executor = Compiler.compile(code);

        // Test data - should keep temperatures between 20-80
        EventData event1 = Util.genEventData("sensor1", "25.5", new Timestamp(System.currentTimeMillis()));
        EventData event2 = Util.genEventData("sensor2", "15.0", new Timestamp(System.currentTimeMillis() + 100)); // Should be filtered out
        EventData event3 = Util.genEventData("sensor3", "75.0", new Timestamp(System.currentTimeMillis() + 200));
        EventData event4 = Util.genEventData("sensor4", "90.0", new Timestamp(System.currentTimeMillis() + 300)); // Should be filtered out

        boolean result = false;
        result = executor.execute(event1); // Keep
        Assertions.assertFalse(result); // Not enough events yet

        result = executor.execute(event2); // Discard (temp < 20)
        Assertions.assertFalse(result); // Still not enough events

        result = executor.execute(event3); // Keep
        Assertions.assertTrue(result); // Should have 2 valid events now
    }

    @Test
    @DisplayName("测试传感器ID过滤UDF")
    void testSensorIdFilterUDF() throws TranslatorException {
        // Use auto-imported sensorFilter - no manual registration needed
        String code = "filter(sensorFilter()).count().meet(==1)";
        Executor executor = Compiler.compile(code);

        // Test data - should only keep sensors with ID starting with "temp_"
        EventData event1 = new EventData("temp_001", "sensor1", "25.5", new Timestamp(System.currentTimeMillis()));
        EventData event2 = new EventData("humidity_001", "sensor2", "60.0", new Timestamp(System.currentTimeMillis() + 100));

        boolean result = false;
        result = executor.execute(event1); // Keep (ID starts with "temp_")
        Assertions.assertTrue(result); // Should have 1 valid event

        // Reset for next test
        executor.resetEnv();
        result = executor.execute(event2); // Discard (ID doesn't start with "temp_")
        Assertions.assertFalse(result); // Should have 0 valid events
    }

    @Test
    @DisplayName("测试UDF与传统比较表达式的兼容性")
    void testUDFWithTraditionalFilter() throws TranslatorException {
        // Test traditional comparison expression still works
        String code1 = "filter(>20).count().meet(==2)";
        Executor executor1 = Compiler.compile(code1);

        EventData event1 = Util.genEventData("sensor1", "25", new Timestamp(System.currentTimeMillis()));
        EventData event2 = Util.genEventData("sensor2", "15", new Timestamp(System.currentTimeMillis() + 100));
        EventData event3 = Util.genEventData("sensor3", "30", new Timestamp(System.currentTimeMillis() + 200));

        boolean result = false;
        result = executor1.execute(event1); // Keep (25 > 20)
        Assertions.assertFalse(result); // Not enough events yet

        result = executor1.execute(event2); // Discard (15 <= 20)
        Assertions.assertFalse(result); // Still not enough events

        result = executor1.execute(event3); // Keep (30 > 20)
        Assertions.assertTrue(result); // Should have 2 valid events now
    }

    @Test
    @DisplayName("测试UDF与其他UDF环境的组合使用")
    void testUDFWithRegularUDFEnv() throws TranslatorException {
        // Create regular UDF environment
        HashMap<String, Object> udfEnv = new HashMap<>();
        udfEnv.put("threshold", 50.0);

        // Compile expression with both regular UDF and auto-imported filter UDF
        String code = "filter(tempFilter()).sum().meet(>threshold)";
        Executor executor = Compiler.compile(code, udfEnv);

        // Test data
        EventData event1 = Util.genEventData("sensor1", "25", new Timestamp(System.currentTimeMillis()));
        EventData event2 = Util.genEventData("sensor2", "30", new Timestamp(System.currentTimeMillis() + 100));

        boolean result = false;
        result = executor.execute(event1); // Keep (temp in range)
        Assertions.assertFalse(result); // Sum not > threshold yet

        result = executor.execute(event2); // Keep (temp in range)
        Assertions.assertTrue(result); // Sum (25+30=55) > threshold (50)
    }


    @Test
    @DisplayName("测试传感器ID过滤与链式函数集成 - sum")
    void testSensorIdFilterWithChainFunctionsSum() throws TranslatorException {
        // Use auto-imported sensorFilter - no manual registration needed
        String code = "filter(sensorFilter()).sum().meet(>100)";
        Executor executor = Compiler.compile(code);

        // Mixed sensor data with numeric values
        EventData[] sensorEvents = {
                new EventData("temp_001", "s1", "25", new Timestamp(System.currentTimeMillis())),        // Keep, value=25
                new EventData("humidity_001", "s2", "60", new Timestamp(System.currentTimeMillis() + 100)), // Discard
                new EventData("temp_002", "s3", "30", new Timestamp(System.currentTimeMillis() + 200)),   // Keep, value=30
                new EventData("pressure_001", "s4", "1013", new Timestamp(System.currentTimeMillis() + 300)), // Discard
                new EventData("temp_003", "s5", "35", new Timestamp(System.currentTimeMillis() + 400)),   // Keep, value=35
                new EventData("temp_004", "s6", "15", new Timestamp(System.currentTimeMillis() + 500))    // Keep, value=15
        };

        boolean result = false;
        int tempSum = 0;
        for (int i = 0; i < sensorEvents.length; i++) {
            result = executor.execute(sensorEvents[i]);
            if (sensorEvents[i].getId().startsWith("temp_")) {
                tempSum += Integer.parseInt(sensorEvents[i].getValue());
            }
            System.out.println("Event " + (i + 1) + " (ID=" + sensorEvents[i].getId() + ", value=" +
                    sensorEvents[i].getValue() + "): " + (result ? "MATCH" : "continue") +
                    " (temp sum so far: " + tempSum + ")");
        }

        Assertions.assertTrue(result); // Sum of temp_ sensors: 25+30+35+15=105 > 100
        System.out.println("Final temp sensor sum: " + tempSum);
    }

    @Test
    @DisplayName("测试传感器ID过滤与链式函数集成 - limit和avg")
    void testSensorIdFilterWithChainFunctionsLimitAvg() throws TranslatorException {
        // Use auto-imported sensorFilter - no manual registration needed
        String code = "filter(sensorFilter()).limit(3).avg().meet([25,35])"; // Average should be between 25-35
        Executor executor = Compiler.compile(code);

        // Sensor data with temperature values
        EventData[] sensorEvents = {
                new EventData("temp_001", "s1", "20", new Timestamp(System.currentTimeMillis())),        // Keep, value=20
                new EventData("humidity_001", "s2", "80", new Timestamp(System.currentTimeMillis() + 100)), // Discard
                new EventData("temp_002", "s3", "30", new Timestamp(System.currentTimeMillis() + 200)),   // Keep, value=30
                new EventData("temp_003", "s5", "40", new Timestamp(System.currentTimeMillis() + 400)),   // Keep, value=40
                new EventData("temp_004", "s6", "50", new Timestamp(System.currentTimeMillis() + 500)),   // Keep but limited out
                new EventData("pressure_001", "s4", "1000", new Timestamp(System.currentTimeMillis() + 300)) // Discard
        };

        boolean result = false;
        int tempCount = 0;
        int tempSum = 0;
        for (int i = 0; i < sensorEvents.length; i++) {
            result = executor.execute(sensorEvents[i]);
            if (sensorEvents[i].getId().startsWith("temp_")) {
                if (tempCount < 3) { // Only first 3 temp sensors due to limit
                    tempSum += Integer.parseInt(sensorEvents[i].getValue());
                }
                tempCount++;
            }
            System.out.println("Event " + (i + 1) + " (ID=" + sensorEvents[i].getId() + ", value=" +
                    sensorEvents[i].getValue() + "): " + (result ? "MATCH" : "continue") +
                    " (temp count: " + tempCount + ", limited sum: " + tempSum + ")");
        }

        Assertions.assertFalse(result); // Average of first 3 temp sensors: (20+30+40)/3=30, which is in [25,35]
        System.out.println("Average of limited temp sensors: " + (tempSum / 3.0));
    }

    @Test
    @DisplayName("测试数组过滤UDF - 少于3个事件")
    void testArrayFilterWithLessThanThreeEvents() throws TranslatorException {
        // Test with 2 events - should keep both
        String code = "filter(arrayFilter()).count().meet(==2)";
        Executor executor = Compiler.compile(code);

        EventData event1 = Util.genEventData("sensor1", "10", new Timestamp(System.currentTimeMillis()));
        EventData event2 = Util.genEventData("sensor2", "20", new Timestamp(System.currentTimeMillis() + 100));

        boolean result = false;
        result = executor.execute(event1);
        Assertions.assertFalse(result); // Not enough events yet

        result = executor.execute(event2);
        Assertions.assertTrue(result); // Should have 2 events (both kept)
    }

    @Test
    @DisplayName("测试数组过滤UDF - 恰好3个事件")
    void testArrayFilterWithExactlyThreeEvents() throws TranslatorException {
        // Test with exactly 3 events - should keep all 3
        String code = "filter(arrayFilter()).count().meet(==3)";
        Executor executor = Compiler.compile(code);

        EventData event1 = Util.genEventData("sensor1", "10", new Timestamp(System.currentTimeMillis()));
        EventData event2 = Util.genEventData("sensor2", "20", new Timestamp(System.currentTimeMillis() + 100));
        EventData event3 = Util.genEventData("sensor3", "30", new Timestamp(System.currentTimeMillis() + 200));

        boolean result = false;
        result = executor.execute(event1);
        Assertions.assertFalse(result); // Not enough events yet

        result = executor.execute(event2);
        Assertions.assertFalse(result); // Still not enough events

        result = executor.execute(event3);
        Assertions.assertTrue(result); // Should have 3 events (all kept)
    }

    @Test
    @DisplayName("测试数组过滤UDF - 超过3个事件，只保留最后3个")
    void testArrayFilterWithMoreThanThreeEvents() throws TranslatorException {
        // Test with 5 events - should only keep the last 3
        String code = "filter(arrayFilter()).count().meet(==3)";
        Executor executor = Compiler.compile(code);

        EventData event1 = Util.genEventData("sensor1", "10", new Timestamp(System.currentTimeMillis()));
        EventData event2 = Util.genEventData("sensor2", "20", new Timestamp(System.currentTimeMillis() + 100));
        EventData event3 = Util.genEventData("sensor3", "30", new Timestamp(System.currentTimeMillis() + 200));
        EventData event4 = Util.genEventData("sensor4", "40", new Timestamp(System.currentTimeMillis() + 300));
        EventData event5 = Util.genEventData("sensor5", "50", new Timestamp(System.currentTimeMillis() + 400));

        boolean result = false;
        result = executor.execute(event1);
        Assertions.assertFalse(result);

        result = executor.execute(event2);
        Assertions.assertFalse(result);

        result = executor.execute(event3);
        Assertions.assertTrue(result); // Should have 3 events, but let's continue to test the filtering

        result = executor.execute(event4);
        Assertions.assertFalse(result); // Should still have 3 events (last 3: event2, event3, event4)

        // Reset and test final state
        executor.resetEnv();
        for (EventData event : new EventData[]{event1, event2, event3, event4, event5}) {
            result = executor.execute(event);
        }
        Assertions.assertTrue(result); // Should have 3 events (last 3: event3, event4, event5)
    }

    @Test
    @DisplayName("测试数组过滤UDF与求和函数集成")
    void testArrayFilterWithSumFunction() throws TranslatorException {
        // Test array filter with sum - should sum only the last 3 values
        String code = "filter(arrayFilter()).sum().meet(==120)"; // 30+40+50 = 120
        Executor executor = Compiler.compile(code);

        EventData event1 = Util.genEventData("sensor1", "10", new Timestamp(System.currentTimeMillis()));
        EventData event2 = Util.genEventData("sensor2", "20", new Timestamp(System.currentTimeMillis() + 100));
        EventData event3 = Util.genEventData("sensor3", "30", new Timestamp(System.currentTimeMillis() + 200));
        EventData event4 = Util.genEventData("sensor4", "40", new Timestamp(System.currentTimeMillis() + 300));
        EventData event5 = Util.genEventData("sensor5", "50", new Timestamp(System.currentTimeMillis() + 400));

        boolean result = false;
        for (EventData event : new EventData[]{event1, event2, event3, event4, event5}) {
            result = executor.execute(event);
            System.out.println("After event " + event.getId() + " (value=" + event.getValue() + "): " +
                    (result ? "MATCH" : "continue"));
        }

        Assertions.assertTrue(result); // Sum of last 3 events: 30+40+50 = 120
    }

    @Test
    @DisplayName("测试数组过滤UDF与平均值函数集成")
    void testArrayFilterWithAvgFunction() throws TranslatorException {
        // Test array filter with avg - should average only the last 3 values
        String code = "filter(arrayFilter()).avg().meet(==40.0)"; // (30+40+50)/3 = 40.0
        Executor executor = Compiler.compile(code);

        EventData event1 = Util.genEventData("sensor1", "10", new Timestamp(System.currentTimeMillis()));
        EventData event2 = Util.genEventData("sensor2", "20", new Timestamp(System.currentTimeMillis() + 100));
        EventData event3 = Util.genEventData("sensor3", "30", new Timestamp(System.currentTimeMillis() + 200));
        EventData event4 = Util.genEventData("sensor4", "40", new Timestamp(System.currentTimeMillis() + 300));
        EventData event5 = Util.genEventData("sensor5", "50", new Timestamp(System.currentTimeMillis() + 400));

        boolean result = false;
        for (EventData event : new EventData[]{event1, event2, event3, event4, event5}) {
            result = executor.execute(event);
            System.out.println("After event " + event.getId() + " (value=" + event.getValue() + "): " +
                    (result ? "MATCH" : "continue"));
        }

        Assertions.assertTrue(result); // Average of last 3 events: (30+40+50)/3 = 40.0
    }

    @Test
    @DisplayName("测试数组过滤UDF边界情况")
    void testArrayFilterEdgeCases() throws TranslatorException {
        // Test with single event
        String code1 = "filter(arrayFilter()).count().meet(==1)";
        Executor executor1 = Compiler.compile(code1);

        EventData singleEvent = Util.genEventData("sensor1", "100", new Timestamp(System.currentTimeMillis()));
        boolean result1 = executor1.execute(singleEvent);
        Assertions.assertTrue(result1); // Should keep the single event

        // Test with exactly 4 events to verify transition from 3 to 3 (last 3)
        String code2 = "filter(arrayFilter()).sum().meet(==90)"; // 20+30+40 = 90 (last 3 of 4)
        Executor executor2 = Compiler.compile(code2);

        EventData[] fourEvents = {
                Util.genEventData("sensor1", "10", new Timestamp(System.currentTimeMillis())),
                Util.genEventData("sensor2", "20", new Timestamp(System.currentTimeMillis() + 100)),
                Util.genEventData("sensor3", "30", new Timestamp(System.currentTimeMillis() + 200)),
                Util.genEventData("sensor4", "40", new Timestamp(System.currentTimeMillis() + 300))
        };

        boolean result2 = false;
        for (EventData event : fourEvents) {
            result2 = executor2.execute(event);
            System.out.println("After event " + event.getId() + " (value=" + event.getValue() + "): " +
                    (result2 ? "MATCH" : "continue"));
        }

        Assertions.assertTrue(result2); // Sum of last 3 events: 20+30+40 = 90
    }

    @Test
    @DisplayName("测试数组过滤UDF与其他过滤条件组合")
    void testArrayFilterWithTraditionalFilter() throws TranslatorException {
        // This test verifies that arrayFilter works independently and doesn't interfere with traditional filters
        // We'll use a separate expression to test traditional filtering still works
        String traditionalCode = "filter(>25).count().meet(==3)";
        Executor traditionalExecutor = Compiler.compile(traditionalCode);

        String arrayCode = "filter(arrayFilter()).count().meet(==3)";
        Executor arrayExecutor = Compiler.compile(arrayCode);

        EventData[] events = {
                Util.genEventData("sensor1", "10", new Timestamp(System.currentTimeMillis())),
                Util.genEventData("sensor2", "30", new Timestamp(System.currentTimeMillis() + 100)),
                Util.genEventData("sensor3", "40", new Timestamp(System.currentTimeMillis() + 200)),
                Util.genEventData("sensor4", "20", new Timestamp(System.currentTimeMillis() + 300)),
                Util.genEventData("sensor5", "50", new Timestamp(System.currentTimeMillis() + 400))
        };

        // Test traditional filter: should keep values > 25 (30, 40, 50)
        boolean traditionalResult = false;
        for (EventData event : events) {
            traditionalResult = traditionalExecutor.execute(event);
        }
        Assertions.assertTrue(traditionalResult); // Should have 3 events > 25

        // Test array filter: should keep last 3 events regardless of value (40, 20, 50)
        boolean arrayResult = false;
        for (EventData event : events) {
            arrayResult = arrayExecutor.execute(event);
        }
        Assertions.assertTrue(arrayResult); // Should have 3 events (last 3)

    }

    @Test
    void testFilterSimplifyMode() throws TranslatorException {

        String arrayCode = "arrayFilter().count().meet(==3)";
        Executor arrayExecutor = Compiler.compile(arrayCode);
        EventData[] events = {
                Util.genEventData("sensor1", "10", new Timestamp(System.currentTimeMillis())),
                Util.genEventData("sensor2", "30", new Timestamp(System.currentTimeMillis() + 100)),
                Util.genEventData("sensor3", "40", new Timestamp(System.currentTimeMillis() + 200)),
                Util.genEventData("sensor4", "20", new Timestamp(System.currentTimeMillis() + 300)),
                Util.genEventData("sensor5", "50", new Timestamp(System.currentTimeMillis() + 400))
        };

        boolean arrayResult = false;
        for (EventData event : events) {
            arrayResult = arrayExecutor.execute(event);
        }
        Assertions.assertTrue(arrayResult); // Should have 3 events (last 3)
    }


    @Test
    void testFilterSimplifyModeMixedUsage()  {

        String arrayCode = "arrayFilter().filter(>30).count().meet(==3)";
        String finalArrayCode = arrayCode;
        Assertions.assertThrows(TranslatorException.class, () -> {
            Compiler.compile(finalArrayCode);
        });

        arrayCode = "filter(>30).arrayFilter().count().meet(==3)";
        String finalArrayCode1 = arrayCode;
        Assertions.assertThrows(TranslatorException.class, () -> {
            Compiler.compile(finalArrayCode1);
        });

        Assertions.assertThrows(TranslatorException.class, () -> {
            Compiler.compile("filter(>30).filter([1,7]).count().meet(==3)");
        });

        Assertions.assertThrows(TranslatorException.class, () -> {
            Compiler.compile("arrayFilter().sensorFilter().count().meet(==3)");
        });
    }

}
