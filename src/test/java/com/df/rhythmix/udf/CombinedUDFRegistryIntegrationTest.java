package com.df.rhythmix.udf;

import com.df.rhythmix.lib.Register;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

/**
 * Integration test for combined UDF registry functionality
 * Tests that both FilterUDF and CalculatorUDF registries work together
 */
public class CombinedUDFRegistryIntegrationTest {

    @BeforeEach
    void setUp() {
        // Clear both registries before each test
        FilterUDFRegistry.clear();
        CalculatorUDFRegistry.clear();
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        FilterUDFRegistry.clear();
        CalculatorUDFRegistry.clear();
    }

    @Test
    @DisplayName("Test that both registries work independently")
    void testIndependentRegistries() {
        // Verify initial state
        assertEquals(0, FilterUDFRegistry.getRegisteredCount());
        assertEquals(0, CalculatorUDFRegistry.getRegisteredCount());
        assertFalse(FilterUDFRegistry.isAutoImportCompleted());
        assertFalse(CalculatorUDFRegistry.isAutoImportCompleted());

        // Auto-import FilterUDFs only
        FilterUDFRegistry.autoImportFilterUDFs();
        
        assertTrue(FilterUDFRegistry.isAutoImportCompleted());
        assertFalse(CalculatorUDFRegistry.isAutoImportCompleted());

        // Auto-import CalculatorUDFs only
        CalculatorUDFRegistry.autoImportCalculatorUDFs();
        
        assertTrue(FilterUDFRegistry.isAutoImportCompleted());
        assertTrue(CalculatorUDFRegistry.isAutoImportCompleted());
    }

    @Test
    @DisplayName("Test auto-registration through Register.importFunction()")
    void testAutoRegistrationThroughRegister() {
        // Verify registries are initially empty
        assertEquals(0, FilterUDFRegistry.getRegisteredCount());
        assertEquals(0, CalculatorUDFRegistry.getRegisteredCount());

        // Call Register.importFunction() which should trigger both auto-registrations
        Register.importFunction();

        // Verify both auto-imports were completed
        assertTrue(FilterUDFRegistry.isAutoImportCompleted());
        assertTrue(CalculatorUDFRegistry.isAutoImportCompleted());

        // Verify built-in UDFs were registered
        Set<String> filterNames = FilterUDFRegistry.getRegisteredNames();
        Set<String> calculatorNames = CalculatorUDFRegistry.getRegisteredNames();

        // Check for built-in filter UDFs
        assertTrue(filterNames.contains("numericFilter"), "numericFilter should be auto-registered");
        assertTrue(filterNames.contains("positiveFilter"), "positiveFilter should be auto-registered");

        // Check for built-in calculator UDFs

        // Verify we can retrieve the registered UDFs
        assertNotNull(FilterUDFRegistry.getFilterUDF("numericFilter"));
        assertNotNull(FilterUDFRegistry.getFilterUDF("positiveFilter"));

    }

    @Test
    @DisplayName("Test registry isolation - no cross-contamination")
    void testRegistryIsolation() {
        // Auto-import both types
        Register.importFunction();

        Set<String> filterNames = FilterUDFRegistry.getRegisteredNames();
        Set<String> calculatorNames = CalculatorUDFRegistry.getRegisteredNames();

        // Verify no overlap between registries
        for (String filterName : filterNames) {
            assertFalse(CalculatorUDFRegistry.isRegistered(filterName), 
                       "FilterUDF '" + filterName + "' should not be in CalculatorUDF registry");
        }

        for (String calculatorName : calculatorNames) {
            assertFalse(FilterUDFRegistry.isRegistered(calculatorName), 
                       "CalculatorUDF '" + calculatorName + "' should not be in FilterUDF registry");
        }
    }


    @Test
    @DisplayName("Test idempotent auto-import for both registries")
    void testIdempotentAutoImport() {
        // First auto-import
        Register.importFunction();
        
        int firstFilterCount = FilterUDFRegistry.getRegisteredCount();
        int firstCalculatorCount = CalculatorUDFRegistry.getRegisteredCount();
        Set<String> firstFilterNames = FilterUDFRegistry.getRegisteredNames();
        Set<String> firstCalculatorNames = CalculatorUDFRegistry.getRegisteredNames();

        // Second auto-import should not change anything
        Register.importFunction();
        
        int secondFilterCount = FilterUDFRegistry.getRegisteredCount();
        int secondCalculatorCount = CalculatorUDFRegistry.getRegisteredCount();
        Set<String> secondFilterNames = FilterUDFRegistry.getRegisteredNames();
        Set<String> secondCalculatorNames = CalculatorUDFRegistry.getRegisteredNames();

        // Verify counts and names are the same
        assertEquals(firstFilterCount, secondFilterCount);
        assertEquals(firstCalculatorCount, secondCalculatorCount);
        assertEquals(firstFilterNames, secondFilterNames);
        assertEquals(firstCalculatorNames, secondCalculatorNames);

        assertTrue(FilterUDFRegistry.isAutoImportCompleted());
        assertTrue(CalculatorUDFRegistry.isAutoImportCompleted());
    }

    @Test
    @DisplayName("Test registry consistency after operations")
    void testRegistryConsistency() {
        // Auto-import
        Register.importFunction();

        // Test FilterUDF registry consistency
        Set<String> filterNames = FilterUDFRegistry.getRegisteredNames();
        int filterCount = FilterUDFRegistry.getRegisteredCount();
        
        assertEquals(filterNames.size(), filterCount, "FilterUDF names count should match registered count");

        for (String name : filterNames) {
            assertTrue(FilterUDFRegistry.isRegistered(name));
            assertNotNull(FilterUDFRegistry.getFilterUDF(name));
        }

        assertEquals(filterCount, FilterUDFRegistry.getRegisteredUdfs().size());
        assertEquals(filterNames, FilterUDFRegistry.getRegisteredUdfs().keySet());

        // Test CalculatorUDF registry consistency
        Set<String> calculatorNames = CalculatorUDFRegistry.getRegisteredNames();
        int calculatorCount = CalculatorUDFRegistry.getRegisteredCount();
        
        assertEquals(calculatorNames.size(), calculatorCount, "CalculatorUDF names count should match registered count");

        for (String name : calculatorNames) {
            assertTrue(CalculatorUDFRegistry.isRegistered(name));
            assertNotNull(CalculatorUDFRegistry.getCalculatorUDF(name));
        }

        assertEquals(calculatorCount, CalculatorUDFRegistry.getRegisteredUdfs().size());
        assertEquals(calculatorNames, CalculatorUDFRegistry.getRegisteredUdfs().keySet());
    }

    @Test
    @DisplayName("Test clear functionality for both registries")
    void testClearFunctionality() {
        // Auto-import and verify UDFs are registered
        Register.importFunction();
        
        assertTrue(FilterUDFRegistry.getRegisteredCount() > 0);
        assertTrue(CalculatorUDFRegistry.getRegisteredCount() > 0);
        assertTrue(FilterUDFRegistry.isAutoImportCompleted());
        assertTrue(CalculatorUDFRegistry.isAutoImportCompleted());

        // Clear both registries
        FilterUDFRegistry.clear();
        CalculatorUDFRegistry.clear();

        // Verify both are cleared
        assertEquals(0, FilterUDFRegistry.getRegisteredCount());
        assertEquals(0, CalculatorUDFRegistry.getRegisteredCount());
        assertFalse(FilterUDFRegistry.isAutoImportCompleted());
        assertFalse(CalculatorUDFRegistry.isAutoImportCompleted());
    }
}
