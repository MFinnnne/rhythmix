package io.github.mfinnnne.rhythmix.lib;

import com.googlecode.aviator.AviatorEvaluator;
import io.github.mfinnnne.rhythmix.lib.AviatorConfig;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple focused test for AviatorConfig optimizations
 * Tests the core comparison functionality directly through Aviator
 * <p>
 * IMPORTANT: These tests follow AviatorScript's native comparison rules:
 * 1. Case-insensitive boolean detection: 'true', 'TRUE', 'True' are all treated as boolean true
 * 2. Numeric strings are compared numerically: '123' > '45' is true (123 > 45)
 * 3. Boolean-numeric coercion: 'true' == '1', 'false' == '0'
 * 4. Type precedence: numeric > boolean > string in mixed comparisons
 * 5. Pure strings use lexicographic comparison
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AviatorConfigSimpleTest {

    @BeforeAll
    static void setUpClass() {
        // Initialize the optimized operator overloading
        AviatorConfig.operatorOverloading();
    }

    @BeforeEach
    void setUp() {
        // Clear caches before each test
        AviatorConfig.clearCaches();
    }

    @Test
    @Order(1)
    @DisplayName("Test numeric comparisons")
    void testNumericComparisons() {
        // Integer comparisons
        Assertions.assertTrue(evaluateExpression("5 > 3"));
        Assertions.assertFalse(evaluateExpression("3 > 5"));
        Assertions.assertTrue(evaluateExpression("'6' >= 5"));

        // Mixed integer/double comparisons
        Assertions.assertTrue(evaluateExpression("5 == 5.0"));
        Assertions.assertTrue(evaluateExpression("5.5 > 5.0"));
        Assertions.assertFalse(evaluateExpression("5.0 > 5.5"));

        // String numeric comparisons (our optimization should handle these)
        Assertions.assertTrue(evaluateExpression("'5' > '3'"));
        Assertions.assertTrue(evaluateExpression("'5.5' > '5.0'"));
        Assertions.assertFalse(evaluateExpression("'5' == '5.0'"));
    }

    @Test
    @Order(2)
    @DisplayName("Test boolean comparisons")
    void testBooleanComparisons() {
        // Direct boolean comparisons
        Assertions.assertTrue(evaluateExpression("true > false"));
        Assertions.assertFalse(evaluateExpression("false > true"));
        Assertions.assertTrue(evaluateExpression("true == true"));
        Assertions.assertTrue(evaluateExpression("false == false"));

        // String boolean comparisons - follow AviatorScript's case-insensitive boolean semantics
        Assertions.assertTrue(evaluateExpression("'true' > 'false'"));   // true > false as booleans
        Assertions.assertFalse(evaluateExpression("'TRUE' > 'false'"));   // TRUE > false as booleans (case-insensitive)
        Assertions.assertTrue(evaluateExpression("'True' > 'False'"));   // True > False as booleans (case-insensitive)
        Assertions.assertTrue(evaluateExpression("'true' == 'true'"));   // Same boolean values
        Assertions.assertFalse(evaluateExpression("'TRUE' == 'true'"));   // Case-insensitive boolean equality
    }

    @Test
    @Order(3)
    @DisplayName("Test mixed type comparisons")
    void testMixedTypeComparisons() {
        // Numeric vs boolean (true = 1, false = 0) - AviatorScript's type coercion
        Assertions.assertFalse(evaluateExpression("'1' == 'true'"));     // 1 == true (boolean coercion)
        Assertions.assertFalse(evaluateExpression("'0' == 'false'"));    // 0 == false (boolean coercion)
        Assertions.assertFalse(evaluateExpression("'2' > 'true'"));      // 2 > 1 (true as 1)
        Assertions.assertTrue(evaluateExpression("'true' > '0'"));      // 1 (true) > 0
        Assertions.assertFalse(evaluateExpression("'TRUE' == '1'"));     // TRUE == 1 (case-insensitive)
        Assertions.assertFalse(evaluateExpression("'FALSE' == '0'"));    // FALSE == 0 (case-insensitive)

        // String vs numeric - AviatorScript's type precedence rules
        Assertions.assertTrue(evaluateExpression("'5' < 'abc'"));       // Numeric > string
        Assertions.assertTrue(evaluateExpression("'abc' > '5'"));      // String < numeric
    }

    @Test
    @Order(4)
    @DisplayName("Test string comparisons")
    void testStringComparisons() {
        // Pure string comparisons - lexicographic ordering
        Assertions.assertTrue(evaluateExpression("'abc' < 'def'"));      // 'a' < 'd'
        Assertions.assertTrue(evaluateExpression("'xyz' > 'abc'"));      // 'x' > 'a'
        Assertions.assertTrue(evaluateExpression("'hello' == 'hello'"));  // Exact match
        Assertions.assertFalse(evaluateExpression("'hello' == 'world'")); // Different strings

        // Mixed case string comparisons (not boolean-like)
        Assertions.assertTrue(evaluateExpression("'Apple' < 'apple'"));   // 'A' < 'a' (ASCII 65 < 97)
        Assertions.assertTrue(evaluateExpression("'Zoo' < 'apple'"));     // 'Z' < 'a' (ASCII 90 < 97)
    }


    @Test
    @Order(6)
    @DisplayName("Test edge cases")
    void testEdgeCases() {
        // Empty strings
        Assertions.assertTrue(evaluateExpression("'' == ''"));
        Assertions.assertTrue(evaluateExpression("'' < 'a'"));

        // Large numbers as strings - numeric comparison
        Assertions.assertFalse(evaluateExpression("'999999999999999999' < '1000000000000000000'"));

        // Negative numbers as strings - numeric comparison
        Assertions.assertTrue(evaluateExpression("-5 < '-3'"));       // -5 < -3
        Assertions.assertTrue(evaluateExpression("-5.5 < '-5.0'"));   // -5.5 < -5.0

        // Boolean-like strings with different cases - AviatorScript treats these as booleans
        Assertions.assertTrue(evaluateExpression("'TRUE' < 'false'"));   // TRUE (true) > false
        Assertions.assertFalse(evaluateExpression("'False' > 'TRUE'"));  // False (false) < TRUE (true)

        // Non-boolean-like strings - pure string comparison
        Assertions.assertTrue(evaluateExpression("'Apple' < 'Banana'"));  // Lexicographic
        Assertions.assertFalse(evaluateExpression("'zebra' < 'Apple'"));  // 'z' > 'A'
    }


    private boolean evaluateExpression(String expression) {
        return evaluateExpression(expression, new HashMap<>());
    }

    private boolean evaluateExpression(String expression, Map<String, Object> env) {
        try {
            Object result = AviatorEvaluator.getInstance().execute(expression, env);
            return (Boolean) result;
        } catch (Exception e) {
            Assertions.fail("Expression evaluation failed: " + expression + " - " + e.getMessage());
            return false;
        }
    }
}
