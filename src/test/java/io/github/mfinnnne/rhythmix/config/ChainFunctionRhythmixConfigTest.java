package io.github.mfinnnne.rhythmix.config;

import io.github.mfinnnne.rhythmix.lib.Register;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Test class for ChainFunctionConfig functionality
 */
public class ChainFunctionRhythmixConfigTest {

    @BeforeAll
    static void beforeAll() {
        Register.importFunction();
    }

    @Test
    @DisplayName("Test singleton instance creation")
    void testSingletonInstanceCreation() {
        ChainFunctionConfig config1 = ChainFunctionConfig.getInstance();
        ChainFunctionConfig config2 = ChainFunctionConfig.getInstance();
        
        assertNotNull(config1);
        assertNotNull(config2);
        assertSame(config1, config2, "Should return the same instance");
    }

    @Test
    @DisplayName("Test default initialization")
    void testDefaultInitialization() {
        ChainFunctionConfig config = ChainFunctionConfig.getInstance();
        
        // Check start functions
        assertTrue(config.getStartFunc().contains("filter"));
        
        // Check end functions
        assertTrue(config.getEndFunc().contains("meet"));
        
        // Check calculation functions
        assertTrue(config.getCalcFunc().contains("sum"));
        assertTrue(config.getCalcFunc().contains("count"));
        assertTrue(config.getCalcFunc().contains("avg"));
        
        // Check post-processing functions
        assertTrue(config.getPostProcessing().contains("clear"));
    }

    @Test
    @DisplayName("Test addStartFunc")
    void testAddStartFunc() {
        ChainFunctionConfig config = ChainFunctionConfig.getInstance();
        int initialCount = config.getStartFunc().size();
        
        config.addStartFunc("customStart");
        
        assertTrue(config.getStartFunc().contains("customStart"));
        assertEquals(initialCount + 1, config.getStartFunc().size());
    }

    @Test
    @DisplayName("Test addStartFunc with duplicate")
    void testAddStartFuncWithDuplicate() {
        ChainFunctionConfig config = ChainFunctionConfig.getInstance();
        int initialCount = config.getStartFunc().size();
        
        config.addStartFunc("filter");
        
        // Should not add duplicate
        assertEquals(initialCount, config.getStartFunc().size());
    }

    @Test
    @DisplayName("Test addCalcFunc")
    void testAddCalcFunc() {
        ChainFunctionConfig config = ChainFunctionConfig.getInstance();
        int initialCount = config.getCalcFunc().size();
        
        config.addCalcFunc("customCalc");
        
        assertTrue(config.getCalcFunc().contains("customCalc"));
        assertEquals(initialCount + 1, config.getCalcFunc().size());
    }

    @Test
    @DisplayName("Test addCalcFunc with duplicate")
    void testAddCalcFuncWithDuplicate() {
        ChainFunctionConfig config = ChainFunctionConfig.getInstance();
        int initialCount = config.getCalcFunc().size();
        
        config.addCalcFunc("sum");
        
        // Should not add duplicate
        assertEquals(initialCount, config.getCalcFunc().size());
    }

    @Test
    @DisplayName("Test addEndFunc")
    void testAddEndFunc() {
        ChainFunctionConfig config = ChainFunctionConfig.getInstance();
        int initialCount = config.getEndFunc().size();
        
        config.addEndFunc("customMeet");
        
        assertTrue(config.getEndFunc().contains("customMeet"));
        assertEquals(initialCount + 1, config.getEndFunc().size());
    }

    @Test
    @DisplayName("Test addEndFunc with duplicate")
    void testAddEndFuncWithDuplicate() {
        ChainFunctionConfig config = ChainFunctionConfig.getInstance();
        int initialCount = config.getEndFunc().size();
        
        config.addEndFunc("meet");
        
        // Should not add duplicate
        assertEquals(initialCount, config.getEndFunc().size());
    }

    @Test
    @DisplayName("Test addPostProcessing")
    void testAddPostProcessing() {
        ChainFunctionConfig config = ChainFunctionConfig.getInstance();
        int initialCount = config.getPostProcessing().size();
        
        config.addPostProcessing("customClear");
        
        assertTrue(config.getPostProcessing().contains("customClear"));
        assertEquals(initialCount + 1, config.getPostProcessing().size());
    }

    @Test
    @DisplayName("Test addPostProcessing with duplicate")
    void testAddPostProcessingWithDuplicate() {
        ChainFunctionConfig config = ChainFunctionConfig.getInstance();
        int initialCount = config.getPostProcessing().size();
        
        config.addPostProcessing("clear");
        
        // Should not add duplicate
        assertEquals(initialCount, config.getPostProcessing().size());
    }

    @Test
    @DisplayName("Test addPostProcessing with multiple functions")
    void testAddPostProcessingWithMultipleFunctions() {
        ChainFunctionConfig config = ChainFunctionConfig.getInstance();
        int initialCount = config.getPostProcessing().size();
        
        config.addPostProcessing("customClear1", "customClear2", "customClear3");
        
        assertTrue(config.getPostProcessing().contains("customClear1"));
        assertTrue(config.getPostProcessing().contains("customClear2"));
        assertTrue(config.getPostProcessing().contains("customClear3"));
        assertEquals(initialCount + 3, config.getPostProcessing().size());
    }

    @Test
    @DisplayName("Test call tree is rebuilt after addPostProcessing")
    void testCallTreeRebuiltAfterAddPostProcessing() {
        ChainFunctionConfig config = ChainFunctionConfig.getInstance();
        Map<String, List<String>> callTree = config.getCallTree();
        
        // Get the initial post-processing functions that can follow meet
        List<String> meetFollowups = callTree.get("meet");
        assertNotNull(meetFollowups);
        assertTrue(meetFollowups.contains("clear"));
        
        // Add a new post-processing function
        config.addPostProcessing("conditionalClear");
        
        // Get the updated call tree
        callTree = config.getCallTree();
        meetFollowups = callTree.get("meet");
        
        // Verify the new function is in the call tree
        assertTrue(meetFollowups.contains("clear"));
        assertTrue(meetFollowups.contains("conditionalClear"));

    }

    @Test
    @DisplayName("Test call tree structure")
    void testCallTreeStructure() {
        ChainFunctionConfig config = ChainFunctionConfig.getInstance();
        Map<String, List<String>> callTree = config.getCallTree();
        
        // Verify filter can be followed by limit/window/calc functions
        List<String> filterFollowups = callTree.get("filter");
        assertNotNull(filterFollowups);
        assertTrue(filterFollowups.contains("limit"));
        assertTrue(filterFollowups.contains("window"));
        assertTrue(filterFollowups.contains("sum"));
        
        // Verify calc functions can be followed by meet
        List<String> sumFollowups = callTree.get("sum");
        assertNotNull(sumFollowups);
        assertTrue(sumFollowups.contains("meet"));
        
        // Verify meet can be followed by post-processing functions
        List<String> meetFollowups = callTree.get("meet");
        assertNotNull(meetFollowups);
        assertTrue(meetFollowups.contains("clear"));
    }

    @Test
    @DisplayName("Test createNewInstance")
    void testCreateNewInstance() {
        ChainFunctionConfig config1 = ChainFunctionConfig.getInstance();
        ChainFunctionConfig config2 = ChainFunctionConfig.createNewInstance();
        
        assertNotSame(config1, config2, "Should create a new instance");
        
        // Both should have the same default configuration
        assertTrue(config1.getStartFunc().contains("filter"));
        assertTrue(config2.getStartFunc().contains("filter"));
    }

    @Test
    @DisplayName("Test thread safety of addPostProcessing")
    void testThreadSafetyOfAddPostProcessing() throws InterruptedException {
        ChainFunctionConfig config = ChainFunctionConfig.getInstance();
        int initialCount = config.getPostProcessing().size();
        
        // Create multiple threads to add post-processing functions
        Thread thread1 = new Thread(() -> config.addPostProcessing("threadClear1"));
        Thread thread2 = new Thread(() -> config.addPostProcessing("threadClear2"));
        Thread thread3 = new Thread(() -> config.addPostProcessing("threadClear3"));
        
        thread1.start();
        thread2.start();
        thread3.start();
        
        thread1.join();
        thread2.join();
        thread3.join();
        
        // Verify all functions were added
        assertTrue(config.getPostProcessing().contains("threadClear1"));
        assertTrue(config.getPostProcessing().contains("threadClear2"));
        assertTrue(config.getPostProcessing().contains("threadClear3"));
        assertEquals(initialCount + 3, config.getPostProcessing().size());
    }
}

