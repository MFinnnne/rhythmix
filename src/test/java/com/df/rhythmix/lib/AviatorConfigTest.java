package com.df.rhythmix.lib;

import com.googlecode.aviator.AviatorEvaluator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author MFine
 * @version 1.0
 * @date 2025/6/8 0:01
 **/
class AviatorConfigTest {

    @BeforeAll
    static void beforeAll() {
        AviatorConfig.operatorOverloading();
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void operatorOverloading() {
        System.out.println(AviatorEvaluator.execute("long(1.0)>long(2)"));
    }

    @Test
    void testNumericComparison() {
        Map<String, Object> env = new HashMap<>();
        
        // Integer vs Integer
        env.put("a", 10);
        env.put("b", 5);
        assertTrue((Boolean) AviatorEvaluator.execute("a > b", env));
        
        // String number vs Integer
        env.put("a", "15");
        env.put("b", 10);
        assertTrue((Boolean) AviatorEvaluator.execute("a > b", env));
        
        // Double vs Integer
        env.put("a", 10.5);
        env.put("b", 10);
        assertTrue((Boolean) AviatorEvaluator.execute("a > b", env));
        
        // String double vs String integer
        env.put("a", "10.5");
        env.put("b", "10");
        assertTrue((Boolean) AviatorEvaluator.execute("a > b", env));
    }

    @Test
    void testMixedTypeComparison() {
        Map<String, Object> env = new HashMap<>();
        
        // Number vs Boolean
        env.put("a", 2);
        env.put("b", true); // true is treated as 1
        assertTrue((Boolean) AviatorEvaluator.execute("a > b", env));
        
        env.put("a", 0);
        env.put("b", false); // false is treated as 0
        assertFalse((Boolean) AviatorEvaluator.execute("a > b", env));
        
        // String number vs Boolean
        env.put("a", "2");
        env.put("b", "true");
        assertTrue((Boolean) AviatorEvaluator.execute("a > b", env));
        
        // Number vs non-numeric string
        env.put("a", 10);
        env.put("b", "hello");
        assertTrue((Boolean) AviatorEvaluator.execute("a > b", env));
    }

    @Test
    void testBooleanComparison() {
        Map<String, Object> env = new HashMap<>();
        
        // Boolean vs Boolean
        env.put("a", true);
        env.put("b", false);
        assertTrue((Boolean) AviatorEvaluator.execute("a > b", env));
        
        // String boolean vs String boolean
        env.put("a", "true");
        env.put("b", "false");
        assertTrue((Boolean) AviatorEvaluator.execute("a > b", env));
        
        // Boolean vs non-boolean string
        env.put("a", true);
        env.put("b", "hello");
        assertTrue((Boolean) AviatorEvaluator.execute("a > b", env));
        
        env.put("a", false);
        env.put("b", "hello");
        assertFalse((Boolean) AviatorEvaluator.execute("a > b", env));
    }

    @Test
    void testNullComparison() {
        Map<String, Object> env = new HashMap<>();
        
        // null vs null
        env.put("a", null);
        env.put("b", null);
        assertFalse((Boolean) AviatorEvaluator.execute("a > b", env));
        
        // value vs null
        env.put("a", 10);
        env.put("b", null);
        assertTrue((Boolean) AviatorEvaluator.execute("a > b", env));
        
        // null vs value
        env.put("a", null);
        env.put("b", 10);
        assertFalse((Boolean) AviatorEvaluator.execute("a > b", env));
    }

    @Test
    void testStringComparison() {
        Map<String, Object> env = new HashMap<>();
        
        // Pure string comparison (lexicographic)
        env.put("a", "zebra");
        env.put("b", "apple");
        assertTrue((Boolean) AviatorEvaluator.execute("a > b", env));
        
        env.put("a", "apple");
        env.put("b", "banana");
        assertFalse((Boolean) AviatorEvaluator.execute("a > b", env));
    }

    @Test
    void testEdgeCases() {
        Map<String, Object> env = new HashMap<>();
        
        // Empty string vs number
        env.put("a", "");
        env.put("b", 0);
        assertFalse((Boolean) AviatorEvaluator.execute("a > b", env));
        
        // Negative numbers
        env.put("a", "-5");
        env.put("b", "-10");
        assertTrue((Boolean) AviatorEvaluator.execute("a > b", env));
        
        // Very large numbers
        env.put("a", "999999999999999999");
        env.put("b", "999999999999999998");
        assertTrue((Boolean) AviatorEvaluator.execute("a > b", env));
        
        // Decimal precision
        env.put("a", "10.001");
        env.put("b", "10.0009");
        assertTrue((Boolean) AviatorEvaluator.execute("a > b", env));
    }

    @Test
    @DisplayName("测试 >= 操作符类型转换")
    void testGTEOperatorTypeConversion() {
        // Test case for the original error: comparing String "9" with Long 7
        AviatorConfig.operatorOverloading();
        
        // Test String >= Long
        Object result1 = AviatorEvaluator.execute("'9' >= 7");
        Assertions.assertTrue((Boolean) result1, "String '9' should be >= Long 7");
        
        // Test Long >= String
        Object result2 = AviatorEvaluator.execute("9 >= '7'");
        Assertions.assertTrue((Boolean) result2, "Long 9 should be >= String '7'");
        
        // Test equal values
        Object result3 = AviatorEvaluator.execute("'7' >= 7");
        Assertions.assertTrue((Boolean) result3, "String '7' should be >= Long 7");
        
        // Test negative case
        Object result4 = AviatorEvaluator.execute("'5' >= 7");
        Assertions.assertFalse((Boolean) result4, "String '5' should not be >= Long 7");
        
        // Test all comparison operators
        Assertions.assertTrue((Boolean) AviatorEvaluator.execute("'9' > 7"), "String '9' should be > Long 7");
        Assertions.assertTrue((Boolean) AviatorEvaluator.execute("'5' < 7"), "String '5' should be < Long 7");
        Assertions.assertTrue((Boolean) AviatorEvaluator.execute("'7' <= 7"), "String '7' should be <= Long 7");
    }

    @Test
    @DisplayName("测试 == 和 != 操作符类型转换")
    void testEQOperatorTypeConversion() {
        AviatorConfig.operatorOverloading();
        
        // Test String == Long
        Object result1 = AviatorEvaluator.execute("'7' == 7");
        Assertions.assertTrue((Boolean) result1, "String '7' should be == Long 7");
        
        // Test Long == String
        Object result2 = AviatorEvaluator.execute("7 == '7'");
        Assertions.assertTrue((Boolean) result2, "Long 7 should be == String '7'");
        
        // Test not equal values
        Object result3 = AviatorEvaluator.execute("'5' == 7");
        Assertions.assertFalse((Boolean) result3, "String '5' should not be == Long 7");
        
        // Test boolean conversions
        Object result4 = AviatorEvaluator.execute("'true' == 1");
        Assertions.assertTrue((Boolean) result4, "String 'true' should be == Long 1");
        
        Object result5 = AviatorEvaluator.execute("'false' == 0");
        Assertions.assertTrue((Boolean) result5, "String 'false' should be == Long 0");
        
        // Test != operator
        Object result6 = AviatorEvaluator.execute("'5' != 7");
        Assertions.assertTrue((Boolean) result6, "String '5' should be != Long 7");
        
        Object result7 = AviatorEvaluator.execute("'7' != 7");
        Assertions.assertFalse((Boolean) result7, "String '7' should not be != Long 7");
        
        // Test decimal equality
        Object result8 = AviatorEvaluator.execute("'7.0' == 7");
        Assertions.assertTrue((Boolean) result8, "String '7.0' should be == Long 7");
    }
}