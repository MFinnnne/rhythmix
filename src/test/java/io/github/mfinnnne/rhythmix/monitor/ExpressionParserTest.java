package io.github.mfinnnne.rhythmix.monitor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ExpressionParser.
 *
 * @author MFine
 * @version 1.0
 * @since 1.1.0
 */
@DisplayName("ExpressionParser Tests")
class ExpressionParserTest {

    @Test
    @DisplayName("Test parsing simple arrow expression")
    void testParseSimpleArrowExpression() {
        String expression = "{>5}->{<3}";
        StateFlowMetadata metadata = ExpressionParser.parse(expression);

        assertNotNull(metadata);
        assertEquals(expression, metadata.getExpression());
        assertEquals(2, metadata.getTotalStates());
        assertEquals("{>5}", metadata.getStateUnitAtPosition(0));
        assertEquals("{<3}", metadata.getStateUnitAtPosition(1));
    }

    @Test
    @DisplayName("Test parsing three-state arrow expression")
    void testParseThreeStateArrowExpression() {
        String expression = "{>10}->{<5}->{==3}";
        StateFlowMetadata metadata = ExpressionParser.parse(expression);

        assertEquals(3, metadata.getTotalStates());
        assertEquals("{>10}", metadata.getStateUnitAtPosition(0));
        assertEquals("{<5}", metadata.getStateUnitAtPosition(1));
        assertEquals("{==3}", metadata.getStateUnitAtPosition(2));
    }

    @Test
    @DisplayName("Test parsing complex state units")
    void testParseComplexStateUnits() {
        String expression = "{count(>4,3)}->{==3}";
        StateFlowMetadata metadata = ExpressionParser.parse(expression);

        assertEquals(2, metadata.getTotalStates());
        assertEquals("{count(>4,3)}", metadata.getStateUnitAtPosition(0));
        assertEquals("{==3}", metadata.getStateUnitAtPosition(1));
    }

    @Test
    @DisplayName("Test parsing single state expression")
    void testParseSingleStateExpression() {
        String expression = ">5";
        StateFlowMetadata metadata = ExpressionParser.parse(expression);

        assertEquals(1, metadata.getTotalStates());
        assertEquals(expression, metadata.getExpression());
        assertEquals(expression, metadata.getStateUnitAtPosition(0));
    }

    @Test
    @DisplayName("Test parsing empty expression")
    void testParseEmptyExpression() {
        StateFlowMetadata metadata = ExpressionParser.parse("");

        assertEquals(0, metadata.getTotalStates());
        assertTrue(metadata.getStateUnits().isEmpty());
    }

    @Test
    @DisplayName("Test parsing null expression")
    void testParseNullExpression() {
        StateFlowMetadata metadata = ExpressionParser.parse(null);

        assertEquals(0, metadata.getTotalStates());
        assertTrue(metadata.getStateUnits().isEmpty());
    }

    @Test
    @DisplayName("Test countStateUnits method")
    void testCountStateUnits() {
        assertEquals(2, ExpressionParser.countStateUnits("{>5}->{<3}"));
        assertEquals(3, ExpressionParser.countStateUnits("{a}->{b}->{c}"));
        assertEquals(1, ExpressionParser.countStateUnits(">5"));
        assertEquals(0, ExpressionParser.countStateUnits(""));
        assertEquals(0, ExpressionParser.countStateUnits(null));
    }

    @Test
    @DisplayName("Test isArrowExpression method")
    void testIsArrowExpression() {
        assertTrue(ExpressionParser.isArrowExpression("{>5}->{<3}"));
        assertTrue(ExpressionParser.isArrowExpression("{a}->{b}->{c}"));
        assertFalse(ExpressionParser.isArrowExpression(">5"));
        assertFalse(ExpressionParser.isArrowExpression(""));
        assertFalse(ExpressionParser.isArrowExpression(null));
    }

    @Test
    @DisplayName("Test state position validation")
    void testStatePositionValidation() {
        String expression = "{>5}->{<3}";
        StateFlowMetadata metadata = ExpressionParser.parse(expression);

        assertTrue(metadata.isValidPosition(0));
        assertTrue(metadata.isValidPosition(1));
        assertFalse(metadata.isValidPosition(2));
        assertFalse(metadata.isValidPosition(-1));
        assertFalse(metadata.isValidPosition(null));
    }

    @Test
    @DisplayName("Test getStateUnitAtPosition with invalid positions")
    void testGetStateUnitAtInvalidPosition() {
        String expression = "{>5}->{<3}";
        StateFlowMetadata metadata = ExpressionParser.parse(expression);

        assertNull(metadata.getStateUnitAtPosition(-1));
        assertNull(metadata.getStateUnitAtPosition(2));
        assertNull(metadata.getStateUnitAtPosition(100));
        assertNull(metadata.getStateUnitAtPosition(null));
    }

    @Test
    @DisplayName("Test parsing expression with spaces")
    void testParseExpressionWithSpaces() {
        String expression = "{ > 5 } -> { < 3 }";
        StateFlowMetadata metadata = ExpressionParser.parse(expression);

        assertEquals(2, metadata.getTotalStates());
        assertEquals("{ > 5 }", metadata.getStateUnitAtPosition(0));
        assertEquals("{ < 3 }", metadata.getStateUnitAtPosition(1));
    }

    @Test
    @DisplayName("Test parsing expression with nested braces")
    void testParseExpressionWithNestedBraces() {
        String expression = "{count([10,20],3)}->{avg()>15}";
        StateFlowMetadata metadata = ExpressionParser.parse(expression);

        assertEquals(2, metadata.getTotalStates());
        // Note: The regex matches the outermost braces
        assertEquals("{count([10,20],3)}", metadata.getStateUnitAtPosition(0));
        assertEquals("{avg()>15}", metadata.getStateUnitAtPosition(1));
    }
}

