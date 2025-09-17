/*
 * author: MFine
 * date: 2025-01-03 12:00:00
 * @LastEditTime: 2025-01-03 12:00:00
 * @LastEditors: MFine
 * @Description: 测试链式表达式的实际应用示例
 */
package com.df.rhythmix.integration;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.RhythmixCompiler;
import com.df.rhythmix.execute.RhythmixExecutor;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.util.RhythmixEventData;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * author MFine
 * version 1.0
 * date 2025/01/03 12:00
 * @description 测试链式表达式的实际应用示例
 * 基于README.md中'链式表达式'部分的内容生成测试用例
 * 包含整数和浮点数混合的测试场景，测试filter->limit->calculate->meet的完整链式流程
 **/
public class TestChainExpression {

    // ==================== 基础链式表达式测试 ====================

    /**
     * 测试基础链式表达式：filter->limit->take->sum->meet
     */
    @Test
    @DisplayName("基础链式表达式 - filter->limit->take->sum->meet")
    void testBasicChainExpression() throws TranslatorException {
        String code = "filter((-5,5)).limit(5).take(0,2).sum().meet(>1)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        // 添加一些数据，包括整数和浮点数
        dataSequence.add(Util.genEventData("data1", "3", new Timestamp(System.currentTimeMillis())));        // 整数3，在(-5,5)范围内 false
        dataSequence.add(Util.genEventData("data2", "2.5", new Timestamp(System.currentTimeMillis() + 100))); // 浮点数2.5，在范围内 true
        dataSequence.add(Util.genEventData("data3", "8", new Timestamp(System.currentTimeMillis() + 200)));   // 整数8，超出范围，被过滤 false
        dataSequence.add(Util.genEventData("data4", "1.5", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数1.5，在范围内  false
        dataSequence.add(Util.genEventData("data5", "-2", new Timestamp(System.currentTimeMillis() + 400)));  // 整数-2，在范围内 false

        boolean result = false;
        List<Boolean> results = new ArrayList<>();
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
            results.add(result);
        }

        // 过滤后：[100, 99.98, 100.02, 100.01, 99.99]，选取索引1-4：[99.98, 100.02, 100.01, 99.99]，平均值=100.0，100.0>99.98，应该返回true
        Assertions.assertArrayEquals(new Boolean[]{false,true,false,false,false}, results.toArray());
    }

    /**
     * 测试简单过滤和求和：filter->sum->meet
     */
    @Test
    @DisplayName("基础链式表达式 - 简单过滤和求和")
    void testSimpleFilterSumMeet() throws TranslatorException {
        String code = "filter(>0).sum().meet(>10)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "5.5", new Timestamp(System.currentTimeMillis())));      // 浮点数5.5 > 0
        dataSequence.add(Util.genEventData("data2", "-3", new Timestamp(System.currentTimeMillis() + 100))); // 整数-3 ≤ 0，被过滤
        dataSequence.add(Util.genEventData("data3", "8", new Timestamp(System.currentTimeMillis() + 200)));   // 整数8 > 0
        dataSequence.add(Util.genEventData("data4", "2.8", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数2.8 > 0

        boolean result = false;
        List<Boolean> results = new ArrayList<>();
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
            results.add(result);
        }

        // 过滤后：[100, 99.98, 100.02, 100.01, 99.99]，选取索引1-4：[99.98, 100.02, 100.01, 99.99]，平均值=100.0，100.0>99.98，应该返回true
        Assertions.assertArrayEquals(new Boolean[]{false,false,true,false}, results.toArray());
    }

    /**
     * 测试无过滤的链式表达式：limit->avg->meet
     */
    @Test
    @DisplayName("基础链式表达式 - 无过滤的平均值计算")
    void testNoFilterAverageMeet() throws TranslatorException {
        String code = "limit(3).avg().meet(>5)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "8.5", new Timestamp(System.currentTimeMillis())));      // 浮点数8.5
        dataSequence.add(Util.genEventData("data2", "6", new Timestamp(System.currentTimeMillis() + 100)));   // 整数6
        dataSequence.add(Util.genEventData("data3", "4.2", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数4.2
        dataSequence.add(Util.genEventData("data4", "10", new Timestamp(System.currentTimeMillis() + 300)));  // 整数10，超出limit

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 限制3个数据：[8.5, 6, 4.2]，平均值=(8.5+6+4.2)/3=6.23，6.23>5，应该返回true
        Assertions.assertTrue(result);
    }

    // ==================== 数据过滤测试 ====================

    /**
     * 测试复杂过滤条件：逻辑组合过滤
     */
    @Test
    @DisplayName("数据过滤 - 复杂逻辑组合过滤")
    void testComplexFilterLogic() throws TranslatorException {
        String code = "filter(((1,7]||>10)&&!=5).sum().meet(>15)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "3.5", new Timestamp(System.currentTimeMillis())));      // 浮点数3.5，在(1,7]且!=5，通过
        dataSequence.add(Util.genEventData("data2", "5", new Timestamp(System.currentTimeMillis() + 100)));   // 整数5，在(1,7]但==5，被过滤
        dataSequence.add(Util.genEventData("data3", "12", new Timestamp(System.currentTimeMillis() + 200)));  // 整数12，>10且!=5，通过 true
        dataSequence.add(Util.genEventData("data4", "1", new Timestamp(System.currentTimeMillis() + 300)));   // 整数1，不在(1,7]且不>10，被过滤
        dataSequence.add(Util.genEventData("data5", "6.8", new Timestamp(System.currentTimeMillis() + 400))); // 浮点数6.8，在(1,7]且!=5，通过

        boolean result = false;
        List<Boolean> results = new ArrayList<>();
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
            results.add(result);
        }

        // 过滤后：[100, 99.98, 100.02, 100.01, 99.99]，选取索引1-4：[99.98, 100.02, 100.01, 99.99]，平均值=100.0，100.0>99.98，应该返回true
        Assertions.assertArrayEquals(new Boolean[]{false,false,true,false,false}, results.toArray());
    }

    /**
     * 测试区间过滤：过滤在指定范围内的数据
     */
    @Test
    @DisplayName("数据过滤 - 区间过滤")
    void testRangeFilter() throws TranslatorException {
        String code = "filter([10.5,20.5]).count().meet(>=3)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "15", new Timestamp(System.currentTimeMillis())));        // 整数15，在[10.5,20.5]
        dataSequence.add(Util.genEventData("data2", "8.5", new Timestamp(System.currentTimeMillis() + 100))); // 浮点数8.5，不在范围
        dataSequence.add(Util.genEventData("data3", "18.7", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数18.7，在范围
        dataSequence.add(Util.genEventData("data4", "25", new Timestamp(System.currentTimeMillis() + 300)));   // 整数25，不在范围
        dataSequence.add(Util.genEventData("data5", "12.3", new Timestamp(System.currentTimeMillis() + 400))); // 浮点数12.3，在范围

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 过滤后的数据：[15, 18.7, 12.3]，计数=3，3>=3，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试负数过滤：过滤掉负数
     */
    @Test
    @DisplayName("数据过滤 - 负数过滤")
    void testNegativeNumberFilter() throws TranslatorException {
        String code = "filter(>=0).maxcalc().meet(>5)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "-3.5", new Timestamp(System.currentTimeMillis())));     // 浮点数-3.5，被过滤
        dataSequence.add(Util.genEventData("data2", "8", new Timestamp(System.currentTimeMillis() + 100)));   // 整数8，通过
        dataSequence.add(Util.genEventData("data3", "-1", new Timestamp(System.currentTimeMillis() + 200)));  // 整数-1，被过滤
        dataSequence.add(Util.genEventData("data4", "6.7", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数6.7，通过

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 过滤后的数据：[8, 6.7]，最大值=8，8>5，应该返回true
        Assertions.assertTrue(result);
    }

    // ==================== 数据限制和选取测试 ====================

    /**
     * 测试数据限制：limit限制数据数量
     */
    @Test
    @DisplayName("数据限制和选取 - limit限制数据数量")
    void testDataLimit() throws TranslatorException {
        String code = "limit(3).sum().meet(>10)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "5.5", new Timestamp(System.currentTimeMillis())));      // 浮点数5.5
        dataSequence.add(Util.genEventData("data2", "3", new Timestamp(System.currentTimeMillis() + 100)));   // 整数3
        dataSequence.add(Util.genEventData("data3", "4.2", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数4.2
        dataSequence.add(Util.genEventData("data4", "8", new Timestamp(System.currentTimeMillis() + 300)));   // 整数8，超出limit
        dataSequence.add(Util.genEventData("data5", "10", new Timestamp(System.currentTimeMillis() + 400)));  // 整数10，超出limit

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 限制前3个数据：[5.5, 3, 4.2]，求和=12.7，12.7>10，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试数据选取：take选取指定范围的数据
     */
    @Test
    @DisplayName("数据限制和选取 - take选取指定范围数据")
    void testDataTake() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "take(1,3).avg().meet(>5)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "2.5", new Timestamp(System.currentTimeMillis())));      // 索引0，不选取
        dataSequence.add(Util.genEventData("data2", "8", new Timestamp(System.currentTimeMillis() + 100)));   // 索引1，选取
        dataSequence.add(Util.genEventData("data3", "6.5", new Timestamp(System.currentTimeMillis() + 200))); // 索引2，选取

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 选取索引1-3的数据：[8, 6.5, 7.2]，平均值=(8+6.5+7.2)/3=7.23，7.23>5，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试组合选取：filter->take组合使用
     */
    @Test
    @DisplayName("数据限制和选取 - filter和take组合使用")
    void testFilterTakeCombination() throws TranslatorException {
        String code = "filter(>0).take(1,3).mincalc().meet(<5)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "-2", new Timestamp(System.currentTimeMillis())));       // 整数-2，被过滤
        dataSequence.add(Util.genEventData("data2", "8.5", new Timestamp(System.currentTimeMillis() + 100))); // 浮点数8.5，过滤后索引0，不选取
        dataSequence.add(Util.genEventData("data3", "3", new Timestamp(System.currentTimeMillis() + 200)));   // 整数3，过滤后索引1，选取
        dataSequence.add(Util.genEventData("data4", "6.2", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数6.2，过滤后索引2，选取
        dataSequence.add(Util.genEventData("data5", "4", new Timestamp(System.currentTimeMillis() + 400)));   // 整数4，过滤后索引3，选取
        dataSequence.add(Util.genEventData("data6", "7", new Timestamp(System.currentTimeMillis() + 500)));   // 整数7，过滤后索引4，不选取

        boolean result = false;
        for (RhythmixEventData data : dataSequence.subList(0,4)) {
            result = rhythmixExecutor.execute(data);
        }

        // 过滤后：[8.5, 3, 6.2, 4, 7]，选取索引1-3：[3, 6.2, 4]，最小值=3，3<5，应该返回true
        Assertions.assertTrue(result);
        for (RhythmixEventData data : dataSequence.subList(4,dataSequence.size())) {
            result = rhythmixExecutor.execute(data);
        }

        Assertions.assertFalse(result);
    }

    // ==================== 数据计算测试 ====================

    /**
     * 测试求和计算：sum()函数
     */
    @Test
    @DisplayName("数据计算 - 求和计算")
    void testSumCalculation() throws TranslatorException {
        String code = "filter(>0).sum().meet([10,20])";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "3.5", new Timestamp(System.currentTimeMillis())));      // 浮点数3.5
        dataSequence.add(Util.genEventData("data2", "-1", new Timestamp(System.currentTimeMillis() + 100))); // 整数-1，被过滤
        dataSequence.add(Util.genEventData("data3", "5", new Timestamp(System.currentTimeMillis() + 200)));   // 整数5
        dataSequence.add(Util.genEventData("data4", "2.8", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数2.8

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 过滤后的数据：[3.5, 5, 2.8]，求和=11.3，11.3在[10,20]，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试平均值计算：avg()函数
     */
    @Test
    @DisplayName("数据计算 - 平均值计算")
    void testAverageCalculation() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter([1,10]).window(3).avg().meet(>5)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "8.5", new Timestamp(System.currentTimeMillis())));      // 浮点数8.5，在[1,10]
        dataSequence.add(Util.genEventData("data2", "15", new Timestamp(System.currentTimeMillis() + 100))); // 整数15，不在[1,10]
        dataSequence.add(Util.genEventData("data3", "6", new Timestamp(System.currentTimeMillis() + 200)));   // 整数6，在[1,10]
        dataSequence.add(Util.genEventData("data4", "3.2", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数3.2，在[1,10]

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 过滤后的数据：[8.5, 6, 3.2]，平均值=(8.5+6+3.2)/3=5.9，5.9>5，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试最大值计算：max()函数
     */
    @Test
    @DisplayName("数据计算 - 最大值计算")
    void testMaxCalculation() throws TranslatorException {
        String code = "filter(!=0).maxcalc().meet(>=8)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "5.5", new Timestamp(System.currentTimeMillis())));      // 浮点数5.5，!=0
        dataSequence.add(Util.genEventData("data2", "0", new Timestamp(System.currentTimeMillis() + 100)));   // 整数0，被过滤
        dataSequence.add(Util.genEventData("data3", "8", new Timestamp(System.currentTimeMillis() + 200)));   // 整数8，!=0

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 过滤后的数据：[5.5, 8, 3.7]，最大值=8，8>=8，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试最小值计算：min()函数
     */
    @Test
    @DisplayName("数据计算 - 最小值计算")
    void testMinCalculation() throws TranslatorException {
        String code = "filter(>0).mincalc().meet(<=3)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "5.8", new Timestamp(System.currentTimeMillis())));      // 浮点数5.8，>0
        dataSequence.add(Util.genEventData("data2", "-2", new Timestamp(System.currentTimeMillis() + 100))); // 整数-2，被过滤
        dataSequence.add(Util.genEventData("data3", "2", new Timestamp(System.currentTimeMillis() + 200)));   // 整数2，>0

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 过滤后的数据：[5.8, 2, 7.3]，最小值=2，2<=3，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试计数计算：count()函数
     */
    @Test
    @DisplayName("数据计算 - 计数计算")
    void testCountCalculation() throws TranslatorException {
        String code = "filter((5,15)).count().meet(>=3)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "8.5", new Timestamp(System.currentTimeMillis())));      // 浮点数8.5，在(5,15)
        dataSequence.add(Util.genEventData("data2", "20", new Timestamp(System.currentTimeMillis() + 100))); // 整数20，不在(5,15)
        dataSequence.add(Util.genEventData("data3", "10", new Timestamp(System.currentTimeMillis() + 200))); // 整数10，在(5,15)
        dataSequence.add(Util.genEventData("data4", "3", new Timestamp(System.currentTimeMillis() + 300)));   // 整数3，不在(5,15)
        dataSequence.add(Util.genEventData("data5", "12.7", new Timestamp(System.currentTimeMillis() + 400))); // 浮点数12.7，在(5,15)

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 过滤后的数据：[8.5, 10, 12.7]，计数=3，3>=3，应该返回true
        Assertions.assertTrue(result);
    }

    // ==================== 条件判断测试 ====================

    /**
     * 测试基础meet条件判断：大于判断
     */
    @Test
    @DisplayName("条件判断 - 基础meet大于判断")
    void testBasicMeetGreaterThan() throws TranslatorException {
        String code = "filter(>0).sum().meet(>100)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "45.5", new Timestamp(System.currentTimeMillis())));     // 浮点数45.5
        dataSequence.add(Util.genEventData("data2", "-10", new Timestamp(System.currentTimeMillis() + 100))); // 整数-10，被过滤
        dataSequence.add(Util.genEventData("data3", "60", new Timestamp(System.currentTimeMillis() + 200)));   // 整数60

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 过滤后的数据：[45.5, 60, 8.7]，求和=114.2，114.2>100，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试区间meet条件判断：范围判断
     */
    @Test
    @DisplayName("条件判断 - meet区间范围判断")
    void testMeetRangeCondition() throws TranslatorException {
        String code = "filter(!=0).avg().meet([5,15])";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "8.5", new Timestamp(System.currentTimeMillis())));      // 浮点数8.5
        dataSequence.add(Util.genEventData("data2", "0", new Timestamp(System.currentTimeMillis() + 100)));   // 整数0，被过滤
        dataSequence.add(Util.genEventData("data3", "12", new Timestamp(System.currentTimeMillis() + 200)));  // 整数12
        dataSequence.add(Util.genEventData("data4", "6.3", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数6.3

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 过滤后的数据：[8.5, 12, 6.3]，平均值=(8.5+12+6.3)/3=8.93，8.93在[5,15]，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试复杂meet条件判断：逻辑组合判断
     */
    @Test
    @DisplayName("条件判断 - meet复杂逻辑组合判断")
    void testMeetComplexLogicCondition() throws TranslatorException {
        String code = "filter(>0).maxcalc().meet(>10&&<20)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "15.5", new Timestamp(System.currentTimeMillis())));     // 浮点数15.5
        dataSequence.add(Util.genEventData("data2", "-5", new Timestamp(System.currentTimeMillis() + 100))); // 整数-5，被过滤
        dataSequence.add(Util.genEventData("data3", "8", new Timestamp(System.currentTimeMillis() + 200)));   // 整数8
        dataSequence.add(Util.genEventData("data4", "12.7", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数12.7

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 过滤后的数据：[15.5, 8, 12.7]，最大值=15.5，15.5>10且15.5<20，应该返回true
        Assertions.assertTrue(result);
    }

    // ==================== 实际应用场景测试 ====================

    /**
     * 测试温度监控场景：过滤异常值并计算平均温度
     */
    @Test
    @DisplayName("实际应用场景 - 温度监控系统")
    void testTemperatureMonitoringScenario() throws TranslatorException {
        String code = "filter((-100,100)).limit(5).avg().meet([20,30])";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> temperatureData = new ArrayList<>();
        temperatureData.add(Util.genEventData("temp1", "25.5", new Timestamp(System.currentTimeMillis())));      // 浮点数25.5，正常温度
        temperatureData.add(Util.genEventData("temp2", "150", new Timestamp(System.currentTimeMillis() + 100)));  // 整数150，异常值，被过滤
        temperatureData.add(Util.genEventData("temp3", "22", new Timestamp(System.currentTimeMillis() + 200)));   // 整数22，正常温度
        temperatureData.add(Util.genEventData("temp4", "28.3", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数28.3，正常温度
        temperatureData.add(Util.genEventData("temp5", "-200", new Timestamp(System.currentTimeMillis() + 400))); // 整数-200，异常值，被过滤
        temperatureData.add(Util.genEventData("temp6", "26", new Timestamp(System.currentTimeMillis() + 500)));   // 整数26，正常温度

        boolean result = false;
        for (RhythmixEventData data : temperatureData) {
            result = rhythmixExecutor.execute(data);
        }

        // 过滤后：[25.5, 22, 28.3, 26]，限制5个（实际4个），平均值=(25.5+22+28.3+26)/4=25.45，25.45在[20,30]，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试网络性能监控场景：响应时间分析
     */
    @Test
    @DisplayName("实际应用场景 - 网络性能监控")
    void testNetworkPerformanceMonitoringScenario() throws TranslatorException {
        String code = "filter([0,1000)).take(0,3).maxcalc().meet(<500)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> responseData = new ArrayList<>();
        responseData.add(Util.genEventData("response1", "150.5", new Timestamp(System.currentTimeMillis())));     // 浮点数150.5ms，正常
        responseData.add(Util.genEventData("response2", "2000", new Timestamp(System.currentTimeMillis() + 100))); // 整数2000ms，超时，被过滤
        responseData.add(Util.genEventData("response3", "300", new Timestamp(System.currentTimeMillis() + 200)));  // 整数300ms，正常
        responseData.add(Util.genEventData("response4", "450.8", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数450.8ms，正常

        boolean result = false;
        for (RhythmixEventData data : responseData) {
            result = rhythmixExecutor.execute(data);
        }

        // 过滤后：[150.5, 300, 450.8, 200]，取前3个：[150.5, 300, 450.8]，最大值=450.8，450.8<500，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试生产质量控制场景：产品重量分析
     */
    @Test
    @DisplayName("实际应用场景 - 生产质量控制")
    void testProductionQualityControlScenario() throws TranslatorException {
        String code = "filter([95,105]).limit(10).sum().meet(>500)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> productData = new ArrayList<>();
        productData.add(Util.genEventData("product1", "98.5", new Timestamp(System.currentTimeMillis())));      // 浮点数98.5g，合格
        productData.add(Util.genEventData("product2", "110", new Timestamp(System.currentTimeMillis() + 100))); // 整数110g，不合格，被过滤
        productData.add(Util.genEventData("product3", "102", new Timestamp(System.currentTimeMillis() + 200))); // 整数102g，合格
        productData.add(Util.genEventData("product4", "97.8", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数97.8g，合格
        productData.add(Util.genEventData("product5", "100", new Timestamp(System.currentTimeMillis() + 400))); // 整数100g，合格
        productData.add(Util.genEventData("product6", "99.2", new Timestamp(System.currentTimeMillis() + 500))); // 浮点数99.2g，合格
        productData.add(Util.genEventData("product7", "103.5", new Timestamp(System.currentTimeMillis() + 600))); // 浮点数103.5g，合格

        boolean result = false;
        for (RhythmixEventData data : productData) {
            result = rhythmixExecutor.execute(data);
        }

        // 过滤后：[98.5, 102, 97.8, 100, 99.2, 103.5]，求和=600.0，600.0>500，应该返回true
        Assertions.assertTrue(result);
    }

    // ==================== 混合数据类型高级测试 ====================

    /**
     * 测试高精度浮点数与整数混合的链式表达式
     */
    @Test
    @DisplayName("混合数据类型 - 高精度浮点数与整数混合链式表达式")
    void testHighPrecisionMixedChainExpression() throws TranslatorException {
        String code = "filter([99.95,100.05]).take(1,4).avg().meet(>99.98)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> precisionData = new ArrayList<>();
        precisionData.add(Util.genEventData("precision1", "99.90", new Timestamp(System.currentTimeMillis())));     // 浮点数99.90，不在范围，被过滤
        precisionData.add(Util.genEventData("precision2", "100", new Timestamp(System.currentTimeMillis() + 100)));   // 整数100，在范围，索引0
        precisionData.add(Util.genEventData("precision3", "99.98", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数99.98，在范围，索引1，选取
        precisionData.add(Util.genEventData("precision4", "100.02", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数100.02，在范围，索引2，选取
        precisionData.add(Util.genEventData("precision5", "100.01", new Timestamp(System.currentTimeMillis() + 400))); // 浮点数100.01，在范围，索引3，选取
        precisionData.add(Util.genEventData("precision6", "99.99", new Timestamp(System.currentTimeMillis() + 500))); // 浮点数99.99，在范围，索引4，选取

        boolean result = false;
        List<Boolean> results = new ArrayList<>();
        for (RhythmixEventData data : precisionData) {
            result = rhythmixExecutor.execute(data);
            results.add(result);
        }

        // 过滤后：[100, 99.98, 100.02, 100.01, 99.99]，选取索引1-4：[99.98, 100.02, 100.01, 99.99]，平均值=100.0，100.0>99.98，应该返回true
        Assertions.assertArrayEquals(new Boolean[]{false,false,false,false,true,false}, results.toArray());
    }

    /**
     * 测试边界值混合：整数和浮点数边界测试的链式表达式
     */
    @Test
    @DisplayName("混合数据类型 - 边界值混合链式表达式")
    void testBoundaryValueMixedChainExpression() throws TranslatorException {
        String code = "filter(>25.5).limit(4).mincalc().meet(<=30)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> boundaryData = new ArrayList<>();
        boundaryData.add(Util.genEventData("boundary1", "25.5", new Timestamp(System.currentTimeMillis())));      // 浮点数25.5，==25.5，被过滤
        boundaryData.add(Util.genEventData("boundary2", "26", new Timestamp(System.currentTimeMillis() + 100)));   // 整数26，>25.5，通过
        boundaryData.add(Util.genEventData("boundary3", "25.6", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数25.6，>25.5，通过
        boundaryData.add(Util.genEventData("boundary4", "25", new Timestamp(System.currentTimeMillis() + 300)));   // 整数25，<25.5，被过滤
        boundaryData.add(Util.genEventData("boundary5", "30.1", new Timestamp(System.currentTimeMillis() + 400))); // 浮点数30.1，>25.5，通过
        boundaryData.add(Util.genEventData("boundary6", "28.7", new Timestamp(System.currentTimeMillis() + 500))); // 浮点数28.7，>25.5，通过

        boolean result = false;
        List<Boolean> results = new ArrayList<>();
        for (RhythmixEventData data : boundaryData) {
            result = rhythmixExecutor.execute(data);
            results.add(result);
        }

        // 过滤后：[100, 99.98, 100.02, 100.01, 99.99]，选取索引1-4：[99.98, 100.02, 100.01, 99.99]，平均值=100.0，100.0>99.98，应该返回true
        Assertions.assertArrayEquals(new Boolean[]{false,true,true,false,false,true}, results.toArray());
    }

    /**
     * 测试复杂混合场景：多步骤链式表达式
     */
    @Test
    @DisplayName("混合数据类型 - 复杂多步骤链式表达式")
    void testComplexMultiStepChainExpression() throws TranslatorException {
        String code = "filter((-100,100)).limit(8).take(2,6).sum().meet([50,200])";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> complexData = new ArrayList<>();
        complexData.add(Util.genEventData("complex1", "150", new Timestamp(System.currentTimeMillis())));       // 整数150，超出范围，被过滤 false
        complexData.add(Util.genEventData("complex2", "15.5", new Timestamp(System.currentTimeMillis() + 100))); // 浮点数15.5，在范围，索引0 false
        complexData.add(Util.genEventData("complex3", "25", new Timestamp(System.currentTimeMillis() + 200)));   // 整数25，在范围，索引1 false
        complexData.add(Util.genEventData("complex4", "8.7", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数8.7，在范围，索引2，选取 false
        complexData.add(Util.genEventData("complex5", "35", new Timestamp(System.currentTimeMillis() + 400)));   // 整数35，在范围，索引3，选取 false
        complexData.add(Util.genEventData("complex6", "12.3", new Timestamp(System.currentTimeMillis() + 500))); // 浮点数12.3，在范围，索引4，选取 false
        complexData.add(Util.genEventData("complex7", "28", new Timestamp(System.currentTimeMillis() + 600)));   // 整数28，在范围，索引5，选取 true
        complexData.add(Util.genEventData("complex8", "45.8", new Timestamp(System.currentTimeMillis() + 700))); // 浮点数45.8，在范围，索引6，选取 false
        complexData.add(Util.genEventData("complex9", "18", new Timestamp(System.currentTimeMillis() + 800)));   // 整数18，在范围，索引7 false

        boolean result = false;
        List<Boolean> results = new ArrayList<>();
        for (RhythmixEventData data : complexData) {
            result = rhythmixExecutor.execute(data);
            results.add(result);
        }

        // 过滤后：[100, 99.98, 100.02, 100.01, 99.99]，选取索引1-4：[99.98, 100.02, 100.01, 99.99]，平均值=100.0，100.0>99.98，应该返回true
        Assertions.assertArrayEquals(new Boolean[]{false,false,false,false,false,false,true,false,false}, results.toArray());
    }

}

