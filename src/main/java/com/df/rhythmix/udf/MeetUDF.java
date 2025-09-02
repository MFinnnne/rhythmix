package com.df.rhythmix.udf;

/**
 * Interface for Meet User-Defined Functions (MeetUDF).
 * <p>
 * MeetUDF allows users to define custom meet conditions that can be used
 * in chain expressions to determine if a calculated result meets specific criteria.
 * <p>
 * Meet functions are typically used at the end of chain expressions to evaluate
 * whether the final calculated value satisfies certain conditions.
 * <p>
 * Example usage in expressions:
 * <pre>
 * filter(>0).sum().customMeet()
 * filter(>0).avg().rangeMeet()
 * filter(>0).count().thresholdMeet()
 * </pre>
 * <p>
 * Implementation example:
 * <pre>
 * public class ThresholdMeetUDF implements MeetUDF {
 *     {@literal @}Override
 *     public String getName() {
 *         return "thresholdMeet";
 *     }
 *     
 *     {@literal @}Override
 *     public boolean meet(Object calculatedValue) {
 *         if (calculatedValue instanceof Number) {
 *             double value = ((Number) calculatedValue).doubleValue();
 *             return value >= 10.0; // Custom threshold logic
 *         }
 *         return false;
 *     }
 * }
 * </pre>
 *
 * @author MFine
 * @version 1.0
 */
public interface MeetUDF {

    /**
     * Get the name of this meet function.
     * This name will be used to identify and call the function in expressions.
     * <p>
     * The name should be unique across all registered MeetUDF implementations.
     * It's recommended to use descriptive names that indicate the meet condition,
     * such as "thresholdMeet", "rangeMeet", "customMeet", etc.
     *
     * @return the unique name of this meet function
     */
    String getName();

    /**
     * Evaluate whether the calculated value meets the defined condition.
     * <p>
     * This method receives the result from a calculation function (like sum(), avg(), count(), etc.)
     * and determines if it satisfies the custom meet condition.
     * <p>
     * The implementation should handle different data types appropriately:
     * <ul>
     * <li>Number types (Integer, Long, Double, Float, etc.)</li>
     * <li>String representations of numbers</li>
     * <li>Boolean values</li>
     * <li>Null values (should typically return false)</li>
     * </ul>
     *
     * @param calculatedValue the result from a calculation function that needs to be evaluated
     * @return true if the value meets the condition, false otherwise
     */
    boolean meet(Number calculatedValue);

    /**
     * Validate the calculated value before processing.
     * This is a helper method that can be overridden for custom validation logic.
     * <p>
     * Default implementation checks for null values.
     *
     * @param calculatedValue the value to validate
     * @return true if the value is valid for processing, false otherwise
     */
    default boolean isValidValue(Number calculatedValue) {
        return calculatedValue != null;
    }

    /**
     * Convert the calculated value to a numeric value for comparison.
     * This is a helper method that handles common type conversions.
     * <p>
     * Default implementation handles Number types and String representations.
     *
     * @param calculatedValue the value to convert
     * @return the numeric representation of the value
     * @throws NumberFormatException if the value cannot be converted to a number
     */
    default double toNumericValue(Object calculatedValue) throws NumberFormatException {
        if (calculatedValue instanceof Number) {
            return ((Number) calculatedValue).doubleValue();
        } else if (calculatedValue instanceof String) {
            return Double.parseDouble((String) calculatedValue);
        } else {
            throw new NumberFormatException("Cannot convert " + calculatedValue.getClass().getSimpleName() + " to numeric value");
        }
    }
}
