package io.github.mfinnnne.rhythmix.udf;

import io.github.mfinnnne.rhythmix.lib.Register;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

/**
 * Test class for MeetUDFRegistry functionality
 */
public class ChainMeetUDFRegistryTest {

    // Test implementation of MeetUDF
    public static class TestChainMeetUDF implements ChainMeetUDF {
        @Override
        public String getName() {
            return "testMeet";
        }

        @Override
        public boolean meet(Number calculatedValue) {
            if (calculatedValue != null) {
                return calculatedValue.doubleValue() > 5.0;
            }
            return false;
        }
    }

    // Another test implementation
    public static class AnotherTestChainMeetUDF implements ChainMeetUDF {
        @Override
        public String getName() {
            return "anotherMeet";
        }

        @Override
        public boolean meet(Number calculatedValue) {
            if (calculatedValue != null) {
                return calculatedValue.doubleValue() < 100.0;
            }
            return false;
        }
    }

    // Test implementation with duplicate name
    public static class DuplicateNameChainMeetUDF implements ChainMeetUDF {
        @Override
        public String getName() {
            return "testMeet"; // Same name as TestMeetUDF
        }

        @Override
        public boolean meet(Number calculatedValue) {
            return true;
        }
    }

    @BeforeEach
    void setUp() {
        // Clear registry before each test
        MeetUDFRegistry.clear();
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        MeetUDFRegistry.clear();
    }

    @Test
    @DisplayName("Test auto-import completion status")
    void testAutoImportCompleted() {
        assertFalse(MeetUDFRegistry.isAutoImportCompleted());
        
        MeetUDFRegistry.autoImportMeetUDFs();
        
        assertTrue(MeetUDFRegistry.isAutoImportCompleted());
    }

    @Test
    @DisplayName("Test manual registration")
    void testManualRegistration() {
        TestChainMeetUDF testUDF = new TestChainMeetUDF();
        
        // Test successful registration
        assertTrue(MeetUDFRegistry.registerMeetUDF(testUDF));
        assertEquals(1, MeetUDFRegistry.getRegisteredCount());
        assertTrue(MeetUDFRegistry.isRegistered("testMeet"));
        assertEquals(testUDF, MeetUDFRegistry.getMeetUDF("testMeet"));
    }

    @Test
    @DisplayName("Test duplicate registration")
    void testDuplicateRegistration() {
        TestChainMeetUDF testUDF1 = new TestChainMeetUDF();
        DuplicateNameChainMeetUDF testUDF2 = new DuplicateNameChainMeetUDF();
        
        // First registration should succeed
        assertTrue(MeetUDFRegistry.registerMeetUDF(testUDF1));
        
        // Second registration with same name should fail
        assertFalse(MeetUDFRegistry.registerMeetUDF(testUDF2));
        
        assertEquals(1, MeetUDFRegistry.getRegisteredCount());
        assertEquals(testUDF1, MeetUDFRegistry.getMeetUDF("testMeet"));
    }

    @Test
    @DisplayName("Test null registration")
    void testNullRegistration() {
        assertFalse(MeetUDFRegistry.registerMeetUDF(null));
        assertEquals(0, MeetUDFRegistry.getRegisteredCount());
    }

    @Test
    @DisplayName("Test getMeetUDF")
    void testGetMeetUDF() {
        TestChainMeetUDF testUDF = new TestChainMeetUDF();
        MeetUDFRegistry.registerMeetUDF(testUDF);
        
        assertEquals(testUDF, MeetUDFRegistry.getMeetUDF("testMeet"));
        assertNull(MeetUDFRegistry.getMeetUDF("nonExistent"));
    }

    @Test
    @DisplayName("Test isRegistered")
    void testIsRegistered() {
        TestChainMeetUDF testUDF = new TestChainMeetUDF();
        
        assertFalse(MeetUDFRegistry.isRegistered("testMeet"));
        
        MeetUDFRegistry.registerMeetUDF(testUDF);
        
        assertTrue(MeetUDFRegistry.isRegistered("testMeet"));
        assertFalse(MeetUDFRegistry.isRegistered("nonExistent"));
    }

    @Test
    @DisplayName("Test getRegisteredNames")
    void testGetRegisteredNames() {
        TestChainMeetUDF testUDF1 = new TestChainMeetUDF();
        AnotherTestChainMeetUDF testUDF2 = new AnotherTestChainMeetUDF();
        
        assertTrue(MeetUDFRegistry.getRegisteredNames().isEmpty());
        
        MeetUDFRegistry.registerMeetUDF(testUDF1);
        MeetUDFRegistry.registerMeetUDF(testUDF2);
        
        Set<String> names = MeetUDFRegistry.getRegisteredNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("testMeet"));
        assertTrue(names.contains("anotherMeet"));
    }

    @Test
    @DisplayName("Test getRegisteredCount")
    void testGetRegisteredCount() {
        assertEquals(0, MeetUDFRegistry.getRegisteredCount());
        
        MeetUDFRegistry.registerMeetUDF(new TestChainMeetUDF());
        assertEquals(1, MeetUDFRegistry.getRegisteredCount());
        
        MeetUDFRegistry.registerMeetUDF(new AnotherTestChainMeetUDF());
        assertEquals(2, MeetUDFRegistry.getRegisteredCount());
    }

    @Test
    @DisplayName("Test clear functionality")
    void testClear() {
        MeetUDFRegistry.registerMeetUDF(new TestChainMeetUDF());
        MeetUDFRegistry.registerMeetUDF(new AnotherTestChainMeetUDF());
        
        assertEquals(2, MeetUDFRegistry.getRegisteredCount());
        
        MeetUDFRegistry.clear();
        
        assertEquals(0, MeetUDFRegistry.getRegisteredCount());
        assertFalse(MeetUDFRegistry.isAutoImportCompleted());
    }

    @Test
    @DisplayName("Test getRegisteredUdfs")
    void testGetRegisteredUdfs() {
        TestChainMeetUDF testUDF = new TestChainMeetUDF();
        MeetUDFRegistry.registerMeetUDF(testUDF);
        
        assertEquals(1, MeetUDFRegistry.getRegisteredUdfs().size());
        assertEquals(testUDF, MeetUDFRegistry.getRegisteredUdfs().get("testMeet"));
    }

    @Test
    @DisplayName("Test auto-import idempotent")
    void testAutoImportIdempotent() {
        // Auto-import should be idempotent (safe to call multiple times)
        MeetUDFRegistry.autoImportMeetUDFs();
        int firstCount = MeetUDFRegistry.getRegisteredCount();
        
        MeetUDFRegistry.autoImportMeetUDFs();
        int secondCount = MeetUDFRegistry.getRegisteredCount();
        
        assertEquals(firstCount, secondCount);
        assertTrue(MeetUDFRegistry.isAutoImportCompleted());
    }

    @Test
    @DisplayName("Test auto-registration through Register.importFunction()")
    void testAutoRegistrationThroughRegister() {
        // Verify registry is initially empty
        assertEquals(0, MeetUDFRegistry.getRegisteredCount());
        assertFalse(MeetUDFRegistry.isAutoImportCompleted());

        // Call Register.importFunction() which should trigger auto-registration
        Register.importFunction();

        // Verify auto-import was completed
        assertTrue(MeetUDFRegistry.isAutoImportCompleted());

        // Verify built-in MeetUDFs were registered
        Set<String> registeredNames = MeetUDFRegistry.getRegisteredNames();
        
        // Check for built-in meet UDFs
        assertTrue(registeredNames.contains("thresholdMeet"), "thresholdMeet should be auto-registered");
        assertTrue(registeredNames.contains("rangeMeet"), "rangeMeet should be auto-registered");
        assertTrue(registeredNames.contains("positiveMeet"), "positiveMeet should be auto-registered");
        assertTrue(registeredNames.contains("evenMeet"), "evenMeet should be auto-registered");

        // Verify we can retrieve the registered UDFs
        assertNotNull(MeetUDFRegistry.getMeetUDF("thresholdMeet"));
        assertNotNull(MeetUDFRegistry.getMeetUDF("rangeMeet"));
        assertNotNull(MeetUDFRegistry.getMeetUDF("positiveMeet"));
        assertNotNull(MeetUDFRegistry.getMeetUDF("evenMeet"));

        // Verify the count is correct
        assertTrue(MeetUDFRegistry.getRegisteredCount() >= 4, 
                   "Should have at least 4 built-in MeetUDFs registered");
    }

    @Test
    @DisplayName("Test built-in MeetUDF functionality")
    void testBuiltInMeetUDFFunctionality() {
        // Register the UDFs
        Register.importFunction();

        // Test ThresholdMeetUDF
        ChainMeetUDF thresholdMeet = MeetUDFRegistry.getMeetUDF("thresholdMeet");
        assertNotNull(thresholdMeet);
        assertEquals("thresholdMeet", thresholdMeet.getName());
        assertTrue(thresholdMeet.meet(15));   // Above threshold
        assertFalse(thresholdMeet.meet(5));   // Below threshold

        // Test RangeMeetUDF
        ChainMeetUDF rangeMeet = MeetUDFRegistry.getMeetUDF("rangeMeet");
        assertNotNull(rangeMeet);
        assertEquals("rangeMeet", rangeMeet.getName());
        assertTrue(rangeMeet.meet(25));   // Within range
        assertFalse(rangeMeet.meet(100)); // Outside range

        // Test PositiveMeetUDF
        ChainMeetUDF positiveMeet = MeetUDFRegistry.getMeetUDF("positiveMeet");
        assertNotNull(positiveMeet);
        assertEquals("positiveMeet", positiveMeet.getName());
        assertTrue(positiveMeet.meet(5));    // Positive
        assertFalse(positiveMeet.meet(-5));  // Negative

        // Test EvenMeetUDF
        ChainMeetUDF evenMeet = MeetUDFRegistry.getMeetUDF("evenMeet");
        assertNotNull(evenMeet);
        assertEquals("evenMeet", evenMeet.getName());
        assertTrue(evenMeet.meet(4));    // Even
        assertFalse(evenMeet.meet(3));   // Odd
    }

    @Test
    @DisplayName("Test registry consistency")
    void testRegistryConsistency() {
        // Auto-import
        MeetUDFRegistry.autoImportMeetUDFs();

        // Verify registry consistency
        Set<String> names = MeetUDFRegistry.getRegisteredNames();
        int count = MeetUDFRegistry.getRegisteredCount();
        
        assertEquals(names.size(), count, "Registered names count should match registered count");

        // Verify each name has a corresponding UDF
        for (String name : names) {
            assertTrue(MeetUDFRegistry.isRegistered(name));
            assertNotNull(MeetUDFRegistry.getMeetUDF(name));
        }

        // Verify getRegisteredUdfs() consistency
        assertEquals(count, MeetUDFRegistry.getRegisteredUdfs().size());
        assertEquals(names, MeetUDFRegistry.getRegisteredUdfs().keySet());
    }
}
