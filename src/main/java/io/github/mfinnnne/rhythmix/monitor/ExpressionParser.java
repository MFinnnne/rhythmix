package io.github.mfinnnne.rhythmix.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing Rhythmix expressions to extract state flow information.
 * <p>
 * This class provides methods to parse different types of expressions and extract
 * state units and their positions.
 *
 * @author MFine
 * @version 1.0
 * @since 1.1.0
 */
public class ExpressionParser {

    /**
     * Pattern to match state units in arrow expressions.
     * Matches content within curly braces: {content}
     */
    private static final Pattern STATE_UNIT_PATTERN = Pattern.compile("\\{[^}]+\\}");

    /**
     * Pattern to match arrow operators.
     */
    private static final Pattern ARROW_PATTERN = Pattern.compile("->");

    /**
     * Parses an expression and creates StateFlowMetadata.
     *
     * @param expression the expression to parse
     * @return StateFlowMetadata containing parsed information
     */
    public static StateFlowMetadata parse(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return createEmptyMetadata(expression);
        }

        // Check if this is an arrow expression (contains ->)
        if (expression.contains("->")) {
            return parseArrowExpression(expression);
        }

        // For non-arrow expressions, treat the entire expression as a single state
        return parseSingleStateExpression(expression);
    }

    /**
     * Parses an arrow expression like "{a}->{b}->{c}".
     *
     * @param expression the arrow expression to parse
     * @return StateFlowMetadata with parsed state units
     */
    private static StateFlowMetadata parseArrowExpression(String expression) {
        List<String> stateUnits = new ArrayList<>();
        Map<Integer, String> statePositionMap = new HashMap<>();

        Matcher matcher = STATE_UNIT_PATTERN.matcher(expression);
        int position = 0;

        while (matcher.find()) {
            String stateUnit = matcher.group();
            stateUnits.add(stateUnit);
            statePositionMap.put(position, stateUnit);
            position++;
        }

        return StateFlowMetadata.builder()
                .expression(expression)
                .stateUnits(stateUnits)
                .totalStates(stateUnits.size())
                .statePositionMap(statePositionMap)
                .build();
    }

    /**
     * Parses a single state expression (no arrows).
     *
     * @param expression the expression to parse
     * @return StateFlowMetadata with a single state unit
     */
    private static StateFlowMetadata parseSingleStateExpression(String expression) {
        List<String> stateUnits = new ArrayList<>();
        Map<Integer, String> statePositionMap = new HashMap<>();

        // Treat the entire expression as a single state unit
        stateUnits.add(expression);
        statePositionMap.put(0, expression);

        return StateFlowMetadata.builder()
                .expression(expression)
                .stateUnits(stateUnits)
                .totalStates(1)
                .statePositionMap(statePositionMap)
                .build();
    }

    /**
     * Creates empty metadata for null or empty expressions.
     *
     * @param expression the original expression
     * @return StateFlowMetadata with empty data
     */
    private static StateFlowMetadata createEmptyMetadata(String expression) {
        return StateFlowMetadata.builder()
                .expression(expression)
                .stateUnits(new ArrayList<>())
                .totalStates(0)
                .statePositionMap(new HashMap<>())
                .build();
    }

    /**
     * Extracts the number of state units in an expression.
     *
     * @param expression the expression to analyze
     * @return the number of state units
     */
    public static int countStateUnits(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return 0;
        }

        if (!expression.contains("->")) {
            return 1;
        }

        Matcher matcher = STATE_UNIT_PATTERN.matcher(expression);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Checks if an expression is an arrow expression.
     *
     * @param expression the expression to check
     * @return true if the expression contains arrow operators, false otherwise
     */
    public static boolean isArrowExpression(String expression) {
        return expression != null && expression.contains("->");
    }
}

