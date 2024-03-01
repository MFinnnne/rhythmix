package com.celi.ferrum.lib;

import cn.hutool.core.bean.BeanUtil;
import com.celi.ferrum.exception.ComputeException;
import com.celi.ferrum.exception.LexicalException;
import com.celi.ferrum.lexer.Lexer;
import com.celi.ferrum.lexer.Token;
import com.googlecode.aviator.annotation.Import;
import com.googlecode.aviator.annotation.ImportScope;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Import(ns = "calc", scopes = {ImportScope.Static})
public class AviatorMath {

    public static String sum(List<Object> values) throws ComputeException {
        if (values == null || values.isEmpty()) {
            return "0";
        }
        List<Token> tokens = EventUtil.event2ValueToken(values);
        if (tokens.stream().allMatch(Token::isInteger)) {
            return String.valueOf(tokens.stream().mapToInt(item -> Integer.parseInt(item.getValue())).sum());
        } else if (tokens.stream().anyMatch(Token::isFloat)) {
            return String.valueOf(tokens.stream().mapToDouble(item -> Double.parseDouble(item.getValue())).sum());
        } else {
            throw new ComputeException("该类型不能计算");
        }
    }

    public static String avg(List<Object> values) throws ComputeException {
        if (values == null || values.isEmpty()) {
            return "0";
        }
        String sum = AviatorMath.sum(values);
        return String.valueOf(Double.parseDouble(sum) / values.size());
    }


    public static String count(List<Object> values) throws ComputeException {
        return String.valueOf(values.size());
    }


    /**
     * @param values 测点集合
     * @return 标准差
     * @throws ComputeException 计算异常
     */
    public static String stddev(List<Object> values) throws ComputeException {
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
        return String.format("%.3f", Math.sqrt(variance));
    }
}
