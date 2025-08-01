package com.df.rhythmix.udf;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.Compiler;
import com.df.rhythmix.execute.Executor;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.util.EventData;
import com.df.rhythmix.util.EventValueType;
import com.df.rhythmix.util.Util;
import com.googlecode.aviator.annotation.Import;
import com.googlecode.aviator.annotation.ImportScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Test cases for Filter UDF functionality
 */
class FilterUDFTest {

    /**
     * Simple temperature filter UDF for testing
     */
    static public class TemperatureFilterUDF implements FilterUDF {
        @Override
        public String getName() {
            return "tempFilter2";
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


    @BeforeAll
    static void beforeAll() {
        FilterUDFRegistry.autoImportFilterUDFs();
    }

    @Test
    @DisplayName("测试简单的温度过滤UDF")
    void testSimpleTemperatureFilterUDF() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);

        // Compile expression with filter UDF
        String code = "filter(tempFilter2()).count().meet(==2)";
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
        TemplateEngine.enableDebugModel(true);


        // Compile expression with filter UDF
        String code = "filter(sensorFilter()).count().meet(==1)";
        Executor executor = Compiler.compile(code);

        // Test data - should only keep sensors with ID starting with "temp_"
        EventData event1 = new EventData("temp_001", "sensor1", "25.5", new Timestamp(System.currentTimeMillis()), EventValueType.FLOAT);
        EventData event2 = new EventData("humidity_001", "sensor2", "60.0", new Timestamp(System.currentTimeMillis() + 100), EventValueType.FLOAT);

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
        TemplateEngine.enableDebugModel(true);

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
        TemplateEngine.enableDebugModel(true);

        // Create regular UDF environment
        HashMap<String, Object> udfEnv = new HashMap<>();
        udfEnv.put("threshold", 50.0);
        // Compile expression with both regular UDF and filter UDF
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
}
