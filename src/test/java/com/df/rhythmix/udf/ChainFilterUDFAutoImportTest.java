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
 * Test cases for FilterUDF auto-import functionality
 */
class ChainFilterUDFAutoImportTest {

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
        RhythmixEventData event1 = Util.genEventData("sensor1", "25.5", new Timestamp(System.currentTimeMillis()));      // Keep
        RhythmixEventData event2 = Util.genEventData("sensor2", "-60.0", new Timestamp(System.currentTimeMillis() + 100)); // Discard (< -50)
        RhythmixEventData event3 = Util.genEventData("sensor3", "75.0", new Timestamp(System.currentTimeMillis() + 200));  // Keep
        RhythmixEventData event4 = Util.genEventData("sensor4", "150.0", new Timestamp(System.currentTimeMillis() + 300)); // Discard (> 100)
        
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
        RhythmixEventData event1 = Util.genEventData("sensor1", "25.5", new Timestamp(System.currentTimeMillis()));      // Keep (numeric)
        RhythmixEventData event2 = Util.genEventData("sensor2", "invalid", new Timestamp(System.currentTimeMillis() + 100)); // Discard (non-numeric)
        RhythmixEventData event3 = Util.genEventData("sensor3", "42", new Timestamp(System.currentTimeMillis() + 200));    // Keep (numeric)
        RhythmixEventData event4 = Util.genEventData("sensor4", "error", new Timestamp(System.currentTimeMillis() + 300));  // Discard (non-numeric)
        
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
        RhythmixEventData event1 = Util.genEventData("sensor1", "25", new Timestamp(System.currentTimeMillis()));      // Keep (positive)
        RhythmixEventData event2 = Util.genEventData("sensor2", "-10", new Timestamp(System.currentTimeMillis() + 100)); // Discard (negative)
        RhythmixEventData event3 = Util.genEventData("sensor3", "30", new Timestamp(System.currentTimeMillis() + 200));  // Keep (positive)
        RhythmixEventData event4 = Util.genEventData("sensor4", "0", new Timestamp(System.currentTimeMillis() + 300));    // Discard (zero)
        
        boolean result = false;
        result = executor.execute(event1); // Keep (25)
        Assertions.assertFalse(result); // Sum not > 50 yet
        
        result = executor.execute(event2); // Discard (-10)
        Assertions.assertFalse(result); // Sum still not > 50
        
        result = executor.execute(event3); // Keep (30)
        Assertions.assertTrue(result); // Sum (25+30=55) > 50
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
        ChainFilterUDF tempFilter = FilterUDFRegistry.getFilterUDF("tempFilter");
        Assertions.assertNotNull(tempFilter, "tempFilter should be retrievable");
        Assertions.assertEquals("tempFilter", tempFilter.getName(), 
                               "Retrieved FilterUDF should have correct name");
        
        // Test non-existent FilterUDF
        ChainFilterUDF nonExistent = FilterUDFRegistry.getFilterUDF("nonExistentFilter");
        Assertions.assertNull(nonExistent, "Non-existent FilterUDF should return null");
    }
}
