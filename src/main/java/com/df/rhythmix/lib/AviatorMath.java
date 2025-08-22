package com.df.rhythmix.lib;

import cn.hutool.core.bean.BeanUtil;
import com.df.rhythmix.exception.ComputeException;
import com.df.rhythmix.lexer.Token;
import com.googlecode.aviator.annotation.Import;
import com.googlecode.aviator.annotation.ImportScope;

import java.util.List;

@Import(ns = "calc", scopes = {ImportScope.Static})
public class AviatorMath {

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

    public static Number avg(List<Object> values) throws ComputeException {
        if (values == null || values.isEmpty()) {
            return 0;
        }
        Number sum = AviatorMath.sum(values);
        return sum.doubleValue() / values.size();
    }


    public static Number count(List<Object> values) throws ComputeException {
        return values.size();
    }


    /**
     * @param values 测点集合
     * @return 标准差
     * @throws ComputeException 计算异常
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
