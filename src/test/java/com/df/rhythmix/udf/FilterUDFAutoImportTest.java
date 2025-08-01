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
        String code = "filter(tempFilter()).count().meet(==2)";
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
        String code = "filter(numericFilter()).count().meet(==2)";
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
        String code = "filter(positiveFilter()).sum().meet(>50)";
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

}
