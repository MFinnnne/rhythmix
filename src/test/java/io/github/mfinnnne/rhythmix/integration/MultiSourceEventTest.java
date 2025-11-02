package io.github.mfinnnne.rhythmix.integration;

import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.execute.RhythmixCompiler;
import io.github.mfinnnne.rhythmix.execute.RhythmixExecutor;
import io.github.mfinnnne.rhythmix.pebble.TemplateEngine;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Integration tests for multi-source event expressions.
 * <p>
 * Tests the complete flow from parsing to execution for expressions that combine
 * conditions from multiple event sources using the syntax:
 * {@code {#eventSource:condition# logicalOp #eventSource:condition#}}
 *
 * @author MFine
 * @version 1.0
 * @since 1.1
 */
public class MultiSourceEventTest {

    // ==================== Two Source Tests ====================

    /**
     * Test two-source event expression with AND operator.
     * Both conditions must be met for the expression to return true.
     */
    @Test
    @DisplayName("Multi-source: Two sources with AND - Both conditions must be met")
    void testTwoSourceAnd() throws TranslatorException {
        String code = "{#temp:<30# && #humidity:>80#}";
        TemplateEngine.enableDebugModel(true);
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> events = new ArrayList<>();
        
        // Send temperature event (satisfies first condition)
        events.add(new RhythmixEventData("1", "temp", "25", new Timestamp(System.currentTimeMillis())));
        
        // Send humidity event (satisfies second condition)
        events.add(new RhythmixEventData("2", "humidity", "85", new Timestamp(System.currentTimeMillis() + 100)));

        boolean result = false;
        for (RhythmixEventData event : events) {
            result = rhythmixExecutor.execute(event);
        }

        // Both conditions met, should return true
        Assertions.assertTrue(result);
    }

    /**
     * Test two-source event expression with AND operator.
     * Only one condition is met, should return false.
     */
    @Test
    @DisplayName("Multi-source: Two sources with AND - Only one condition met")
    void testTwoSourceAndOnlyOneMet() throws TranslatorException {
        String code = "{#temp:<30# && #humidity:>80#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> events = new ArrayList<>();
        
        // Send temperature event (satisfies first condition)
        events.add(new RhythmixEventData("1", "temp", "25", new Timestamp(System.currentTimeMillis())));
        
        // Send humidity event (does NOT satisfy second condition)
        events.add(new RhythmixEventData("2", "humidity", "70", new Timestamp(System.currentTimeMillis() + 100)));

        boolean result = false;
        for (RhythmixEventData event : events) {
            result = rhythmixExecutor.execute(event);
        }

        // Only one condition met, should return false
        Assertions.assertFalse(result);
    }

    /**
     * Test two-source event expression with OR operator.
     * Either condition can trigger true result.
     */
    @Test
    @DisplayName("Multi-source: Two sources with OR - Either condition triggers true")
    void testTwoSourceOr() throws TranslatorException {
        String code = "{#temp:<30# || #humidity:>80#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> events = new ArrayList<>();
        
        // Send temperature event (satisfies first condition)
        events.add(new RhythmixEventData("1", "temp", "25", new Timestamp(System.currentTimeMillis())));

        boolean result = rhythmixExecutor.execute(events.get(0));

        // First condition met, should return true immediately
        Assertions.assertTrue(result);
    }

    /**
     * Test two-source event expression with OR operator.
     * Second condition triggers true.
     */
    @Test
    @DisplayName("Multi-source: Two sources with OR - Second condition triggers true")
    void testTwoSourceOrSecondCondition() throws TranslatorException {
        String code = "{#temp:<30# || #humidity:>80#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> events = new ArrayList<>();
        
        // Send temperature event (does NOT satisfy first condition)
        events.add(new RhythmixEventData("1", "temp", "35", new Timestamp(System.currentTimeMillis())));
        
        // Send humidity event (satisfies second condition)
        events.add(new RhythmixEventData("2", "humidity", "85", new Timestamp(System.currentTimeMillis() + 100)));

        boolean result = false;
        for (RhythmixEventData event : events) {
            result = rhythmixExecutor.execute(event);
        }

        // Second condition met, should return true
        Assertions.assertTrue(result);
    }

    // ==================== Three Source Tests ====================

    /**
     * Test three-source event expression with mixed operators.
     * Tests operator precedence: AND has higher precedence than OR.
     */
    @Test
    @DisplayName("Multi-source: Three sources with mixed operators (AND/OR)")
    void testThreeSourceMixed() throws TranslatorException {
        String code = "{#temp:<30# && #humidity:>80# || #pressure:>1000#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> events = new ArrayList<>();
        
        // Send pressure event (satisfies third condition)
        events.add(new RhythmixEventData("1", "pressure", "1050", new Timestamp(System.currentTimeMillis())));

        boolean result = rhythmixExecutor.execute(events.get(0));

        // Third condition met (pressure > 1000), should return true
        Assertions.assertTrue(result);
    }

    /**
     * Test three-source event expression where first two conditions are met.
     */
    @Test
    @DisplayName("Multi-source: Three sources - First two conditions met")
    void testThreeSourceFirstTwoMet() throws TranslatorException {
        String code = "{#temp:<30# && #humidity:>80# || #pressure:>1000#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> events = new ArrayList<>();
        
        // Send temperature event (satisfies first condition)
        events.add(new RhythmixEventData("1", "temp", "25", new Timestamp(System.currentTimeMillis())));
        
        // Send humidity event (satisfies second condition)
        events.add(new RhythmixEventData("2", "humidity", "85", new Timestamp(System.currentTimeMillis() + 100)));

        boolean result = false;
        for (RhythmixEventData event : events) {
            result = rhythmixExecutor.execute(event);
        }

        // First two conditions met (temp < 30 AND humidity > 80), should return true
        Assertions.assertTrue(result);
    }

    // ==================== Complex Condition Tests ====================

    /**
     * Test multi-source with range expression as condition.
     */
    @Test
    @DisplayName("Multi-source: Range expression as condition")
    void testComplexConditionRange() throws TranslatorException {
        String code = "{#temp:[20,30]# && #humidity:>80#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> events = new ArrayList<>();
        
        // Send temperature event (in range [20,30])
        events.add(new RhythmixEventData("1", "temp", "25", new Timestamp(System.currentTimeMillis())));
        
        // Send humidity event (satisfies condition)
        events.add(new RhythmixEventData("2", "humidity", "85", new Timestamp(System.currentTimeMillis() + 100)));

        boolean result = false;
        for (RhythmixEventData event : events) {
            result = rhythmixExecutor.execute(event);
        }

        // Both conditions met, should return true
        Assertions.assertTrue(result);
    }

    /**
     * Test multi-source with chain expression as condition.
     */
    @Test
    @DisplayName("Multi-source: Chain expression as condition")
    void testComplexConditionChain() throws TranslatorException {
        String code = "{#temp:>20 && <30# && #humidity:>=80#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> events = new ArrayList<>();
        
        // Send temperature event (satisfies chain: >20 && <30)
        events.add(new RhythmixEventData("1", "temp", "25", new Timestamp(System.currentTimeMillis())));
        
        // Send humidity event (satisfies condition >=80)
        events.add(new RhythmixEventData("2", "humidity", "80", new Timestamp(System.currentTimeMillis() + 100)));

        boolean result = false;
        for (RhythmixEventData event : events) {
            result = rhythmixExecutor.execute(event);
        }

        // Both conditions met, should return true
        Assertions.assertTrue(result);
    }

    // ==================== Event Order Independence Tests ====================

    /**
     * Test that event order doesn't matter - humidity then temp.
     */
    @Test
    @DisplayName("Multi-source: Event order independence - humidity first")
    void testEventOrderIndependenceHumidityFirst() throws TranslatorException {
        String code = "{#temp:<30# && #humidity:>80#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> events = new ArrayList<>();
        
        // Send humidity event FIRST
        events.add(new RhythmixEventData("1", "humidity", "85", new Timestamp(System.currentTimeMillis())));
        
        // Send temperature event SECOND
        events.add(new RhythmixEventData("2", "temp", "25", new Timestamp(System.currentTimeMillis() + 100)));

        boolean result = false;
        for (RhythmixEventData event : events) {
            result = rhythmixExecutor.execute(event);
        }

        // Both conditions met regardless of order, should return true
        Assertions.assertTrue(result);
    }

    /**
     * Test that event order doesn't matter - temp then humidity.
     */
    @Test
    @DisplayName("Multi-source: Event order independence - temp first")
    void testEventOrderIndependenceTempFirst() throws TranslatorException {
        String code = "{#temp:<30# && #humidity:>80#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> events = new ArrayList<>();
        
        // Send temperature event FIRST
        events.add(new RhythmixEventData("1", "temp", "25", new Timestamp(System.currentTimeMillis())));
        
        // Send humidity event SECOND
        events.add(new RhythmixEventData("2", "humidity", "85", new Timestamp(System.currentTimeMillis() + 100)));

        boolean result = false;
        for (RhythmixEventData event : events) {
            result = rhythmixExecutor.execute(event);
        }

        // Both conditions met regardless of order, should return true
        Assertions.assertTrue(result);
    }

    // ==================== Operator Precedence Tests ====================

    /**
     * Test operator precedence: AND has higher precedence than OR.
     * Expression: {#a:>1# || #b:>2# && #c:>3#}
     * Should parse as: a || (b && c)
     *
     * Test case: Only 'a' condition is met (a=5)
     * Expected: true (because a > 1 is true, and OR short-circuits)
     */
    @Test
    @DisplayName("Precedence: OR || AND - Left side true (short-circuit)")
    void testPrecedenceOrAndLeftTrue() throws TranslatorException {
        String code = "{#a:>1# || #b:>2# && #c:>3#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        // Send only event 'a' that satisfies condition (a > 1)
        RhythmixEventData event = new RhythmixEventData("1", "a", "5", new Timestamp(System.currentTimeMillis()));

        boolean result = rhythmixExecutor.execute(event);

        // a > 1 is true, so (a || (b && c)) = true (OR short-circuits)
        Assertions.assertTrue(result);
    }

    /**
     * Test operator precedence: AND has higher precedence than OR.
     * Expression: {#a:>1# || #b:>2# && #c:>3#}
     * Should parse as: a || (b && c)
     *
     * Test case: Only 'b' and 'c' conditions are met (a=0, b=5, c=5)
     * Expected: true (because (b && c) is true)
     */
    @Test
    @DisplayName("Precedence: OR || AND - Right side true")
    void testPrecedenceOrAndRightTrue() throws TranslatorException {
        String code = "{#a:>1# || #b:>2# && #c:>3#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> events = new ArrayList<>();

        // Send event 'a' that does NOT satisfy condition (a > 1)
        events.add(new RhythmixEventData("1", "a", "0", new Timestamp(System.currentTimeMillis())));

        // Send event 'b' that satisfies condition (b > 2)
        events.add(new RhythmixEventData("2", "b", "5", new Timestamp(System.currentTimeMillis() + 100)));

        // Send event 'c' that satisfies condition (c > 3)
        events.add(new RhythmixEventData("3", "c", "5", new Timestamp(System.currentTimeMillis() + 200)));

        boolean result = false;
        for (RhythmixEventData event : events) {
            result = rhythmixExecutor.execute(event);
        }

        // a > 1 is false, but (b > 2 && c > 3) is true, so (a || (b && c)) = false || true = true
        Assertions.assertTrue(result);
    }

    /**
     * Test operator precedence: AND has higher precedence than OR.
     * Expression: {#a:>1# || #b:>2# && #c:>3#}
     * Should parse as: a || (b && c)
     *
     * Test case: Only 'b' condition is met (a=0, b=5, c=1)
     * Expected: false (because a is false and (b && c) is false)
     */
    @Test
    @DisplayName("Precedence: OR || AND - Only middle condition true")
    void testPrecedenceOrAndOnlyMiddleTrue() throws TranslatorException {
        String code = "{#a:>1# || #b:>2# && #c:>3#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> events = new ArrayList<>();

        // Send event 'a' that does NOT satisfy condition (a > 1)
        events.add(new RhythmixEventData("1", "a", "0", new Timestamp(System.currentTimeMillis())));

        // Send event 'b' that satisfies condition (b > 2)
        events.add(new RhythmixEventData("2", "b", "5", new Timestamp(System.currentTimeMillis() + 100)));

        // Send event 'c' that does NOT satisfy condition (c > 3)
        events.add(new RhythmixEventData("3", "c", "1", new Timestamp(System.currentTimeMillis() + 200)));

        boolean result = false;
        for (RhythmixEventData event : events) {
            result = rhythmixExecutor.execute(event);
        }

        // a > 1 is false, (b > 2 && c > 3) is false, so (a || (b && c)) = false || false = false
        Assertions.assertFalse(result);
    }

    /**
     * Test operator precedence: AND before OR (reversed order).
     * Expression: {#a:>1# && #b:>2# || #c:>3#}
     * Should parse as: (a && b) || c
     *
     * Test case: Only 'c' condition is met (a=0, b=0, c=5)
     * Expected: true (because c > 3 is true)
     */
    @Test
    @DisplayName("Precedence: AND && OR - Right side true")
    void testPrecedenceAndOrRightTrue() throws TranslatorException {
        String code = "{#a:>1# && #b:>2# || #c:>3#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> events = new ArrayList<>();

        // Send event 'a' that does NOT satisfy condition (a > 1)
        events.add(new RhythmixEventData("1", "a", "0", new Timestamp(System.currentTimeMillis())));

        // Send event 'b' that does NOT satisfy condition (b > 2)
        events.add(new RhythmixEventData("2", "b", "0", new Timestamp(System.currentTimeMillis() + 100)));

        // Send event 'c' that satisfies condition (c > 3)
        events.add(new RhythmixEventData("3", "c", "5", new Timestamp(System.currentTimeMillis() + 200)));

        boolean result = false;
        for (RhythmixEventData event : events) {
            result = rhythmixExecutor.execute(event);
        }

        // (a && b) is false, but c > 3 is true, so ((a && b) || c) = false || true = true
        Assertions.assertTrue(result);
    }

    /**
     * Test operator precedence with four sources.
     * Expression: {#a:>1# || #b:>2# && #c:>3# || #d:>4#}
     * Should parse as: a || (b && c) || d
     *
     * Test case: Only 'd' condition is met
     * Expected: true
     */
    @Test
    @DisplayName("Precedence: Complex mixed operators - Four sources")
    void testPrecedenceFourSources() throws TranslatorException {
        String code = "{#a:>1# || #b:>2# && #c:>3# || #d:>4#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> events = new ArrayList<>();

        events.add(new RhythmixEventData("1", "a", "0", new Timestamp(System.currentTimeMillis())));
        events.add(new RhythmixEventData("2", "b", "0", new Timestamp(System.currentTimeMillis() + 100)));
        events.add(new RhythmixEventData("3", "c", "0", new Timestamp(System.currentTimeMillis() + 200)));
        events.add(new RhythmixEventData("4", "d", "10", new Timestamp(System.currentTimeMillis() + 300)));

        boolean result = false;
        for (RhythmixEventData event : events) {
            result = rhythmixExecutor.execute(event);
        }

        // a || (b && c) || d = false || false || true = true
        Assertions.assertTrue(result);
    }

    // ==================== Single Source Tests ====================

    /**
     * Test single-source expression using multi-source syntax.
     */
    @Test
    @DisplayName("Multi-source: Single source edge case")
    void testSingleSource() throws TranslatorException {
        String code = "{#temp:<30#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        // Send temperature event
        RhythmixEventData event = new RhythmixEventData("1", "temp", "25", new Timestamp(System.currentTimeMillis()));
        
        boolean result = rhythmixExecutor.execute(event);

        // Condition met, should return true
        Assertions.assertTrue(result);
    }

    /**
     * Test single-source expression that doesn't meet condition.
     */
    @Test
    @DisplayName("Multi-source: Single source - condition not met")
    void testSingleSourceNotMet() throws TranslatorException {
        String code = "{#temp:<30#}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        // Send temperature event that doesn't meet condition
        RhythmixEventData event = new RhythmixEventData("1", "temp", "35", new Timestamp(System.currentTimeMillis()));
        
        boolean result = rhythmixExecutor.execute(event);

        // Condition not met, should return false
        Assertions.assertFalse(result);
    }
}

