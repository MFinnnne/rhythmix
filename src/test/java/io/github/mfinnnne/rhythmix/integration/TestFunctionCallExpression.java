/*
 * author: MFine
 * date: 2025-01-03 11:30:00
 * @LastEditTime: 2025-01-03 11:30:00
 * @LastEditors: MFine
 * @Description: 测试函数调用表达式的实际应用示例
 */
package io.github.mfinnnne.rhythmix.integration;

import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.execute.RhythmixCompiler;
import io.github.mfinnnne.rhythmix.execute.RhythmixExecutor;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import io.github.mfinnnne.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * author MFine
 * version 1.0
 * date 2025/01/03 11:30
 * @description 测试函数调用表达式的实际应用示例
 * 基于README.md中'函数调用'部分的内容生成测试用例
 * 包含整数和浮点数混合的测试场景，重点测试count()和count!()函数
 **/
public class TestFunctionCallExpression {

    // ==================== count() 函数测试（非连续计数）====================

    /**
     * 测试count()函数基本功能：统计非连续满足条件的数据
     */
    @Test
    @DisplayName("count()函数 - 基本非连续计数功能")
    void testCountFunctionBasic() throws TranslatorException {
        String code = "count(>4, 3)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "5", new Timestamp(System.currentTimeMillis())));      // >4 ✓ (计数=1)
        dataSequence.add(Util.genEventData("data2", "2", new Timestamp(System.currentTimeMillis() + 100))); // ≤4 ✗ (计数保持=1)
        dataSequence.add(Util.genEventData("data3", "6", new Timestamp(System.currentTimeMillis() + 200))); // >4 ✓ (计数=2)
        dataSequence.add(Util.genEventData("data4", "1", new Timestamp(System.currentTimeMillis() + 300))); // ≤4 ✗ (计数保持=2)
        dataSequence.add(Util.genEventData("data5", "7", new Timestamp(System.currentTimeMillis() + 400))); // >4 ✓ (计数=3) → 返回 true

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 非连续满足3次条件，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试count()函数与浮点数：混合整数和浮点数
     */
    @Test
    @DisplayName("count()函数 - 混合整数和浮点数计数")
    void testCountFunctionWithMixedNumbers() throws TranslatorException {
        String code = "count(>4.5, 3)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "5", new Timestamp(System.currentTimeMillis())));        // 整数5 > 4.5 ✓ (计数=1)
        dataSequence.add(Util.genEventData("data2", "4.2", new Timestamp(System.currentTimeMillis() + 100))); // 浮点数4.2 ≤ 4.5 ✗ (计数保持=1)
        dataSequence.add(Util.genEventData("data3", "6.8", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数6.8 > 4.5 ✓ (计数=2)
        dataSequence.add(Util.genEventData("data4", "3", new Timestamp(System.currentTimeMillis() + 300)));   // 整数3 ≤ 4.5 ✗ (计数保持=2)
        dataSequence.add(Util.genEventData("data5", "7.1", new Timestamp(System.currentTimeMillis() + 400))); // 浮点数7.1 > 4.5 ✓ (计数=3) → 返回 true

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 混合数据类型非连续满足3次条件，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试count()函数与区间表达式：检测在范围内的数据
     */
    @Test
    @DisplayName("count()函数 - 与区间表达式结合")
    void testCountFunctionWithRangeExpression() throws TranslatorException {
        String code = "count([10.5,20.5], 4)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "15", new Timestamp(System.currentTimeMillis())));        // 整数15 在[10.5,20.5] ✓ (计数=1)
        dataSequence.add(Util.genEventData("data2", "25.3", new Timestamp(System.currentTimeMillis() + 100))); // 浮点数25.3 不在范围 ✗ (计数保持=1)
        dataSequence.add(Util.genEventData("data3", "12.8", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数12.8 在范围 ✓ (计数=2)
        dataSequence.add(Util.genEventData("data4", "8", new Timestamp(System.currentTimeMillis() + 300)));    // 整数8 不在范围 ✗ (计数保持=2)
        dataSequence.add(Util.genEventData("data5", "18.7", new Timestamp(System.currentTimeMillis() + 400))); // 浮点数18.7 在范围 ✓ (计数=3)
        dataSequence.add(Util.genEventData("data6", "20", new Timestamp(System.currentTimeMillis() + 500)));   // 整数20 在范围 ✓ (计数=4) → 返回 true

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 非连续满足4次区间条件，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试count()函数边界情况：刚好不满足计数要求
     */
    @Test
    @DisplayName("count()函数 - 边界情况：不满足计数要求")
    void testCountFunctionBoundaryCase() throws TranslatorException {
        String code = "count(>10.0, 3)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "15.5", new Timestamp(System.currentTimeMillis())));      // 浮点数15.5 > 10.0 ✓ (计数=1)
        dataSequence.add(Util.genEventData("data2", "8", new Timestamp(System.currentTimeMillis() + 100)));   // 整数8 ≤ 10.0 ✗ (计数保持=1)
        dataSequence.add(Util.genEventData("data3", "12.3", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数12.3 > 10.0 ✓ (计数=2)
        dataSequence.add(Util.genEventData("data4", "5.7", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数5.7 ≤ 10.0 ✗ (计数保持=2)

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 只满足2次条件，不足3次，应该返回false
        Assertions.assertFalse(result);
    }

    // ==================== count!() 函数测试（连续计数）====================

    /**
     * 测试count!()函数基本功能：统计连续满足条件的数据
     */
    @Test
    @DisplayName("count!()函数 - 基本连续计数功能")
    void testCountStrictFunctionBasic() throws TranslatorException {
        String code = "count!(>4, 3)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "5", new Timestamp(System.currentTimeMillis())));      // >4 ✓ (计数=1)
        dataSequence.add(Util.genEventData("data2", "2", new Timestamp(System.currentTimeMillis() + 100))); // ≤4 ✗ (计数重置=0)
        dataSequence.add(Util.genEventData("data3", "6", new Timestamp(System.currentTimeMillis() + 200))); // >4 ✓ (计数=1)
        dataSequence.add(Util.genEventData("data4", "1", new Timestamp(System.currentTimeMillis() + 300))); // ≤4 ✗ (计数重置=0)
        dataSequence.add(Util.genEventData("data5", "7", new Timestamp(System.currentTimeMillis() + 400))); // >4 ✓ (计数=1)
        dataSequence.add(Util.genEventData("data6", "8", new Timestamp(System.currentTimeMillis() + 500))); // >4 ✓ (计数=2)
        dataSequence.add(Util.genEventData("data7", "9", new Timestamp(System.currentTimeMillis() + 600))); // >4 ✓ (计数=3) → 返回 true

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 连续满足3次条件，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试count!()函数与浮点数：混合整数和浮点数的连续计数
     */
    @Test
    @DisplayName("count!()函数 - 混合整数和浮点数连续计数")
    void testCountStrictFunctionWithMixedNumbers() throws TranslatorException {
        String code = "count!(>10.5, 4)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "12", new Timestamp(System.currentTimeMillis())));        // 整数12 > 10.5 ✓ (计数=1)
        dataSequence.add(Util.genEventData("data2", "15.8", new Timestamp(System.currentTimeMillis() + 100))); // 浮点数15.8 > 10.5 ✓ (计数=2)
        dataSequence.add(Util.genEventData("data3", "11", new Timestamp(System.currentTimeMillis() + 200)));   // 整数11 > 10.5 ✓ (计数=3)
        dataSequence.add(Util.genEventData("data4", "13.2", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数13.2 > 10.5 ✓ (计数=4) → 返回 true

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 混合数据类型连续满足4次条件，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试count!()函数重置机制：不满足条件时计数器重置
     */
    @Test
    @DisplayName("count!()函数 - 计数器重置机制")
    void testCountStrictFunctionResetMechanism() throws TranslatorException {
        String code = "count!(<5.0, 3)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "3.5", new Timestamp(System.currentTimeMillis())));      // 浮点数3.5 < 5.0 ✓ (计数=1)
        dataSequence.add(Util.genEventData("data2", "4", new Timestamp(System.currentTimeMillis() + 100)));   // 整数4 < 5.0 ✓ (计数=2)
        dataSequence.add(Util.genEventData("data3", "6.2", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数6.2 ≥ 5.0 ✗ (计数重置=0)
        dataSequence.add(Util.genEventData("data4", "2.8", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数2.8 < 5.0 ✓ (计数=1)
        dataSequence.add(Util.genEventData("data5", "1", new Timestamp(System.currentTimeMillis() + 400)));   // 整数1 < 5.0 ✓ (计数=2)
        dataSequence.add(Util.genEventData("data6", "4.9", new Timestamp(System.currentTimeMillis() + 500))); // 浮点数4.9 < 5.0 ✓ (计数=3) → 返回 true

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 经过重置后连续满足3次条件，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试count!()函数与区间表达式：连续在范围内的数据
     */
    @Test
    @DisplayName("count!()函数 - 与区间表达式结合")
    void testCountStrictFunctionWithRangeExpression() throws TranslatorException {
        String code = "count!([10,20], 3)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "15.5", new Timestamp(System.currentTimeMillis())));      // 浮点数15.5 在[10,20] ✓ (计数=1)
        dataSequence.add(Util.genEventData("data2", "18", new Timestamp(System.currentTimeMillis() + 100)));   // 整数18 在[10,20] ✓ (计数=2)
        dataSequence.add(Util.genEventData("data3", "12.3", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数12.3 在[10,20] ✓ (计数=3) → 返回 true

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 连续3次都在范围内，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试count!()函数失败情况：连续性被打断
     */
    @Test
    @DisplayName("count!()函数 - 连续性被打断的情况")
    void testCountStrictFunctionInterrupted() throws TranslatorException {
        String code = "count!(>=50.0, 4)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> dataSequence = new ArrayList<>();
        dataSequence.add(Util.genEventData("data1", "55", new Timestamp(System.currentTimeMillis())));        // 整数55 ≥ 50.0 ✓ (计数=1)
        dataSequence.add(Util.genEventData("data2", "60.5", new Timestamp(System.currentTimeMillis() + 100))); // 浮点数60.5 ≥ 50.0 ✓ (计数=2)
        dataSequence.add(Util.genEventData("data3", "45.8", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数45.8 < 50.0 ✗ (计数重置=0)
        dataSequence.add(Util.genEventData("data4", "52", new Timestamp(System.currentTimeMillis() + 300)));   // 整数52 ≥ 50.0 ✓ (计数=1)
        dataSequence.add(Util.genEventData("data5", "58.3", new Timestamp(System.currentTimeMillis() + 400))); // 浮点数58.3 ≥ 50.0 ✓ (计数=2)

        boolean result = false;
        for (RhythmixEventData data : dataSequence) {
            result = rhythmixExecutor.execute(data);
        }

        // 连续性被打断，未达到4次，应该返回false
        Assertions.assertFalse(result);
    }

    // ==================== 实际应用场景测试 ====================

    /**
     * 测试监控场景：检测连续3次温度超标
     */
    @Test
    @DisplayName("实际应用场景 - 监控：连续3次温度超标")
    void testMonitoringConsecutiveTemperatureExceedance() throws TranslatorException {
        String code = "count!(>80, 3)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> temperatureData = new ArrayList<>();
        temperatureData.add(Util.genEventData("temp1", "75.5", new Timestamp(System.currentTimeMillis())));      // 浮点数75.5 ≤ 80 ✗ (计数=0)
        temperatureData.add(Util.genEventData("temp2", "85", new Timestamp(System.currentTimeMillis() + 100)));   // 整数85 > 80 ✓ (计数=1)
        temperatureData.add(Util.genEventData("temp3", "82.3", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数82.3 > 80 ✓ (计数=2)
        temperatureData.add(Util.genEventData("temp4", "88", new Timestamp(System.currentTimeMillis() + 300)));   // 整数88 > 80 ✓ (计数=3) → 返回 true

        boolean result = false;
        for (RhythmixEventData data : temperatureData) {
            result = rhythmixExecutor.execute(data);
        }

        // 连续3次温度超标，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试质量控制：检测连续5个产品合格
     */
    @Test
    @DisplayName("实际应用场景 - 质量控制：连续5个产品合格")
    void testQualityControlConsecutiveQualifiedProducts() throws TranslatorException {
        String code = "count!([95,100], 5)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> productData = new ArrayList<>();
        productData.add(Util.genEventData("product1", "97.5", new Timestamp(System.currentTimeMillis())));      // 浮点数97.5 在[95,100] ✓ (计数=1)
        productData.add(Util.genEventData("product2", "98", new Timestamp(System.currentTimeMillis() + 100)));   // 整数98 在[95,100] ✓ (计数=2)
        productData.add(Util.genEventData("product3", "96.8", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数96.8 在[95,100] ✓ (计数=3)
        productData.add(Util.genEventData("product4", "99", new Timestamp(System.currentTimeMillis() + 300)));   // 整数99 在[95,100] ✓ (计数=4)
        productData.add(Util.genEventData("product5", "95.2", new Timestamp(System.currentTimeMillis() + 400))); // 浮点数95.2 在[95,100] ✓ (计数=5) → 返回 true

        boolean result = false;
        for (RhythmixEventData data : productData) {
            result = rhythmixExecutor.execute(data);
        }

        // 连续5个产品合格，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试网络监控：检测连续2次响应时间过长
     */
    @Test
    @DisplayName("实际应用场景 - 网络监控：连续2次响应时间过长")
    void testNetworkMonitoringConsecutiveSlowResponse() throws TranslatorException {
        String code = "count!(>1000, 2)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> responseData = new ArrayList<>();
        responseData.add(Util.genEventData("response1", "800.5", new Timestamp(System.currentTimeMillis())));    // 浮点数800.5 ≤ 1000 ✗ (计数=0)
        responseData.add(Util.genEventData("response2", "1200", new Timestamp(System.currentTimeMillis() + 100))); // 整数1200 > 1000 ✓ (计数=1)
        responseData.add(Util.genEventData("response3", "1500.8", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数1500.8 > 1000 ✓ (计数=2) → 返回 true

        boolean result = false;
        for (RhythmixEventData data : responseData) {
            result = rhythmixExecutor.execute(data);
        }

        // 连续2次响应时间过长，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试金融风控：检测连续3笔交易都在正常范围内
     */
    @Test
    @DisplayName("实际应用场景 - 金融风控：连续3笔正常交易")
    void testFinancialRiskControlNormalTransactions() throws TranslatorException {
        String code = "count!([100,10000], 3)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> transactionData = new ArrayList<>();
        transactionData.add(Util.genEventData("transaction1", "2500.50", new Timestamp(System.currentTimeMillis())));    // 浮点数2500.50 在[100,10000] ✓ (计数=1)
        transactionData.add(Util.genEventData("transaction2", "5000", new Timestamp(System.currentTimeMillis() + 100))); // 整数5000 在[100,10000] ✓ (计数=2)
        transactionData.add(Util.genEventData("transaction3", "1200.75", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数1200.75 在[100,10000] ✓ (计数=3) → 返回 true

        boolean result = false;
        for (RhythmixEventData data : transactionData) {
            result = rhythmixExecutor.execute(data);
        }

        // 连续3笔交易都在正常范围内，应该返回true
        Assertions.assertTrue(result);
    }

    // ==================== 组合使用测试 ====================

    /**
     * 测试逻辑组合：连续异常或单次严重异常
     */
    @Test
    @DisplayName("组合使用 - 连续异常或单次严重异常")
    void testLogicalCombinationConsecutiveOrSevere() throws TranslatorException {
        String code = "count!(>50, 3) || count(>100, 1)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        // 测试单次严重异常情况
        List<RhythmixEventData> severeData = new ArrayList<>();
        severeData.add(Util.genEventData("data1", "45.5", new Timestamp(System.currentTimeMillis())));    // 浮点数45.5 ≤ 50且≤100 ✗
        severeData.add(Util.genEventData("data2", "120", new Timestamp(System.currentTimeMillis() + 100))); // 整数120 > 100 ✓ 单次严重异常

        boolean result1 = false;
        for (RhythmixEventData data : severeData) {
            result1 = rhythmixExecutor.execute(data);
        }

        // 单次严重异常，应该返回true
        Assertions.assertTrue(result1);

        // 重新创建executor测试连续异常情况
        RhythmixExecutor rhythmixExecutor2 = RhythmixCompiler.compile(code);
        List<RhythmixEventData> consecutiveData = new ArrayList<>();
        consecutiveData.add(Util.genEventData("data1", "60.5", new Timestamp(System.currentTimeMillis())));    // 浮点数60.5 > 50 ✓ (计数=1)
        consecutiveData.add(Util.genEventData("data2", "75", new Timestamp(System.currentTimeMillis() + 100))); // 整数75 > 50 ✓ (计数=2)
        consecutiveData.add(Util.genEventData("data3", "55.8", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数55.8 > 50 ✓ (计数=3) → 连续异常

        boolean result2 = false;
        for (RhythmixEventData data : consecutiveData) {
            result2 = rhythmixExecutor2.execute(data);
        }

        // 连续3次异常，应该返回true
        Assertions.assertTrue(result2);
    }

    /**
     * 测试复合条件：连续正常且无严重异常
     */
    @Test
    @DisplayName("组合使用 - 连续正常且无严重异常")
    void testLogicalCombinationNormalAndNoSevere() throws TranslatorException {
        String code = "count!([20,80], 5)  count(<100, 10)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> mixedData = new ArrayList<>();
        // 添加连续5个正常值
        mixedData.add(Util.genEventData("data1", "25.5", new Timestamp(System.currentTimeMillis())));      // 浮点数25.5 在[20,80] ✓
        mixedData.add(Util.genEventData("data2", "45", new Timestamp(System.currentTimeMillis() + 100)));   // 整数45 在[20,80] ✓
        mixedData.add(Util.genEventData("data3", "60.8", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数60.8 在[20,80] ✓
        mixedData.add(Util.genEventData("data4", "35", new Timestamp(System.currentTimeMillis() + 300)));   // 整数35 在[20,80] ✓
        mixedData.add(Util.genEventData("data5", "70.2", new Timestamp(System.currentTimeMillis() + 400))); // 浮点数70.2 在[20,80] ✓ (连续5个)

        boolean result = false;
        for (RhythmixEventData data : mixedData) {
            result = rhythmixExecutor.execute(data);
        }

        // 连续正常且无严重异常，应该返回true
        Assertions.assertTrue(result);
    }

    // ==================== 状态转换测试 ====================

    /**
     * 测试状态转换：正常 → 连续异常 → 恢复
     */
    @Test
    @DisplayName("状态转换 - 正常→连续异常→恢复")
    void testStateTransitionNormalToAbnormalToRecovery() throws TranslatorException {
        String code = "{count!(<10, 3)}->{count!(>50, 2)}->{<5}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> stateData = new ArrayList<>();
        // 第一阶段：连续3次小于10（正常状态）
        stateData.add(Util.genEventData("state1", "8.5", new Timestamp(System.currentTimeMillis())));      // 浮点数8.5 < 10 ✓ (计数=1)
        stateData.add(Util.genEventData("state2", "7", new Timestamp(System.currentTimeMillis() + 100)));   // 整数7 < 10 ✓ (计数=2)
        stateData.add(Util.genEventData("state3", "9.2", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数9.2 < 10 ✓ (计数=3) → 第一状态完成

        // 第二阶段：连续2次大于50（异常状态）
        stateData.add(Util.genEventData("state4", "60", new Timestamp(System.currentTimeMillis() + 300)));   // 整数60 > 50 ✓ (计数=1)
        stateData.add(Util.genEventData("state5", "55.8", new Timestamp(System.currentTimeMillis() + 400))); // 浮点数55.8 > 50 ✓ (计数=2) → 第二状态完成

        // 第三阶段：恢复（小于5）
        stateData.add(Util.genEventData("state6", "3.5", new Timestamp(System.currentTimeMillis() + 500))); // 浮点数3.5 < 5 ✓ → 第三状态完成

        boolean result = false;
        for (RhythmixEventData data : stateData) {
            result = rhythmixExecutor.execute(data);
        }

        // 完成三阶段状态转换，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试复杂状态检测
     */
    @Test
    @DisplayName("状态转换 - 复杂状态检测")
    void testComplexStateDetection() throws TranslatorException {
        String code = "{==0}->{count!(>4, 3)}->{count!(<2, 2)}";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> complexData = new ArrayList<>();
        // 第一阶段：等于0
        complexData.add(Util.genEventData("complex1", "0", new Timestamp(System.currentTimeMillis())));      // 整数0 == 0 ✓ → 第一状态完成

        // 第二阶段：连续3次大于4
        complexData.add(Util.genEventData("complex2", "5.5", new Timestamp(System.currentTimeMillis() + 100))); // 浮点数5.5 > 4 ✓ (计数=1)
        complexData.add(Util.genEventData("complex3", "6", new Timestamp(System.currentTimeMillis() + 200)));   // 整数6 > 4 ✓ (计数=2)
        complexData.add(Util.genEventData("complex4", "7.8", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数7.8 > 4 ✓ (计数=3) → 第二状态完成

        // 第三阶段：连续2次小于2
        complexData.add(Util.genEventData("complex5", "1.5", new Timestamp(System.currentTimeMillis() + 400))); // 浮点数1.5 < 2 ✓ (计数=1)
        complexData.add(Util.genEventData("complex6", "1", new Timestamp(System.currentTimeMillis() + 500)));   // 整数1 < 2 ✓ (计数=2) → 第三状态完成

        boolean result = false;
        for (RhythmixEventData data : complexData) {
            result = rhythmixExecutor.execute(data);
        }

        // 完成复杂状态检测，应该返回true
        Assertions.assertTrue(result);
    }

    // ==================== 混合数据类型高级测试 ====================

    /**
     * 测试高精度浮点数与整数混合的函数调用
     */
    @Test
    @DisplayName("混合数据类型 - 高精度浮点数与整数混合")
    void testHighPrecisionMixedDataTypes() throws TranslatorException {
        String code = "count!([99.95,100.05], 4)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> precisionData = new ArrayList<>();
        precisionData.add(Util.genEventData("precision1", "100", new Timestamp(System.currentTimeMillis())));      // 整数100 在[99.95,100.05] ✓ (计数=1)
        precisionData.add(Util.genEventData("precision2", "99.98", new Timestamp(System.currentTimeMillis() + 100))); // 浮点数99.98 在[99.95,100.05] ✓ (计数=2)
        precisionData.add(Util.genEventData("precision3", "100.02", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数100.02 在[99.95,100.05] ✓ (计数=3)
        precisionData.add(Util.genEventData("precision4", "100.01", new Timestamp(System.currentTimeMillis() + 300))); // 浮点数100.01 在[99.95,100.05] ✓ (计数=4) → 返回 true

        boolean result = false;
        for (RhythmixEventData data : precisionData) {
            result = rhythmixExecutor.execute(data);
        }

        // 高精度混合数据连续满足4次条件，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试边界值混合：整数和浮点数边界测试
     */
    @Test
    @DisplayName("混合数据类型 - 边界值混合测试")
    void testBoundaryValueMixedTypes() throws TranslatorException {
        String code = "count(>25.5, 3)";
        RhythmixExecutor rhythmixExecutor = RhythmixCompiler.compile(code);

        List<RhythmixEventData> boundaryData = new ArrayList<>();
        boundaryData.add(Util.genEventData("boundary1", "25.5", new Timestamp(System.currentTimeMillis())));      // 浮点数25.5 == 25.5 ✗ (计数=0)
        boundaryData.add(Util.genEventData("boundary2", "26", new Timestamp(System.currentTimeMillis() + 100)));   // 整数26 > 25.5 ✓ (计数=1)
        boundaryData.add(Util.genEventData("boundary3", "25.6", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数25.6 > 25.5 ✓ (计数=2)
        boundaryData.add(Util.genEventData("boundary4", "25", new Timestamp(System.currentTimeMillis() + 300)));   // 整数25 < 25.5 ✗ (计数保持=2)
        boundaryData.add(Util.genEventData("boundary5", "30.1", new Timestamp(System.currentTimeMillis() + 400))); // 浮点数30.1 > 25.5 ✓ (计数=3) → 返回 true

        boolean result = false;
        for (RhythmixEventData data : boundaryData) {
            result = rhythmixExecutor.execute(data);
        }

        // 边界值混合测试满足3次条件，应该返回true
        Assertions.assertTrue(result);
    }
}
