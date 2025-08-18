package com.df.rhythmix.udf;

import com.df.rhythmix.util.EventData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test class for generic UDFRegistry
 */
public class UDFRegistryTest {

    // Test implementation of FilterUDF
    public static class TestFilterUDF implements FilterUDF {
        @Override
        public String getName() {
            return "testFilter";
        }

        @Override
        public boolean filter(EventData data) {
            return true;
        }

        @Override
        public List<EventData> filter(List<EventData> dataList) {
            return dataList;
        }
    }

    // Test implementation of CalculatorUDF
    public static class TestCalculatorUDF implements CalculatorUDF {
        @Override
        public String getName() {
            return "testCalc";
        }

        @Override
        public Number calculate(List<EventData> values) {
            return 1.0;
        }
    }

    private UDFRegistry<FilterUDF> filterRegistry;
    private UDFRegistry<CalculatorUDF> calculatorRegistry;

    @BeforeEach
    void setUp() {
        filterRegistry = new UDFRegistry<>(FilterUDF.class, "TestFilterUDF");
        calculatorRegistry = new UDFRegistry<>(CalculatorUDF.class, "TestCalculatorUDF");
    }

    @AfterEach
    void tearDown() {
        filterRegistry.clear();
        calculatorRegistry.clear();
    }

    @Test
    void testGenericRegistryCreation() {
        assertNotNull(filterRegistry);
        assertNotNull(calculatorRegistry);
        assertEquals(0, filterRegistry.getRegisteredCount());
        assertEquals(0, calculatorRegistry.getRegisteredCount());
        assertFalse(filterRegistry.isAutoImportCompleted());
        assertFalse(calculatorRegistry.isAutoImportCompleted());
    }

    @Test
    void testManualRegistration() {
        TestFilterUDF filterUDF = new TestFilterUDF();
        TestCalculatorUDF calculatorUDF = new TestCalculatorUDF();

        // Test FilterUDF registration
        assertTrue(filterRegistry.registerUDF(filterUDF));
        assertEquals(1, filterRegistry.getRegisteredCount());
        assertTrue(filterRegistry.isRegistered("testFilter"));
        assertEquals(filterUDF, filterRegistry.getUDF("testFilter"));

        // Test CalculatorUDF registration
        assertTrue(calculatorRegistry.registerUDF(calculatorUDF));
        assertEquals(1, calculatorRegistry.getRegisteredCount());
        assertTrue(calculatorRegistry.isRegistered("testCalc"));
        assertEquals(calculatorUDF, calculatorRegistry.getUDF("testCalc"));
    }

    @Test
    void testNullRegistration() {
        assertFalse(filterRegistry.registerUDF(null));
        assertFalse(calculatorRegistry.registerUDF(null));
        assertEquals(0, filterRegistry.getRegisteredCount());
        assertEquals(0, calculatorRegistry.getRegisteredCount());
    }

    @Test
    void testDuplicateRegistration() {
        TestFilterUDF filterUDF1 = new TestFilterUDF();
        TestFilterUDF filterUDF2 = new TestFilterUDF(); // Same name

        assertTrue(filterRegistry.registerUDF(filterUDF1));
        assertFalse(filterRegistry.registerUDF(filterUDF2)); // Should fail

        assertEquals(1, filterRegistry.getRegisteredCount());
        assertEquals(filterUDF1, filterRegistry.getUDF("testFilter"));
    }

    @Test
    void testGetUDF() {
        TestFilterUDF filterUDF = new TestFilterUDF();
        filterRegistry.registerUDF(filterUDF);

        assertEquals(filterUDF, filterRegistry.getUDF("testFilter"));
        assertNull(filterRegistry.getUDF("nonExistent"));
    }

    @Test
    void testIsRegistered() {
        TestFilterUDF filterUDF = new TestFilterUDF();

        assertFalse(filterRegistry.isRegistered("testFilter"));
        filterRegistry.registerUDF(filterUDF);
        assertTrue(filterRegistry.isRegistered("testFilter"));
        assertFalse(filterRegistry.isRegistered("nonExistent"));
    }

    @Test
    void testGetRegisteredNames() {
        TestFilterUDF filterUDF = new TestFilterUDF();
        TestCalculatorUDF calculatorUDF = new TestCalculatorUDF();

        assertTrue(filterRegistry.getRegisteredNames().isEmpty());
        assertTrue(calculatorRegistry.getRegisteredNames().isEmpty());

        filterRegistry.registerUDF(filterUDF);
        calculatorRegistry.registerUDF(calculatorUDF);

        Set<String> filterNames = filterRegistry.getRegisteredNames();
        Set<String> calculatorNames = calculatorRegistry.getRegisteredNames();

        assertEquals(1, filterNames.size());
        assertEquals(1, calculatorNames.size());
        assertTrue(filterNames.contains("testFilter"));
        assertTrue(calculatorNames.contains("testCalc"));
    }

    @Test
    void testGetRegisteredCount() {
        assertEquals(0, filterRegistry.getRegisteredCount());
        assertEquals(0, calculatorRegistry.getRegisteredCount());

        filterRegistry.registerUDF(new TestFilterUDF());
        calculatorRegistry.registerUDF(new TestCalculatorUDF());

        assertEquals(1, filterRegistry.getRegisteredCount());
        assertEquals(1, calculatorRegistry.getRegisteredCount());
    }

    @Test
    void testClear() {
        filterRegistry.registerUDF(new TestFilterUDF());
        calculatorRegistry.registerUDF(new TestCalculatorUDF());

        assertEquals(1, filterRegistry.getRegisteredCount());
        assertEquals(1, calculatorRegistry.getRegisteredCount());

        filterRegistry.clear();
        calculatorRegistry.clear();

        assertEquals(0, filterRegistry.getRegisteredCount());
        assertEquals(0, calculatorRegistry.getRegisteredCount());
        assertFalse(filterRegistry.isAutoImportCompleted());
        assertFalse(calculatorRegistry.isAutoImportCompleted());
    }

    @Test
    void testGetRegisteredUDFs() {
        TestFilterUDF filterUDF = new TestFilterUDF();
        filterRegistry.registerUDF(filterUDF);

        assertEquals(1, filterRegistry.getRegisteredUDFs().size());
        assertEquals(filterUDF, filterRegistry.getRegisteredUDFs().get("testFilter"));
    }

    @Test
    void testAutoImportCompleted() {
        assertFalse(filterRegistry.isAutoImportCompleted());
        filterRegistry.autoImportUDFs();
        assertTrue(filterRegistry.isAutoImportCompleted());
    }

    @Test
    void testAutoImportIdempotent() {
        filterRegistry.autoImportUDFs();
        int firstCount = filterRegistry.getRegisteredCount();

        filterRegistry.autoImportUDFs();
        int secondCount = filterRegistry.getRegisteredCount();

        assertEquals(firstCount, secondCount);
        assertTrue(filterRegistry.isAutoImportCompleted());
    }

    @Test
    void testThreadSafetyAutoImport() throws InterruptedException {
        final int threadCount = 5;
        final CountDownLatch latch = new CountDownLatch(threadCount);

        // Create multiple threads that call autoImportUDFs
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    filterRegistry.autoImportUDFs();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // Wait for all threads to complete
        assertTrue(latch.await(10, TimeUnit.SECONDS));

        // Auto-import should have completed exactly once
        assertTrue(filterRegistry.isAutoImportCompleted());
    }


    @Test
    void testRegistryIsolation() {
        // Test that different registry instances are isolated
        TestFilterUDF filterUDF = new TestFilterUDF();
        TestCalculatorUDF calculatorUDF = new TestCalculatorUDF();

        filterRegistry.registerUDF(filterUDF);
        calculatorRegistry.registerUDF(calculatorUDF);

        // Each registry should only contain its own type
        assertEquals(1, filterRegistry.getRegisteredCount());
        assertEquals(1, calculatorRegistry.getRegisteredCount());

        assertTrue(filterRegistry.isRegistered("testFilter"));
        assertFalse(filterRegistry.isRegistered("testCalc"));

        assertTrue(calculatorRegistry.isRegistered("testCalc"));
        assertFalse(calculatorRegistry.isRegistered("testFilter"));
    }
}
