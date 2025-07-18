package com.df.rhythmix.example;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.Compiler;
import com.df.rhythmix.execute.Executor;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.udf.FilterUDF;
import com.df.rhythmix.util.EventData;
import com.df.rhythmix.util.EventValueType;
import com.df.rhythmix.util.Util;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Example demonstrating Filter UDF functionality
 * 
 * This example shows how to:
 * 1. Create custom filter UDFs
 * 2. Register them with the compiler
 * 3. Use them in Rhythmix expressions
 * 4. Combine them with other chain functions
 */
public class FilterUDFExample {

    /**
     * Example temperature filter UDF
     * Keeps temperatures between 20-80 degrees
     */
    static class TemperatureFilterUDF implements FilterUDF {
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
     * Example sensor ID filter UDF
     * Keeps only sensors with IDs starting with "temp_"
     */
    static class SensorIdFilterUDF implements FilterUDF {
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
     * Example numeric value filter UDF
     * Keeps only numeric values (filters out non-numeric strings)
     */
    static class NumericFilterUDF implements FilterUDF {
        @Override
        public String getName() {
            return "numericFilter";
        }

        @Override
        public boolean filter(EventData event) {
            try {
                Double.parseDouble(event.getValue());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Enable debug mode to see filter operations
            TemplateEngine.enableDebugModel(true);
            
            System.out.println("=== Filter UDF Example ===\n");
            
            // Example 1: Simple temperature filter
            example1_SimpleTemperatureFilter();
            
            // Example 2: Sensor ID filter
            example2_SensorIdFilter();
            
            // Example 3: Combining UDF with traditional expressions
            example3_CombiningWithTraditionalExpressions();
            
            // Example 4: Multiple UDFs in complex pipeline
            example4_ComplexPipeline();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void example1_SimpleTemperatureFilter() throws TranslatorException {
        System.out.println("--- Example 1: Simple Temperature Filter ---");
        
        // Create filter UDF map
        Map<String, FilterUDF> filterUDFs = new HashMap<>();
        filterUDFs.put("tempFilter", new TemperatureFilterUDF());
        
        // Compile expression with filter UDF
        String code = "filter(tempFilter()).collect().count().meet(==2)";
        Executor executor = Compiler.compile(code, filterUDFs);
        
        System.out.println("Expression: " + code);
        System.out.println("Filter UDF: tempFilter (keeps temperatures 20-80°C)");
        
        // Test data
        EventData[] events = {
            Util.genEventData("sensor1", "25.5", new Timestamp(System.currentTimeMillis())),      // Keep
            Util.genEventData("sensor2", "15.0", new Timestamp(System.currentTimeMillis() + 100)), // Discard (< 20)
            Util.genEventData("sensor3", "75.0", new Timestamp(System.currentTimeMillis() + 200)), // Keep
            Util.genEventData("sensor4", "90.0", new Timestamp(System.currentTimeMillis() + 300))  // Discard (> 80)
        };
        
        boolean result = false;
        for (int i = 0; i < events.length; i++) {
            result = executor.execute(events[i]);
            System.out.println("Event " + (i+1) + " (value=" + events[i].getValue() + "): " + 
                             (result ? "MATCH" : "continue"));
        }
        System.out.println("Final result: " + result + " (should be true - 2 valid temperatures)\n");
    }

    private static void example2_SensorIdFilter() throws TranslatorException {
        System.out.println("--- Example 2: Sensor ID Filter ---");
        
        // Create filter UDF map
        Map<String, FilterUDF> filterUDFs = new HashMap<>();
        filterUDFs.put("sensorFilter", new SensorIdFilterUDF());
        
        // Compile expression with filter UDF
        String code = "filter(sensorFilter()).collect().count().meet(==1)";
        Executor executor = Compiler.compile(code, filterUDFs);
        
        System.out.println("Expression: " + code);
        System.out.println("Filter UDF: sensorFilter (keeps sensors with ID starting with 'temp_')");
        
        // Test data with specific sensor IDs
        EventData event1 = new EventData("temp_001", "sensor1", "25.5", 
                                        new Timestamp(System.currentTimeMillis()), EventValueType.FLOAT);
        EventData event2 = new EventData("humidity_001", "sensor2", "60.0", 
                                        new Timestamp(System.currentTimeMillis() + 100), EventValueType.FLOAT);
        
        boolean result1 = executor.execute(event1);
        System.out.println("Event 1 (ID=temp_001): " + (result1 ? "MATCH" : "continue"));
        
        executor.resetEnv(); // Reset for independent test
        boolean result2 = executor.execute(event2);
        System.out.println("Event 2 (ID=humidity_001): " + (result2 ? "MATCH" : "continue"));
        System.out.println();
    }

    private static void example3_CombiningWithTraditionalExpressions() throws TranslatorException {
        System.out.println("--- Example 3: UDF + Traditional Filter Compatibility ---");
        
        // Test that traditional comparison expressions still work
        String traditionalCode = "filter(>20).collect().count().meet(==2)";
        Executor traditionalExecutor = Compiler.compile(traditionalCode);
        
        System.out.println("Traditional Expression: " + traditionalCode);
        
        EventData[] events = {
            Util.genEventData("sensor1", "25", new Timestamp(System.currentTimeMillis())),
            Util.genEventData("sensor2", "15", new Timestamp(System.currentTimeMillis() + 100)),
            Util.genEventData("sensor3", "30", new Timestamp(System.currentTimeMillis() + 200))
        };
        
        boolean result = false;
        for (int i = 0; i < events.length; i++) {
            result = traditionalExecutor.execute(events[i]);
            System.out.println("Event " + (i+1) + " (value=" + events[i].getValue() + "): " + 
                             (result ? "MATCH" : "continue"));
        }
        System.out.println("Traditional filter still works: " + result + "\n");
    }

    private static void example4_ComplexPipeline() throws TranslatorException {
        System.out.println("--- Example 4: Complex Pipeline with Multiple UDFs ---");
        
        // Create regular UDF environment
        HashMap<String, Object> udfEnv = new HashMap<>();
        udfEnv.put("threshold", 50.0);
        
        // Create filter UDF map
        Map<String, FilterUDF> filterUDFs = new HashMap<>();
        filterUDFs.put("numericFilter", new NumericFilterUDF());
        
        // Complex expression combining filter UDF, chain functions, and regular UDF
        String code = "filter(numericFilter()).collect().sum().meet(>threshold)";
        Executor executor = Compiler.compile(code, udfEnv, filterUDFs);
        
        System.out.println("Expression: " + code);
        System.out.println("Filter UDF: numericFilter (keeps only numeric values)");
        System.out.println("Regular UDF: threshold = 50.0");
        
        // Test data with mixed numeric and non-numeric values
        EventData[] events = {
            Util.genEventData("sensor1", "25", new Timestamp(System.currentTimeMillis())),        // Keep (numeric)
            Util.genEventData("sensor2", "invalid", new Timestamp(System.currentTimeMillis() + 100)), // Discard (non-numeric)
            Util.genEventData("sensor3", "30", new Timestamp(System.currentTimeMillis() + 200)),   // Keep (numeric)
            Util.genEventData("sensor4", "error", new Timestamp(System.currentTimeMillis() + 300))  // Discard (non-numeric)
        };
        
        boolean result = false;
        for (int i = 0; i < events.length; i++) {
            result = executor.execute(events[i]);
            System.out.println("Event " + (i+1) + " (value=" + events[i].getValue() + "): " + 
                             (result ? "MATCH" : "continue"));
        }
        System.out.println("Final result: " + result + " (sum of numeric values: 25+30=55 > 50)\n");
    }
}
