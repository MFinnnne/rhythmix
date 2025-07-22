package com.df.rhythmix.udf;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.Compiler;
import com.df.rhythmix.execute.Executor;
import com.df.rhythmix.lib.Register;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.util.EventData;
import com.df.rhythmix.util.EventValueType;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.*;

import java.sql.Timestamp;

/**
 * Test cases for FilterUDF auto-import functionality
 */
class FilterUDFAutoImportTest {

    @BeforeAll
    static void setUp() {
        // Enable debug mode
        TemplateEngine.enableDebugModel(true);
        
        // Trigger auto-import by calling Register.importFunction()
        Register.importFunction();
    }

    @Test
    @DisplayName("测试FilterUDF自动导入功能")
    void testAutoImportFunctionality() {
        // Verify that auto-import has been completed
        Assertions.assertTrue(FilterUDFRegistry.isAutoImportCompleted(), 
                            "Auto-import should be completed");
        
        // Verify that built-in FilterUDFs are registered
        Assertions.assertTrue(FilterUDFRegistry.isRegistered("tempFilter"), 
                            "tempFilter should be auto-registered");
        Assertions.assertTrue(FilterUDFRegistry.isRegistered("numericFilter"), 
                            "numericFilter should be auto-registered");
        Assertions.assertTrue(FilterUDFRegistry.isRegistered("positiveFilter"), 
                            "positiveFilter should be auto-registered");
        
        // Check registration count
        int registeredCount = FilterUDFRegistry.getRegisteredCount();
        Assertions.assertTrue(registeredCount >= 3, 
                            "At least 3 FilterUDFs should be registered, found: " + registeredCount);
        
        System.out.println("Auto-imported FilterUDFs: " + FilterUDFRegistry.getRegisteredNames());
    }

    @Test
    @DisplayName("测试自动导入的tempFilter UDF")
    void testAutoImportedTempFilter() throws TranslatorException {
        // Use auto-imported tempFilter without manual registration
        String code = "filter(tempFilter()).collect().count().meet(==2)";
        Executor executor = Compiler.compile(code);
        
        // Test data - tempFilter should keep temperatures between -50 and 100
        EventData event1 = Util.genEventData("sensor1", "25.5", new Timestamp(System.currentTimeMillis()));      // Keep
        EventData event2 = Util.genEventData("sensor2", "-60.0", new Timestamp(System.currentTimeMillis() + 100)); // Discard (< -50)
        EventData event3 = Util.genEventData("sensor3", "75.0", new Timestamp(System.currentTimeMillis() + 200));  // Keep
        EventData event4 = Util.genEventData("sensor4", "150.0", new Timestamp(System.currentTimeMillis() + 300)); // Discard (> 100)
        
        boolean result = false;
        result = executor.execute(event1); // Keep
        Assertions.assertFalse(result); // Not enough events yet
        
        result = executor.execute(event2); // Discard
        Assertions.assertFalse(result); // Still not enough events
        
        result = executor.execute(event3); // Keep
        Assertions.assertTrue(result); // Should have 2 valid temperatures now
    }

    @Test
    @DisplayName("测试自动导入的numericFilter UDF")
    void testAutoImportedNumericFilter() throws TranslatorException {
        // Use auto-imported numericFilter without manual registration
        String code = "filter(numericFilter()).collect().count().meet(==2)";
        Executor executor = Compiler.compile(code);
        
        // Test data - numericFilter should keep only numeric values
        EventData event1 = Util.genEventData("sensor1", "25.5", new Timestamp(System.currentTimeMillis()));      // Keep (numeric)
        EventData event2 = Util.genEventData("sensor2", "invalid", new Timestamp(System.currentTimeMillis() + 100)); // Discard (non-numeric)
        EventData event3 = Util.genEventData("sensor3", "42", new Timestamp(System.currentTimeMillis() + 200));    // Keep (numeric)
        EventData event4 = Util.genEventData("sensor4", "error", new Timestamp(System.currentTimeMillis() + 300));  // Discard (non-numeric)
        
        boolean result = false;
        result = executor.execute(event1); // Keep
        Assertions.assertFalse(result); // Not enough events yet
        
        result = executor.execute(event2); // Discard
        Assertions.assertFalse(result); // Still not enough events
        
        result = executor.execute(event3); // Keep
        Assertions.assertTrue(result); // Should have 2 valid numeric values now
    }

    @Test
    @DisplayName("测试自动导入的positiveFilter UDF")
    void testAutoImportedPositiveFilter() throws TranslatorException {
        // Use auto-imported positiveFilter without manual registration
        String code = "filter(positiveFilter()).collect().sum().meet(>50)";
        Executor executor = Compiler.compile(code);
        
        // Test data - positiveFilter should keep only positive values
        EventData event1 = Util.genEventData("sensor1", "25", new Timestamp(System.currentTimeMillis()));      // Keep (positive)
        EventData event2 = Util.genEventData("sensor2", "-10", new Timestamp(System.currentTimeMillis() + 100)); // Discard (negative)
        EventData event3 = Util.genEventData("sensor3", "30", new Timestamp(System.currentTimeMillis() + 200));  // Keep (positive)
        EventData event4 = Util.genEventData("sensor4", "0", new Timestamp(System.currentTimeMillis() + 300));    // Discard (zero)
        
        boolean result = false;
        result = executor.execute(event1); // Keep (25)
        Assertions.assertFalse(result); // Sum not > 50 yet
        
        result = executor.execute(event2); // Discard (-10)
        Assertions.assertFalse(result); // Sum still not > 50
        
        result = executor.execute(event3); // Keep (30)
        Assertions.assertTrue(result); // Sum (25+30=55) > 50
    }

    @Test
    @DisplayName("测试自动导入与手动注册的兼容性")
    void testAutoImportWithManualRegistration() throws TranslatorException {
        // Create a custom FilterUDF for manual registration
        FilterUDF customFilter = new FilterUDF() {
            @Override
            public String getName() {
                return "customFilter";
            }

            @Override
            public boolean filter(EventData event) {
                try {
                    double value = Double.parseDouble(event.getValue());
                    return value >= 10 && value <= 50;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        };
        
        // Register manually
        boolean registered = FilterUDFRegistry.registerFilterUDF(customFilter);
        Assertions.assertTrue(registered, "Manual registration should succeed");
        
        // Use both auto-imported and manually registered filters
        String code = "filter(numericFilter()).filter(customFilter()).collect().count().meet(==1)";
        Executor executor = Compiler.compile(code);
        
        // Test data
        EventData event1 = Util.genEventData("sensor1", "25", new Timestamp(System.currentTimeMillis()));      // Keep (numeric and in range)
        EventData event2 = Util.genEventData("sensor2", "60", new Timestamp(System.currentTimeMillis() + 100)); // Discard (numeric but out of range)
        
        boolean result = false;
        result = executor.execute(event1); // Keep
        Assertions.assertTrue(result); // Should have 1 valid event
        
        // Reset for next test
        executor.resetEnv();
        result = executor.execute(event2); // Discard
        Assertions.assertFalse(result); // Should have 0 valid events
    }

    @Test
    @DisplayName("测试自动导入与传统过滤器的兼容性")
    void testAutoImportWithTraditionalFilters() throws TranslatorException {
        // Test that traditional comparison expressions still work alongside auto-imported UDFs
        String traditionalCode = "filter(>20).collect().count().meet(==2)";
        Executor traditionalExecutor = Compiler.compile(traditionalCode);
        
        EventData[] events = {
            Util.genEventData("sensor1", "25", new Timestamp(System.currentTimeMillis())),
            Util.genEventData("sensor2", "15", new Timestamp(System.currentTimeMillis() + 100)),
            Util.genEventData("sensor3", "30", new Timestamp(System.currentTimeMillis() + 200))
        };
        
        boolean result = false;
        for (int i = 0; i < events.length; i++) {
            result = traditionalExecutor.execute(events[i]);
        }
        Assertions.assertTrue(result, "Traditional filter should still work");
        
        // Test mixed usage
        String mixedCode = "filter(positiveFilter()).filter(>10).collect().count().meet(==2)";
        Executor mixedExecutor = Compiler.compile(mixedCode);
        
        mixedExecutor.resetEnv();
        for (int i = 0; i < events.length; i++) {
            result = mixedExecutor.execute(events[i]);
        }
        Assertions.assertTrue(result, "Mixed UDF and traditional filter should work");
    }

    @Test
    @DisplayName("测试FilterUDF注册信息")
    void testFilterUDFRegistrationInfo() {
        // Test registry information methods
        Assertions.assertTrue(FilterUDFRegistry.getRegisteredCount() > 0, 
                            "Should have registered FilterUDFs");
        
        Assertions.assertFalse(FilterUDFRegistry.getRegisteredNames().isEmpty(), 
                             "Should have registered FilterUDF names");
        
        // Test specific FilterUDF retrieval
        FilterUDF tempFilter = FilterUDFRegistry.getFilterUDF("tempFilter");
        Assertions.assertNotNull(tempFilter, "tempFilter should be retrievable");
        Assertions.assertEquals("tempFilter", tempFilter.getName(), 
                               "Retrieved FilterUDF should have correct name");
        
        // Test non-existent FilterUDF
        FilterUDF nonExistent = FilterUDFRegistry.getFilterUDF("nonExistentFilter");
        Assertions.assertNull(nonExistent, "Non-existent FilterUDF should return null");
    }
}
