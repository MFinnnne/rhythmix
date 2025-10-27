package io.github.mfinnnne.rhythmix.udf;

import io.github.mfinnnne.rhythmix.lib.Register;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Test class for PostProcessingUDFRegistry functionality
 */
public class ChainPostProcessingUDFRegistryTest {

    // Test implementation of PostProcessingUDF
    public static class TestChainPostProcessingUDF implements ChainPostProcessingUDF {
        @Override
        public String getName() {
            return "testClear";
        }

        @Override
        public boolean handle(List<RhythmixEventData> aggregatedData, Number calculationResult) {
            if (calculationResult != null) {
                return calculationResult.doubleValue() > 5.0;
            }
            return false;
        }
    }

    // Another test implementation
    public static class AnotherTestChainPostProcessingUDF implements ChainPostProcessingUDF {
        @Override
        public String getName() {
            return "anotherClear";
        }

        @Override
        public boolean handle(List<RhythmixEventData> aggregatedData, Number calculationResult) {
            if (calculationResult != null) {
                return aggregatedData.size() > 2 && calculationResult.doubleValue() < 100.0;
            }
            return false;
        }
    }

    // Test implementation with duplicate name
    public static class DuplicateNameChainPostProcessingUDF implements ChainPostProcessingUDF {
        @Override
        public String getName() {
            return "testClear";
        }

        @Override
        public boolean handle(List<RhythmixEventData> aggregatedData, Number calculationResult) {
            return false;
        }
    }

    @BeforeEach
    void setUp() {
        PostProcessingUDFRegistry.clear();
    }

    @AfterEach
    void tearDown() {
        PostProcessingUDFRegistry.clear();
    }

    @Test
    @DisplayName("Test manual registration")
    void testManualRegistration() {
        TestChainPostProcessingUDF testUDF = new TestChainPostProcessingUDF();
        
        assertTrue(PostProcessingUDFRegistry.registerPostProcessingUDF(testUDF));
        assertEquals(1, PostProcessingUDFRegistry.getRegisteredCount());
        assertTrue(PostProcessingUDFRegistry.isRegistered("testClear"));
        assertEquals(testUDF, PostProcessingUDFRegistry.getPostProcessingUDF("testClear"));
    }

    @Test
    @DisplayName("Test duplicate registration")
    void testDuplicateRegistration() {
        TestChainPostProcessingUDF testUDF1 = new TestChainPostProcessingUDF();
        DuplicateNameChainPostProcessingUDF testUDF2 = new DuplicateNameChainPostProcessingUDF();
        
        // First registration should succeed
        assertTrue(PostProcessingUDFRegistry.registerPostProcessingUDF(testUDF1));
        
        // Second registration with same name should fail
        assertFalse(PostProcessingUDFRegistry.registerPostProcessingUDF(testUDF2));
        
        assertEquals(1, PostProcessingUDFRegistry.getRegisteredCount());
        assertEquals(testUDF1, PostProcessingUDFRegistry.getPostProcessingUDF("testClear"));
    }

    @Test
    @DisplayName("Test null registration")
    void testNullRegistration() {
        assertFalse(PostProcessingUDFRegistry.registerPostProcessingUDF(null));
        assertEquals(0, PostProcessingUDFRegistry.getRegisteredCount());
    }

    @Test
    @DisplayName("Test getPostProcessingUDF")
    void testGetPostProcessingUDF() {
        TestChainPostProcessingUDF testUDF = new TestChainPostProcessingUDF();
        PostProcessingUDFRegistry.registerPostProcessingUDF(testUDF);
        
        assertEquals(testUDF, PostProcessingUDFRegistry.getPostProcessingUDF("testClear"));
        assertNull(PostProcessingUDFRegistry.getPostProcessingUDF("nonExistent"));
    }

    @Test
    @DisplayName("Test isRegistered")
    void testIsRegistered() {
        TestChainPostProcessingUDF testUDF = new TestChainPostProcessingUDF();
        
        assertFalse(PostProcessingUDFRegistry.isRegistered("testClear"));
        
        PostProcessingUDFRegistry.registerPostProcessingUDF(testUDF);
        
        assertTrue(PostProcessingUDFRegistry.isRegistered("testClear"));
        assertFalse(PostProcessingUDFRegistry.isRegistered("nonExistent"));
    }

    @Test
    @DisplayName("Test getRegisteredNames")
    void testGetRegisteredNames() {
        PostProcessingUDFRegistry.registerPostProcessingUDF(new TestChainPostProcessingUDF());
        PostProcessingUDFRegistry.registerPostProcessingUDF(new AnotherTestChainPostProcessingUDF());
        
        Set<String> names = PostProcessingUDFRegistry.getRegisteredNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("testClear"));
        assertTrue(names.contains("anotherClear"));
    }

    @Test
    @DisplayName("Test getRegisteredCount")
    void testGetRegisteredCount() {
        assertEquals(0, PostProcessingUDFRegistry.getRegisteredCount());
        
        PostProcessingUDFRegistry.registerPostProcessingUDF(new TestChainPostProcessingUDF());
        assertEquals(1, PostProcessingUDFRegistry.getRegisteredCount());
        
        PostProcessingUDFRegistry.registerPostProcessingUDF(new AnotherTestChainPostProcessingUDF());
        assertEquals(2, PostProcessingUDFRegistry.getRegisteredCount());
    }

    @Test
    @DisplayName("Test clear functionality")
    void testClear() {
        PostProcessingUDFRegistry.registerPostProcessingUDF(new TestChainPostProcessingUDF());
        PostProcessingUDFRegistry.registerPostProcessingUDF(new AnotherTestChainPostProcessingUDF());
        
        assertEquals(2, PostProcessingUDFRegistry.getRegisteredCount());
        
        PostProcessingUDFRegistry.clear();
        
        assertEquals(0, PostProcessingUDFRegistry.getRegisteredCount());
        assertFalse(PostProcessingUDFRegistry.isAutoImportCompleted());
    }

    @Test
    @DisplayName("Test getRegisteredUdfs")
    void testGetRegisteredUdfs() {
        TestChainPostProcessingUDF testUDF = new TestChainPostProcessingUDF();
        PostProcessingUDFRegistry.registerPostProcessingUDF(testUDF);
        
        assertEquals(1, PostProcessingUDFRegistry.getRegisteredUdfs().size());
        assertEquals(testUDF, PostProcessingUDFRegistry.getRegisteredUdfs().get("testClear"));
    }

    @Test
    @DisplayName("Test auto-import idempotent")
    void testAutoImportIdempotent() {
        // Auto-import should be idempotent (safe to call multiple times)
        PostProcessingUDFRegistry.autoImportPostProcessingUDFs();
        int firstCount = PostProcessingUDFRegistry.getRegisteredCount();
        
        PostProcessingUDFRegistry.autoImportPostProcessingUDFs();
        int secondCount = PostProcessingUDFRegistry.getRegisteredCount();
        
        assertEquals(firstCount, secondCount);
        assertTrue(PostProcessingUDFRegistry.isAutoImportCompleted());
    }

    @Test
    @DisplayName("Test auto-registration through Register.importFunction()")
    void testAutoRegistrationThroughRegister() {
        // Verify registry is initially empty
        assertEquals(0, PostProcessingUDFRegistry.getRegisteredCount());
        assertFalse(PostProcessingUDFRegistry.isAutoImportCompleted());

        // Call Register.importFunction() which should trigger auto-registration
        Register.importFunction();

        // Verify auto-import was completed
        assertTrue(PostProcessingUDFRegistry.isAutoImportCompleted());

        // Verify built-in PostProcessingUDFs were registered
        Set<String> registeredNames = PostProcessingUDFRegistry.getRegisteredNames();
        
        // Check for built-in post-processing UDFs
        assertTrue(registeredNames.contains("clear"), "clear should be auto-registered");
        assertTrue(registeredNames.contains("conditionalClear"), "conditionalClear should be auto-registered");

        // Verify we can retrieve the registered UDFs
        assertNotNull(PostProcessingUDFRegistry.getPostProcessingUDF("clear"));
        assertNotNull(PostProcessingUDFRegistry.getPostProcessingUDF("conditionalClear"));
    }

    @Test
    @DisplayName("Test built-in PostProcessingUDF functionality")
    void testBuiltInPostProcessingUDFFunctionality() {
        // Register the UDFs
        Register.importFunction();

        // Test DefaultClearUDF
        ChainPostProcessingUDF defaultClear = PostProcessingUDFRegistry.getPostProcessingUDF("clear");
        assertNotNull(defaultClear);
        assertEquals("clear", defaultClear.getName());
        assertTrue(defaultClear.handle(new ArrayList<>(), 50.0));   // Always returns true
        assertTrue(defaultClear.handle(new ArrayList<>(), 0.0));    // Always returns true
        assertTrue(defaultClear.handle(new ArrayList<>(), null));   // Always returns true

        // Test ConditionalClearUDF
        ChainPostProcessingUDF conditionalClear = PostProcessingUDFRegistry.getPostProcessingUDF("conditionalClear");
        assertNotNull(conditionalClear);
        assertEquals("conditionalClear", conditionalClear.getName());
        assertTrue(conditionalClear.handle(new ArrayList<>(), 150.0));   // Above threshold
        assertFalse(conditionalClear.handle(new ArrayList<>(), 50.0));   // Below threshold
        assertFalse(conditionalClear.handle(new ArrayList<>(), null));   // Null result
    }

    @Test
    @DisplayName("Test registry consistency")
    void testRegistryConsistency() {
        // Auto-import
        PostProcessingUDFRegistry.autoImportPostProcessingUDFs();

        // Verify registry consistency
        Set<String> names = PostProcessingUDFRegistry.getRegisteredNames();
        int count = PostProcessingUDFRegistry.getRegisteredCount();
        
        assertEquals(names.size(), count, "Registered names count should match registered count");

        // Verify each name has a corresponding UDF
        for (String name : names) {
            assertTrue(PostProcessingUDFRegistry.isRegistered(name));
            assertNotNull(PostProcessingUDFRegistry.getPostProcessingUDF(name));
        }

        // Verify getRegisteredUdfs() consistency
        assertEquals(count, PostProcessingUDFRegistry.getRegisteredUdfs().size());
        assertEquals(names, PostProcessingUDFRegistry.getRegisteredUdfs().keySet());
    }
}

