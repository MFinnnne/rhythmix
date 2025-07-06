package com.df.rhythmix.lib;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.lexer.token.OperatorType;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.function.BiFunction;

/**
 * @author MFine
 * @version 1.0
 * @date 2025/6/7 23:53
 **/
public class AviatorConfig {
    
    // Constants for comparison results
    private static final int EQUAL = 0;
    private static final int GREATER = 1;
    private static final int LESS = -1;
    
    // Pattern to match numeric strings (including integers and decimals)
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
    
    // Boolean string constants
    private static final String TRUE_STR = "true";
    private static final String FALSE_STR = "false";
    private static final String ONE_STR = "1";
    private static final String ZERO_STR = "0";
    
    /**
     * Enum for comparison operations to improve readability
     */
    private enum ComparisonOp {
        GT((left, right) -> compareValues(left, right) > 0),
        GTE((left, right) -> compareValues(left, right) >= 0),
        LT((left, right) -> compareValues(left, right) < 0),
        LTE((left, right) -> compareValues(left, right) <= 0),
        EQ((left, right) -> compareValues(left, right) == 0),
        NEQ((left, right) -> compareValues(left, right) != 0);
        
        private final BiFunction<Object, Object, Boolean> comparator;
        
        ComparisonOp(BiFunction<Object, Object, Boolean> comparator) {
            this.comparator = comparator;
        }
        
        public boolean compare(Object left, Object right) {
            return comparator.apply(left, right);
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
     * Unified operator registration method to eliminate code duplication
     */
    private static void registerOperator(OperatorType operatorType, ComparisonOp operation) {
        AviatorEvaluator.getInstance().addOpFunction(operatorType, new AbstractFunction() {
            @Override
            public String getName() {
                return operatorType.getToken();
            }

            @Override
            public AviatorObject call(Map<String, Object> map, AviatorObject left, AviatorObject right) {
                // If same type, use built-in comparison for better performance
                if (left.getAviatorType() == right.getAviatorType()) {
                    int compareResult = left.compare(right, map);
                    return AviatorBoolean.valueOf(evaluateBuiltinComparison(compareResult, operation));
                }
                
                // Type-agnostic comparison
                Object leftValue = left.getValue(map);
                Object rightValue = right.getValue(map);
                return AviatorBoolean.valueOf(operation.compare(leftValue, rightValue));
            }
        });
    }
    
    /**
     * Evaluate built-in comparison result based on operation type
     */
    private static boolean evaluateBuiltinComparison(int compareResult, ComparisonOp operation) {
        switch (operation) {
            case GT: return compareResult > 0;
            case GTE: return compareResult >= 0;
            case LT: return compareResult < 0;
            case LTE: return compareResult <= 0;
            case EQ: return compareResult == 0;
            case NEQ: return compareResult != 0;
            default: return false;
        }
    }
    
    /**
     * Unified comparison method for all types
     * @param leftValue left operand
     * @param rightValue right operand
     * @return comparison result: negative if left < right, 0 if equal, positive if left > right
     */
    private static int compareValues(Object leftValue, Object rightValue) {
        // Handle null values
        if (leftValue == null && rightValue == null) return EQUAL;
        if (leftValue == null) return LESS;
        if (rightValue == null) return GREATER;
        
        String leftStr = String.valueOf(leftValue);
        String rightStr = String.valueOf(rightValue);
        
        // Try numeric comparison first
        if (isNumeric(leftStr) && isNumeric(rightStr)) {
            return compareAsNumbers(leftStr, rightStr);
        }
        
        // Handle mixed numeric/boolean comparisons
        Integer mixedResult = handleMixedComparison(leftStr, rightStr);
        if (mixedResult != null) {
            return mixedResult;
        }
        
        // Handle boolean comparisons
        if (isBooleanString(leftStr) && isBooleanString(rightStr)) {
            return compareBooleans(leftStr, rightStr);
        }
        
        // Handle mixed boolean comparisons
        Integer booleanMixedResult = handleMixedBooleanComparison(leftStr, rightStr);
        if (booleanMixedResult != null) {
            return booleanMixedResult;
        }
        
        // String comparison as fallback
        return leftStr.compareTo(rightStr);
    }
    
    /**
     * Handle mixed numeric/non-numeric comparisons
     */
    private static Integer handleMixedComparison(String leftStr, String rightStr) {
        if (isNumeric(leftStr) && !isNumeric(rightStr)) {
            if (rightStr.equalsIgnoreCase(TRUE_STR)) {
                return compareAsNumbers(leftStr, ONE_STR);
            } else if (rightStr.equalsIgnoreCase(FALSE_STR)) {
                return compareAsNumbers(leftStr, ZERO_STR);
            }
            return GREATER; // numeric > non-numeric string
        }
        
        if (!isNumeric(leftStr) && isNumeric(rightStr)) {
            if (leftStr.equalsIgnoreCase(TRUE_STR)) {
                return compareAsNumbers(ONE_STR, rightStr);
            } else if (leftStr.equalsIgnoreCase(FALSE_STR)) {
                return compareAsNumbers(ZERO_STR, rightStr);
            }
            return LESS; // non-numeric string < numeric
        }
        
        return null; // No mixed comparison applicable
    }
    
    /**
     * Handle mixed boolean/non-boolean comparisons
     */
    private static Integer handleMixedBooleanComparison(String leftStr, String rightStr) {
        if (isBooleanString(leftStr) && !isBooleanString(rightStr)) {
            boolean leftBool = Boolean.parseBoolean(leftStr);
            return leftBool ? GREATER : LESS;
        }
        
        if (!isBooleanString(leftStr) && isBooleanString(rightStr)) {
            boolean rightBool = Boolean.parseBoolean(rightStr);
            return rightBool ? LESS : GREATER;
        }
        
        return null; // No mixed boolean comparison applicable
    }
    
    /**
     * Compare two boolean strings
     */
    private static int compareBooleans(String leftStr, String rightStr) {
        boolean leftBool = Boolean.parseBoolean(leftStr);
        boolean rightBool = Boolean.parseBoolean(rightStr);
        
        if (leftBool == rightBool) return EQUAL;
        return leftBool ? GREATER : LESS; // true > false
    }
    
    /**
     * Check if a string represents a numeric value
     */
    private static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        return NUMERIC_PATTERN.matcher(str.trim()).matches();
    }
    
    /**
     * Check if a string represents a boolean value
     */
    private static boolean isBooleanString(String str) {
        return str != null && (str.equalsIgnoreCase(TRUE_STR) || str.equalsIgnoreCase(FALSE_STR));
    }
    
    /**
     * Compare two numeric strings
     * @return comparison result: negative if left < right, 0 if equal, positive if left > right
     */
    private static int compareAsNumbers(String leftStr, String rightStr) {
        try {
            // Determine if we need floating point comparison
            if (leftStr.contains(".") || rightStr.contains(".")) {
                double leftDouble = Double.parseDouble(leftStr);
                double rightDouble = Double.parseDouble(rightStr);
                return Double.compare(leftDouble, rightDouble);
            } else {
                // Both are integers
                long leftLong = Long.parseLong(leftStr);
                long rightLong = Long.parseLong(rightStr);
                return Long.compare(leftLong, rightLong);
            }
        } catch (NumberFormatException e) {
            // Fallback to string comparison if parsing fails
            return leftStr.compareTo(rightStr);
        }
    }
}
