package io.github.mfinnnne.rhythmix.lib;
import io.github.mfinnnne.rhythmix.exception.ComputeException;
import io.github.mfinnnne.rhythmix.lexer.Token;
import com.googlecode.aviator.annotation.Import;
import com.googlecode.aviator.annotation.ImportScope;

import java.util.List;

/**
 * A utility class providing mathematical functions for use within Aviator expressions.
 * <p>
 * This class is imported as a static namespace 'calc', making its methods available
 * directly in Rhythmix expressions (e.g., {@code calc.sum(queue)}). It provides
 * common aggregate functions like sum, average, count, and standard deviation.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
@Import(ns = "calc", scopes = {ImportScope.Static})
public class AviatorMath {

    /**
     * Calculates the sum of a list of numeric values.
     * <p>
     * The method handles both integers and floating-point numbers. If the list contains
     * any floats, the result will be a double; otherwise, it will be an integer.
     *
     * @param values a {@link java.util.List} of objects holding event data with numeric values
     * @return a {@link java.lang.Number} representing the sum; 0 if the list is null or empty
     * @throws ComputeException if any value is not numeric
     */
    public static Number sum(List<Object> values) throws ComputeException {
        if (values == null || values.isEmpty()) {
            return 0;
        }
        List<Token> tokens = EventUtil.event2ValueToken(values);
        if (tokens.stream().allMatch(Token::isInteger)) {
            return tokens.stream().mapToInt(item -> Integer.parseInt(item.getValue())).sum();
        } else if (tokens.stream().anyMatch(Token::isFloat)) {
            return tokens.stream().mapToDouble(item -> Double.parseDouble(item.getValue())).sum();
        } else {
            throw new ComputeException("该类型不能计算");
        }
    }

    /**
     * Calculates the average of a list of numeric values.
     *
     * @param values a list of event objects with numeric values
     * @return the average value; 0 if the list is null or empty
     * @throws ComputeException if any value is not numeric
     */
    public static Number avg(List<Object> values) throws ComputeException {
        if (values == null || values.isEmpty()) {
            return 0;
        }
        Number sum = AviatorMath.sum(values);
        return sum.doubleValue() / values.size();
    }


    /**
     * Counts the number of elements in a list.
     *
     * @param values the list to count
     * @return the size of the list
     * @throws ComputeException if the input list is null
     */
    public static Number count(List<Object> values) throws ComputeException {
        return values.size();
    }


    /**
     * Calculates the standard deviation of a list of numeric values.
     *
     * @param values the list of data points
     * @return the standard deviation of the values
     * @throws ComputeException if fewer than two elements are provided
     */
    public static Number stddev(List<Object> values) throws ComputeException {
        if (values.size() < 2) {
            throw new ComputeException("列表中至少需要两个数字来计算标准差");
        }
        List<Token> tokens = EventUtil.event2ValueToken(values);
        double[] numbers = tokens.stream().mapToDouble(item -> Double.parseDouble(item.getValue())).toArray();

        // 计算平均值
        double sum = 0.0;
        for (double num : numbers) {
            sum += num;
        }
        double mean = sum / numbers.length;

        // 计算方差
        double squaredDifferenceSum = 0.0;
        for (double num : numbers) {
            squaredDifferenceSum += Math.pow(num - mean, 2);
        }
        double variance = squaredDifferenceSum / numbers.length;
        return Math.sqrt(variance);
    }
}
