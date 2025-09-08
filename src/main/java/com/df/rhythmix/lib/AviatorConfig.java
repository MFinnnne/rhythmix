package com.df.rhythmix.lib;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.lexer.token.OperatorType;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Optimized Aviator configuration for type-agnostic comparison operations.
 * Provides enhanced performance through caching, early type detection, and optimized comparison logic.
 *
 * @author MFine
 * @version 2.0 (Optimized)
 * @date 2025/6/7 23:53
 **/
public class AviatorConfig {

    // Constants for comparison results
    private static final int EQUAL = 0;
    private static final int GREATER = 1;
    private static final int LESS = -1;

    // Optimized regex pattern with better performance characteristics

    // Pre-compiled boolean values for faster lookup
    private static final String TRUE_STR = "true";
    private static final String FALSE_STR = "false";

    // Cache for type detection results to avoid repeated parsing
    private static final ConcurrentHashMap<String, ValueType> TYPE_CACHE = new ConcurrentHashMap<>(256);
    private static final ConcurrentHashMap<String, Number> NUMERIC_CACHE = new ConcurrentHashMap<>(128);

    // Maximum cache size to prevent memory leaks
    private static final int MAX_CACHE_SIZE = 1000;

    /**
     * Enum representing the detected type of a value for optimized processing
     */
    private enum ValueType {
        INTEGER, DOUBLE, BOOLEAN_TRUE, BOOLEAN_FALSE, STRING, NULL
    }

    /**
     * Optimized comparison operation interface that avoids redundant compareValues calls
     */
    @FunctionalInterface
    private interface OptimizedComparator {
        boolean compare(int compareResult);
    }

    /**
     * Optimized comparison operations that work directly with comparison results
     */
    private enum ComparisonOp {
        GT(result -> result > 0),
        GTE(result -> result >= 0),
        LT(result -> result < 0),
        LTE(result -> result <= 0),
        EQ(result -> result == 0),
        NEQ(result -> result != 0);

        private final OptimizedComparator comparator;

        ComparisonOp(OptimizedComparator comparator) {
            this.comparator = comparator;
        }

        public boolean evaluate(int compareResult) {
            return comparator.compare(compareResult);
        }

        public boolean compare(Object left, Object right) {
            return comparator.compare(compareValues(left, right));
        }
    }

    public static void operatorOverloading() {
        // Register all comparison operators using the unified approach
        registerOperator(OperatorType.GT, ComparisonOp.GT);
        registerOperator(OperatorType.GE, ComparisonOp.GTE);
        registerOperator(OperatorType.LT, ComparisonOp.LT);
        registerOperator(OperatorType.LE, ComparisonOp.LTE);
        registerOperator(OperatorType.EQ, ComparisonOp.EQ);
        registerOperator(OperatorType.NEQ, ComparisonOp.NEQ);
    }

    /**
     * Unified operator registration method with optimized comparison logic
     */
    private static void registerOperator(OperatorType operatorType, ComparisonOp operation) {
        AviatorEvaluator.getInstance().addOpFunction(operatorType, new AbstractFunction() {
            @Override
            public String getName() {
                return operatorType.getToken();
            }

            @Override
            public AviatorObject call(Map<String, Object> map, AviatorObject left, AviatorObject right) {
                // If same type, use built-in comparison for optimal performance
                if (left.getAviatorType() == right.getAviatorType()) {
                    int compareResult = left.compare(right, map);
                    return AviatorBoolean.valueOf(operation.evaluate(compareResult));
                }
                Object leftValue = left.getValue(map);
               if(left.getValue(map) instanceof  String && isNumericFast(leftValue+"")){
                   if (detectValueType(leftValue+"")==ValueType.DOUBLE){
                       leftValue = Double.parseDouble(leftValue+"");
                   }
                   if (detectValueType(leftValue+"")==ValueType.INTEGER){
                       leftValue = Integer.parseInt(leftValue+"");
                   }
               }
                Object rightValue = right.getValue(map);
                int compareResult = compareValues(leftValue, rightValue);
                return AviatorBoolean.valueOf(operation.evaluate(compareResult));
            }
        });
    }

    /**
     * Optimized comparison method with caching and early type detection
     * @param leftValue left operand
     * @param rightValue right operand
     * @return comparison result: negative if left < right, 0 if equal, positive if left > right
     */
    private static int compareValues(Object leftValue, Object rightValue) {
        // Handle null values first (most common edge case)
        if (leftValue == null && rightValue == null) return EQUAL;
        if (leftValue == null) return LESS;
        if (rightValue == null) return GREATER;
        if (leftValue instanceof Number && rightValue instanceof Number) {
            return compareNumbers((Number) leftValue, (Number) rightValue);
        }

        // Convert to strings only when necessary
        String leftStr = String.valueOf(leftValue);
        String rightStr = String.valueOf(rightValue);

        // Get cached or detect types
        ValueType leftType = getValueType(leftStr);
        ValueType rightType = getValueType(rightStr);

        // Optimized comparison based on detected types
        return compareByTypes(leftStr, rightStr, leftType, rightType);
    }

    /**
     * Fast comparison for direct Number instances
     */
    private static int compareNumbers(Number left, Number right) {
        // Handle common integer cases first
        if (left instanceof Integer && right instanceof Integer) {
            return Integer.compare((Integer) left, (Integer) right);
        }
        if (left instanceof Long && right instanceof Long) {
            return Long.compare((Long) left, (Long) right);
        }

        // Fall back to double comparison for mixed or floating point numbers
        return Double.compare(left.doubleValue(), right.doubleValue());
    }

    /**
     * Optimized type-based comparison with minimal string operations
     */
    private static int compareByTypes(String leftStr, String rightStr, ValueType leftType, ValueType rightType) {
        // Same type comparisons (most common case)
        if (leftType == rightType) {
            switch (leftType) {
                case INTEGER:
                case DOUBLE:
                    return compareNumericStrings(leftStr, rightStr, leftType == ValueType.DOUBLE);
                case BOOLEAN_TRUE:
                case BOOLEAN_FALSE:
                    return EQUAL; // Same boolean values are equal
                case STRING:
                    return leftStr.compareTo(rightStr);
                case NULL:
                    return EQUAL;
            }
        }
        if (isNumericType(leftType)&&isNumericType(rightType)) {
            return compareNumericStrings(leftStr, rightStr, leftType == ValueType.DOUBLE);
        }

        // Mixed type comparisons with optimized logic
        return compareMixedTypes(leftStr, rightStr, leftType, rightType);
    }

    /**
     * Handle mixed type comparisons efficiently
     */
    private static int compareMixedTypes(String leftStr, String rightStr, ValueType leftType, ValueType rightType) {
        // Numeric vs Boolean
        if (isNumericType(leftType) && isBooleanType(rightType)) {
            Number leftNum = getCachedNumber(leftStr);
            int boolValue = (rightType == ValueType.BOOLEAN_TRUE) ? 1 : 0;
            return Double.compare(leftNum.doubleValue(), boolValue);
        }
        if (isBooleanType(leftType) && isNumericType(rightType)) {
            int boolValue = (leftType == ValueType.BOOLEAN_TRUE) ? 1 : 0;
            Number rightNum = getCachedNumber(rightStr);
            return Double.compare(boolValue, rightNum.doubleValue());
        }

        // Boolean vs String
        if (isBooleanType(leftType) && rightType == ValueType.STRING) {
            return (leftType == ValueType.BOOLEAN_TRUE) ? GREATER : LESS;
        }
        if (leftType == ValueType.STRING && isBooleanType(rightType)) {
            return (rightType == ValueType.BOOLEAN_TRUE) ? LESS : GREATER;
        }

        // Numeric vs String (numeric > string)
        if (isNumericType(leftType) && rightType == ValueType.STRING) {
            return GREATER;
        }
        if (leftType == ValueType.STRING && isNumericType(rightType)) {
            return LESS;
        }

        // Boolean comparison between different boolean types
        if (isBooleanType(leftType) && isBooleanType(rightType)) {
            boolean leftBool = (leftType == ValueType.BOOLEAN_TRUE);
            boolean rightBool = (rightType == ValueType.BOOLEAN_TRUE);
            return Boolean.compare(leftBool, rightBool);
        }

        // Default to string comparison
        return leftStr.compareTo(rightStr);
    }

    /**
     * Get or detect the type of a string value with caching
     */
    private static ValueType getValueType(String str) {
        if (str == null) return ValueType.NULL;

        // Check cache first
        ValueType cached = TYPE_CACHE.get(str);
        if (cached != null) {
            return cached;
        }

        // Detect type
        ValueType type = detectValueType(str);

        // Cache the result if cache isn't too large
        if (TYPE_CACHE.size() < MAX_CACHE_SIZE) {
            TYPE_CACHE.put(str, type);
        }

        return type;
    }

    /**
     * Fast type detection without regex for common cases
     * AviatorScript's original string comparison semantics
     */
    private static ValueType detectValueType(String str) {
        if (str.isEmpty()) return ValueType.STRING;

        // Fast boolean check
        if (str.length() <= 5) { // "false".length() == 5
            if (TRUE_STR.equalsIgnoreCase(str)) return ValueType.BOOLEAN_TRUE;
            if (FALSE_STR.equalsIgnoreCase(str)) return ValueType.BOOLEAN_FALSE;
        }

        // Fast numeric check for common patterns
        char first = str.charAt(0);
        if (first == '-' || (first >= '0' && first <= '9')) {
            if (isNumericFast(str)) {
                return str.contains(".") ? ValueType.DOUBLE : ValueType.INTEGER;
            }
        }

        return ValueType.STRING;
    }

    /**
     * Fast numeric detection without regex for performance
     */
    private static boolean isNumericFast(String str) {
        if (str.isEmpty()) return false;

        int start = 0;
        if (str.charAt(0) == '-') {
            if (str.length() == 1) return false;
            start = 1;
        }

        boolean hasDecimal = false;
        for (int i = start; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '.') {
                if (hasDecimal) return false; // Multiple decimals
                hasDecimal = true;
            } else if (c < '0' || c > '9') {
                return false;
            }
        }

        return true;
    }

    /**
     * Get cached numeric value or parse and cache it
     */
    private static Number getCachedNumber(String str) {
        Number cached = NUMERIC_CACHE.get(str);
        if (cached != null) {
            return cached;
        }

        try {
            Number number;
            if (str.contains(".")) {
                number = Double.parseDouble(str);
            } else {
                long longValue = Long.parseLong(str);
                // Use Integer for small values to save memory
                if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                    number = (int) longValue;
                } else {
                    number = longValue;
                }
            }

            // Cache if not too large
            if (NUMERIC_CACHE.size() < MAX_CACHE_SIZE) {
                NUMERIC_CACHE.put(str, number);
            }

            return number;
        } catch (NumberFormatException e) {
            // Return a sentinel value that will cause string comparison fallback
            return Double.NaN;
        }
    }

    /**
     * Optimized numeric string comparison with caching
     */
    private static int compareNumericStrings(String leftStr, String rightStr, boolean useDouble) {
        Number leftNum = getCachedNumber(leftStr);
        Number rightNum = getCachedNumber(rightStr);

        // Handle parsing failures
        if (Double.isNaN(leftNum.doubleValue()) || Double.isNaN(rightNum.doubleValue())) {
            return leftStr.compareTo(rightStr);
        }

        if (useDouble) {
            return Double.compare(leftNum.doubleValue(), rightNum.doubleValue());
        } else {
            return Long.compare(leftNum.longValue(), rightNum.longValue());
        }
    }

    // Helper methods for type checking
    private static boolean isNumericType(ValueType type) {
        return type == ValueType.INTEGER || type == ValueType.DOUBLE;
    }

    private static boolean isBooleanType(ValueType type) {
        return type == ValueType.BOOLEAN_TRUE || type == ValueType.BOOLEAN_FALSE;
    }

    /**
     * Clear caches to prevent memory leaks in long-running applications
     */
    public static void clearCaches() {
        TYPE_CACHE.clear();
        NUMERIC_CACHE.clear();
    }
}
