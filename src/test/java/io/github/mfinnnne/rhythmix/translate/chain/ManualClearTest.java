package io.github.mfinnnne.rhythmix.translate.chain;

import io.github.mfinnnne.rhythmix.execute.RhythmixCompiler;
import io.github.mfinnnne.rhythmix.execute.RhythmixExecutor;
import io.github.mfinnnne.rhythmix.lib.Register;
import io.github.mfinnnne.rhythmix.pebble.TemplateEngine;
import io.github.mfinnnne.rhythmix.translate.EnvProxy;
import io.github.mfinnnne.rhythmix.translate.Translator;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import io.github.mfinnnne.rhythmix.util.Util;

import java.sql.Timestamp;

/**
 * Manual test for the Clear post-processing operator.
 * Run this class directly to test the clear operator functionality.
 * 
 * @author MFine
 */
public class ManualClearTest {

    public static void main(String[] args) {
        try {
            Register.importFunction();
            TemplateEngine.enableDebugModel(true);
            
            System.out.println("=== Testing Clear Operator ===\n");
            
            // Test 1: Basic clear operator after meet
            System.out.println("Test 1: Basic clear operator after meet");
            testBasicClear();
            
            // Test 2: Clear operator translation
            System.out.println("\nTest 2: Clear operator translation");
            testClearTranslation();
            
            // Test 3: Clear operator must be at the end (should fail)
            System.out.println("\nTest 3: Clear operator must be at the end (should fail)");
            testClearMustBeAtEnd();
            
            System.out.println("\n=== All tests completed ===");
            
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testBasicClear() throws Exception {
        String code = "filter((-5,5)).limit(5).sum().meet(>1).clear()";
        EnvProxy env = new EnvProxy();
        String transCode = Translator.translate(code, env);
        System.out.println("Translated code:\n" + transCode);
        
        RhythmixExecutor rhythmixExecutor = new RhythmixExecutor(transCode, env);
        
        RhythmixEventData p1 = Util.genEventData("1", "1", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "2", new Timestamp(System.currentTimeMillis()));
        
        // First execution: sum = 1, not > 1, should return false
        boolean execute1 = rhythmixExecutor.execute(p1);
        System.out.println("First execution (value=1): " + execute1 + " (expected: false)");
        assert !execute1 : "First execution should return false";
        
        // Second execution: sum = 3, > 1, should return true and clear queues
        boolean execute2 = rhythmixExecutor.execute(p2);
        System.out.println("Second execution (value=2, sum=3): " + execute2 + " (expected: true)");
        assert execute2 : "Second execution should return true";
        
        // After clear, the queues should be empty, so next execution starts fresh
        // Third execution: sum = 1, not > 1, should return false
        boolean execute3 = rhythmixExecutor.execute(p1);
        System.out.println("Third execution after clear (value=1): " + execute3 + " (expected: false)");
        assert !execute3 : "Third execution should return false (queues should be cleared)";
        
        System.out.println("✓ Test 1 passed");
    }
    
    private static void testClearTranslation() throws Exception {
        String code = "count().meet(>5).clear()";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);
        assert rhythmixExecutor != null : "Executor should not be null";
        System.out.println("✓ Test 2 passed - clear operator translates correctly");
    }
    
    private static void testClearMustBeAtEnd() {
        try {
            // clear() can only appear at the end of a chain expression
            String code = "filter().clear().sum().meet(>1)";
            RhythmixCompiler.compile(code);
            System.out.println("✗ Test 3 failed - should have thrown exception");
        } catch (Exception e) {
            System.out.println("✓ Test 3 passed - correctly rejected clear in middle of chain: " + e.getMessage());
        }
    }
}

