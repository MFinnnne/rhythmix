package com.df.rhythmix.lib;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.lexer.token.OperatorType;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.system.CompareFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author MFine
 * @version 1.0
 * @date 2025/6/7 23:53
 **/
public class AviatorConfig {
    
    // Pattern to match numeric strings (including integers and decimals)
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
    
    public static void operatorOverloading() {
        // Override GT (>) operator
        AviatorEvaluator.getInstance().addOpFunction(OperatorType.GT, new AbstractFunction() {
            @Override
            public String getName() {
                return OperatorType.GT.getToken();
            }

            @Override
            public AviatorObject call(Map<String, Object> map, AviatorObject aviatorObject, AviatorObject aviatorObject1) {
                final Object leftValue = aviatorObject.getValue(map);
                final Object rightValue = aviatorObject1.getValue(map);
                
                // If same type, use built-in comparison
                if (aviatorObject.getAviatorType() == aviatorObject1.getAviatorType()) {
                    return AviatorBoolean.valueOf(aviatorObject.compare(aviatorObject1, map) == 1);
                }
                
                // Type-agnostic comparison
                return AviatorBoolean.valueOf(compareIgnoreType(leftValue, rightValue));
            }
        });
        
        // Override GTE (>=) operator
        AviatorEvaluator.getInstance().addOpFunction(OperatorType.GE, new AbstractFunction() {
            @Override
            public String getName() {
                return OperatorType.GE.getToken();
            }

            @Override
            public AviatorObject call(Map<String, Object> map, AviatorObject aviatorObject, AviatorObject aviatorObject1) {
                final Object leftValue = aviatorObject.getValue(map);
                final Object rightValue = aviatorObject1.getValue(map);
                
                // If same type, use built-in comparison
                if (aviatorObject.getAviatorType() == aviatorObject1.getAviatorType()) {
                    int compareResult = aviatorObject.compare(aviatorObject1, map);
                    return AviatorBoolean.valueOf(compareResult >= 0);
                }
                
                // Type-agnostic comparison
                return AviatorBoolean.valueOf(compareIgnoreTypeGTE(leftValue, rightValue));
            }
        });
        
        // Override LT (<) operator
        AviatorEvaluator.getInstance().addOpFunction(OperatorType.LT, new AbstractFunction() {
            @Override
            public String getName() {
                return OperatorType.LT.getToken();
            }

            @Override
            public AviatorObject call(Map<String, Object> map, AviatorObject aviatorObject, AviatorObject aviatorObject1) {
                final Object leftValue = aviatorObject.getValue(map);
                final Object rightValue = aviatorObject1.getValue(map);
                
                // If same type, use built-in comparison
                if (aviatorObject.getAviatorType() == aviatorObject1.getAviatorType()) {
                    return AviatorBoolean.valueOf(aviatorObject.compare(aviatorObject1, map) < 0);
                }
                
                // Type-agnostic comparison - opposite of GT
                return AviatorBoolean.valueOf(!compareIgnoreType(leftValue, rightValue) && !compareIgnoreTypeEQ(leftValue, rightValue));
            }
        });
        
        // Override LTE (<=) operator
        AviatorEvaluator.getInstance().addOpFunction(OperatorType.LE, new AbstractFunction() {
            @Override
            public String getName() {
                return OperatorType.LE.getToken();
            }

            @Override
            public AviatorObject call(Map<String, Object> map, AviatorObject aviatorObject, AviatorObject aviatorObject1) {
                final Object leftValue = aviatorObject.getValue(map);
                final Object rightValue = aviatorObject1.getValue(map);
                
                // If same type, use built-in comparison
                if (aviatorObject.getAviatorType() == aviatorObject1.getAviatorType()) {
                    int compareResult = aviatorObject.compare(aviatorObject1, map);
                    return AviatorBoolean.valueOf(compareResult <= 0);
                }
                
                // Type-agnostic comparison - opposite of GT
                return AviatorBoolean.valueOf(!compareIgnoreType(leftValue, rightValue));
            }
        });
        
        // Override EQ (==) operator
        AviatorEvaluator.getInstance().addOpFunction(OperatorType.EQ, new AbstractFunction() {
            @Override
            public String getName() {
                return OperatorType.EQ.getToken();
            }

            @Override
            public AviatorObject call(Map<String, Object> map, AviatorObject aviatorObject, AviatorObject aviatorObject1) {
                final Object leftValue = aviatorObject.getValue(map);
                final Object rightValue = aviatorObject1.getValue(map);
                
                // If same type, use built-in comparison
                if (aviatorObject.getAviatorType() == aviatorObject1.getAviatorType()) {
                    int compareResult = aviatorObject.compare(aviatorObject1, map);
                    return AviatorBoolean.valueOf(compareResult == 0);
                }
                
                // Type-agnostic comparison
                return AviatorBoolean.valueOf(compareIgnoreTypeEQ(leftValue, rightValue));
            }
        });
        
        // Override NEQ (!=) operator
        AviatorEvaluator.getInstance().addOpFunction(OperatorType.NEQ, new AbstractFunction() {
            @Override
            public String getName() {
                return OperatorType.NEQ.getToken();
            }

            @Override
            public AviatorObject call(Map<String, Object> map, AviatorObject aviatorObject, AviatorObject aviatorObject1) {
                final Object leftValue = aviatorObject.getValue(map);
                final Object rightValue = aviatorObject1.getValue(map);
                
                // If same type, use built-in comparison
                if (aviatorObject.getAviatorType() == aviatorObject1.getAviatorType()) {
                    int compareResult = aviatorObject.compare(aviatorObject1, map);
                    return AviatorBoolean.valueOf(compareResult != 0);
                }
                
                // Type-agnostic comparison - opposite of EQ
                return AviatorBoolean.valueOf(!compareIgnoreTypeEQ(leftValue, rightValue));
            }
        });
    }
    
    /**
     * Compare two values ignoring their types by attempting intelligent type conversion
     * @param leftValue left operand
     * @param rightValue right operand
     * @return true if leftValue > rightValue, false otherwise
     */
    private static boolean compareIgnoreType(Object leftValue, Object rightValue) {
        if (leftValue == null && rightValue == null) {
            return false; // null == null, not greater
        }
        if (leftValue == null) {
            return false; // null is not greater than anything
        }
        if (rightValue == null) {
            return true; // anything is greater than null
        }
        
        String leftStr = String.valueOf(leftValue);
        String rightStr = String.valueOf(rightValue);
        
        // Try numeric comparison first
        if (isNumeric(leftStr) && isNumeric(rightStr)) {
            return compareAsNumbers(leftStr, rightStr);
        }
        
        // Try mixed numeric comparison (one side is numeric)
        if (isNumeric(leftStr) && !isNumeric(rightStr)) {
            // Try to parse right as number
            try {
                if (rightStr.equalsIgnoreCase("true")) {
                    return compareAsNumbers(leftStr, "1");
                } else if (rightStr.equalsIgnoreCase("false")) {
                    return compareAsNumbers(leftStr, "0");
                }
                // If right is not boolean and not numeric, numeric value is considered greater
                return true;
            } catch (Exception e) {
                return true; // numeric > non-numeric string
            }
        }
        
        if (!isNumeric(leftStr) && isNumeric(rightStr)) {
            // Try to parse left as number
            try {
                if (leftStr.equalsIgnoreCase("true")) {
                    return compareAsNumbers("1", rightStr);
                } else if (leftStr.equalsIgnoreCase("false")) {
                    return compareAsNumbers("0", rightStr);
                }
                // If left is not boolean and not numeric, numeric value is considered smaller
                return false;
            } catch (Exception e) {
                return false; // non-numeric string < numeric
            }
        }
        
        // Boolean comparison
        if (isBooleanString(leftStr) && isBooleanString(rightStr)) {
            boolean leftBool = Boolean.parseBoolean(leftStr);
            boolean rightBool = Boolean.parseBoolean(rightStr);
            return leftBool && !rightBool; // true > false
        }
        
        // Mixed boolean comparison
        if (isBooleanString(leftStr)) {
            boolean leftBool = Boolean.parseBoolean(leftStr);
            return leftBool; // true > anything non-boolean, false < anything non-boolean
        }
        
        if (isBooleanString(rightStr)) {
            boolean rightBool = Boolean.parseBoolean(rightStr);
            return !rightBool; // anything > false, anything < true
        }
        
        // String comparison as fallback
        return leftStr.compareTo(rightStr) > 0;
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
        return str != null && (str.equalsIgnoreCase("true") || str.equalsIgnoreCase("false"));
    }
    
    /**
     * Compare two numeric strings
     */
    private static boolean compareAsNumbers(String leftStr, String rightStr) {
        try {
            // Determine if we need floating point comparison
            if (leftStr.contains(".") || rightStr.contains(".")) {
                double leftDouble = Double.parseDouble(leftStr);
                double rightDouble = Double.parseDouble(rightStr);
                return leftDouble > rightDouble;
            } else {
                // Both are integers
                long leftLong = Long.parseLong(leftStr);
                long rightLong = Long.parseLong(rightStr);
                return leftLong > rightLong;
            }
        } catch (NumberFormatException e) {
            // Fallback to string comparison if parsing fails
            return leftStr.compareTo(rightStr) > 0;
        }
    }
    
    /**
     * Compare two values ignoring their types for >= operation
     * @param leftValue left operand
     * @param rightValue right operand
     * @return true if leftValue >= rightValue, false otherwise
     */
    private static boolean compareIgnoreTypeGTE(Object leftValue, Object rightValue) {
        return compareIgnoreType(leftValue, rightValue) || compareIgnoreTypeEQ(leftValue, rightValue);
    }
    
    /**
     * Compare two values ignoring their types for equality
     * @param leftValue left operand
     * @param rightValue right operand
     * @return true if leftValue == rightValue, false otherwise
     */
    private static boolean compareIgnoreTypeEQ(Object leftValue, Object rightValue) {
        if (leftValue == null && rightValue == null) {
            return true;
        }
        if (leftValue == null || rightValue == null) {
            return false;
        }
        
        String leftStr = String.valueOf(leftValue);
        String rightStr = String.valueOf(rightValue);
        
        // Try numeric comparison first
        if (isNumeric(leftStr) && isNumeric(rightStr)) {
            return compareAsNumbersEQ(leftStr, rightStr);
        }
        
        // Try mixed numeric comparison (one side is numeric)
        if (isNumeric(leftStr) && !isNumeric(rightStr)) {
            try {
                if (rightStr.equalsIgnoreCase("true")) {
                    return compareAsNumbersEQ(leftStr, "1");
                } else if (rightStr.equalsIgnoreCase("false")) {
                    return compareAsNumbersEQ(leftStr, "0");
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        
        if (!isNumeric(leftStr) && isNumeric(rightStr)) {
            try {
                if (leftStr.equalsIgnoreCase("true")) {
                    return compareAsNumbersEQ("1", rightStr);
                } else if (leftStr.equalsIgnoreCase("false")) {
                    return compareAsNumbersEQ("0", rightStr);
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        
        // Boolean comparison
        if (isBooleanString(leftStr) && isBooleanString(rightStr)) {
            boolean leftBool = Boolean.parseBoolean(leftStr);
            boolean rightBool = Boolean.parseBoolean(rightStr);
            return leftBool == rightBool;
        }
        
        // String comparison as fallback
        return leftStr.equals(rightStr);
    }
    
    /**
     * Compare two numeric strings for equality
     */
    private static boolean compareAsNumbersEQ(String leftStr, String rightStr) {
        try {
            // Determine if we need floating point comparison
            if (leftStr.contains(".") || rightStr.contains(".")) {
                double leftDouble = Double.parseDouble(leftStr);
                double rightDouble = Double.parseDouble(rightStr);
                return Double.compare(leftDouble, rightDouble) == 0;
            } else {
                // Both are integers
                long leftLong = Long.parseLong(leftStr);
                long rightLong = Long.parseLong(rightStr);
                return leftLong == rightLong;
            }
        } catch (NumberFormatException e) {
            // Fallback to string comparison if parsing fails
            return leftStr.equals(rightStr);
        }
    }
}
