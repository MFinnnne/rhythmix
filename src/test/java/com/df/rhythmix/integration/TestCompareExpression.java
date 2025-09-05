/*
 * @Author: MFine
 * @Date: 2025-01-03 10:00:00
 * @LastEditTime: 2025-01-03 10:00:00
 * @LastEditors: MFine
 * @Description: 测试比较表达式的实际应用示例
 */
package com.df.rhythmix.integration;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.Compiler;
import com.df.rhythmix.execute.Executor;
import com.df.rhythmix.util.RhythmixEventData;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MFine
 * @version 1.0
 * @date 2025/01/03 10:00
 * @description 测试比较表达式的实际应用示例
 * 基于README.md中'比较表达式实际应用示例'部分的内容生成测试用例
 **/
public class TestCompareExpression {

    // ==================== 温度监控场景测试 ====================

    /**
     * 测试检测温度超过30度
     */
    @Test
    @DisplayName("温度监控 - 检测温度超过30度")
    void testTemperatureGreaterThan30() throws TranslatorException {
        String code = ">30";
        Executor executor = Compiler.compile(code);

        // 测试温度35度，应该返回true
        RhythmixEventData tempData = Util.genEventData("temp1", "35", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(tempData));

        // 测试温度25度，应该返回false
        RhythmixEventData tempData2 = Util.genEventData("temp2", "25", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData2));

        // 测试边界值30度，应该返回false
        RhythmixEventData tempData3 = Util.genEventData("temp3", "30", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData3));
    }

    /**
     * 测试检测温度低于10度
     */
    @Test
    @DisplayName("温度监控 - 检测温度低于10度")
    void testTemperatureLessThan10() throws TranslatorException {
        String code = "<10";
        Executor executor = Compiler.compile(code);

        // 测试温度5度，应该返回true
        RhythmixEventData tempData = Util.genEventData("temp1", "5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(tempData));

        // 测试温度15度，应该返回false
        RhythmixEventData tempData2 = Util.genEventData("temp2", "15", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData2));

        // 测试边界值10度，应该返回false
        RhythmixEventData tempData3 = Util.genEventData("temp3", "10", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData3));
    }

    /**
     * 测试检测温度等于25度（精确匹配）
     */
    @Test
    @DisplayName("温度监控 - 检测温度等于25度")
    void testTemperatureEquals25() throws TranslatorException {
        String code = "==25";
        Executor executor = Compiler.compile(code);

        // 测试温度25度，应该返回true
        RhythmixEventData tempData = Util.genEventData("temp1", "25", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(tempData));

        // 测试温度24度，应该返回false
        RhythmixEventData tempData2 = Util.genEventData("temp2", "24", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData2));

        // 测试温度26度，应该返回false
        RhythmixEventData tempData3 = Util.genEventData("temp3", "26", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData3));
    }

    /**
     * 测试检测温度不等于0度（排除异常读数）
     */
    @Test
    @DisplayName("温度监控 - 检测温度不等于0度")
    void testTemperatureNotEquals0() throws TranslatorException {
        String code = "!=0";
        Executor executor = Compiler.compile(code);

        // 测试温度25度，应该返回true
        RhythmixEventData tempData = Util.genEventData("temp1", "25", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(tempData));

        // 测试温度0度，应该返回false
        RhythmixEventData tempData2 = Util.genEventData("temp2", "0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData2));

        // 测试负温度-5度，应该返回true
        RhythmixEventData tempData3 = Util.genEventData("temp3", "-5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(tempData3));
    }

    // ==================== 生产线质量控制测试 ====================

    /**
     * 测试产品重量大于等于标准重量100g
     */
    @Test
    @DisplayName("生产线质量控制 - 产品重量大于等于100g")
    void testProductWeightGreaterOrEqual100() throws TranslatorException {
        String code = ">=100";
        Executor executor = Compiler.compile(code);

        // 测试重量105g，应该返回true
        RhythmixEventData productData = Util.genEventData("product1", "105", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(productData));

        // 测试重量100g，应该返回true（边界值）
        RhythmixEventData productData2 = Util.genEventData("product2", "100", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(productData2));

        // 测试重量95g，应该返回false
        RhythmixEventData productData3 = Util.genEventData("product3", "95", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(productData3));
    }

    /**
     * 测试产品尺寸小于等于最大允许值50mm
     */
    @Test
    @DisplayName("生产线质量控制 - 产品尺寸小于等于50mm")
    void testProductSizeLessOrEqual50() throws TranslatorException {
        String code = "<=50";
        Executor executor = Compiler.compile(code);

        // 测试尺寸45mm，应该返回true
        RhythmixEventData productData = Util.genEventData("product1", "45", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(productData));

        // 测试尺寸50mm，应该返回true（边界值）
        RhythmixEventData productData2 = Util.genEventData("product2", "50", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(productData2));

        // 测试尺寸55mm，应该返回false
        RhythmixEventData productData3 = Util.genEventData("product3", "55", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(productData3));
    }

    /**
     * 测试检测连续3个产品重量都大于95g
     */
    @Test
    @DisplayName("生产线质量控制 - 检测连续3个产品重量都大于95g")
    void testConsecutiveProductWeightGreaterThan95() throws TranslatorException {
        String code = "count(>95, 3)";
        Executor executor = Compiler.compile(code);

        List<RhythmixEventData> products = new ArrayList<>();
        products.add(Util.genEventData("product1", "98", new Timestamp(System.currentTimeMillis())));
        products.add(Util.genEventData("product2", "102", new Timestamp(System.currentTimeMillis() + 100)));
        products.add(Util.genEventData("product3", "97", new Timestamp(System.currentTimeMillis() + 200)));

        boolean result = false;
        for (RhythmixEventData product : products) {
            result = executor.execute(product);
        }

        // 3个产品重量都大于95g，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试检测连续2个产品尺寸都小于45mm
     */
    @Test
    @DisplayName("生产线质量控制 - 检测连续2个产品尺寸都小于45mm")
    void testConsecutiveProductSizeLessThan45() throws TranslatorException {
        String code = "count!(<45, 2)";
        Executor executor = Compiler.compile(code);

        List<RhythmixEventData> products = new ArrayList<>();
        products.add(Util.genEventData("product1", "42", new Timestamp(System.currentTimeMillis())));
        products.add(Util.genEventData("product2", "43", new Timestamp(System.currentTimeMillis() + 100)));

        boolean result = false;
        for (RhythmixEventData product : products) {
            result = executor.execute(product);
        }

        // 连续2个产品尺寸都小于45mm，应该返回true
        Assertions.assertTrue(result);
    }

    // ==================== 网络延迟监控测试 ====================

    /**
     * 测试响应时间大于1000ms（1秒）
     */
    @Test
    @DisplayName("网络延迟监控 - 响应时间大于1000ms")
    void testResponseTimeGreaterThan1000() throws TranslatorException {
        String code = ">1000";
        Executor executor = Compiler.compile(code);

        // 测试响应时间1500ms，应该返回true
        RhythmixEventData responseData = Util.genEventData("response1", "1500", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(responseData));

        // 测试响应时间800ms，应该返回false
        RhythmixEventData responseData2 = Util.genEventData("response2", "800", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(responseData2));

        // 测试边界值1000ms，应该返回false
        RhythmixEventData responseData3 = Util.genEventData("response3", "1000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(responseData3));
    }

    /**
     * 测试检测连续5次响应时间都小于100ms
     */
    @Test
    @DisplayName("网络延迟监控 - 检测连续5次响应时间都小于100ms")
    void testConsecutiveResponseTimeLessThan100() throws TranslatorException {
        String code = "count!(<100, 5)";
        Executor executor = Compiler.compile(code);

        List<RhythmixEventData> responses = new ArrayList<>();
        responses.add(Util.genEventData("response1", "80", new Timestamp(System.currentTimeMillis())));
        responses.add(Util.genEventData("response2", "90", new Timestamp(System.currentTimeMillis() + 100)));
        responses.add(Util.genEventData("response3", "85", new Timestamp(System.currentTimeMillis() + 200)));
        responses.add(Util.genEventData("response4", "95", new Timestamp(System.currentTimeMillis() + 300)));
        responses.add(Util.genEventData("response5", "88", new Timestamp(System.currentTimeMillis() + 400)));

        boolean result = false;
        for (RhythmixEventData response : responses) {
            result = executor.execute(response);
        }

        // 连续5次响应时间都小于100ms，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试状态转换：正常响应 → 高延迟 → 恢复正常
     */
    @Test
    @DisplayName("网络延迟监控 - 状态转换：正常→高延迟→恢复正常")
    void testNetworkLatencyStateTransition() throws TranslatorException {
        String code = "{<100}->{>1000}->{<200}";
        Executor executor = Compiler.compile(code);

        List<RhythmixEventData> responses = new ArrayList<>();
        responses.add(Util.genEventData("response1", "80", new Timestamp(System.currentTimeMillis())));    // 正常响应
        responses.add(Util.genEventData("response2", "1200", new Timestamp(System.currentTimeMillis() + 100))); // 高延迟
        responses.add(Util.genEventData("response3", "150", new Timestamp(System.currentTimeMillis() + 200)));  // 恢复正常

        boolean result = false;
        for (RhythmixEventData response : responses) {
            result = executor.execute(response);
        }

        // 完成状态转换，应该返回true
        Assertions.assertTrue(result);
    }

    // ==================== 传感器数据处理测试 ====================

    /**
     * 测试压力传感器读数等于0（可能故障）
     */
    @Test
    @DisplayName("传感器数据处理 - 压力传感器读数等于0")
    void testPressureSensorEquals0() throws TranslatorException {
        String code = "==0";
        Executor executor = Compiler.compile(code);

        // 测试压力值0，应该返回true（可能故障）
        RhythmixEventData sensorData = Util.genEventData("pressure1", "0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(sensorData));

        // 测试压力值50，应该返回false
        RhythmixEventData sensorData2 = Util.genEventData("pressure2", "50", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(sensorData2));
    }

    /**
     * 测试湿度传感器读数不等于-1（排除错误值）
     */
    @Test
    @DisplayName("传感器数据处理 - 湿度传感器读数不等于-1")
    void testHumiditySensorNotEqualsNegative1() throws TranslatorException {
        String code = "!=(-1)";
        Executor executor = Compiler.compile(code);

        // 测试湿度值60，应该返回true
        RhythmixEventData sensorData = Util.genEventData("humidity1", "60", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(sensorData));

        // 测试湿度值-1，应该返回false（错误值）
        RhythmixEventData sensorData2 = Util.genEventData("humidity2", "-1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(sensorData2));

        // 测试湿度值0，应该返回true
        RhythmixEventData sensorData3 = Util.genEventData("humidity3", "0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(sensorData3));
    }

    /**
     * 测试检测压力值连续3次大于等于50
     */
    @Test
    @DisplayName("传感器数据处理 - 检测压力值连续3次大于等于50")
    void testConsecutivePressureGreaterOrEqual50() throws TranslatorException {
        String code = "count!(>=50.1, 3)";
        Executor executor = Compiler.compile(code);

        List<RhythmixEventData> pressureData = new ArrayList<>();
        pressureData.add(Util.genEventData("pressure1", "55.2", new Timestamp(System.currentTimeMillis())));
        pressureData.add(Util.genEventData("pressure2", "60", new Timestamp(System.currentTimeMillis() + 100)));
        pressureData.add(Util.genEventData("pressure3", "50", new Timestamp(System.currentTimeMillis() + 200)));

        boolean result = false;
        for (RhythmixEventData data : pressureData) {
            result = executor.execute(data);
        }

        // 连续3次压力值都大于等于50，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试复合条件：温度不等于0且大于10（排除异常值并检测正常范围）
     */
    @Test
    @DisplayName("传感器数据处理 - 复合条件：温度不等于0且大于10")
    void testTemperatureCompoundCondition() throws TranslatorException {
        String code = "!=0&&>10.5";
        Executor executor = Compiler.compile(code);

        // 测试温度25度，应该返回true（不等于0且大于10）
        RhythmixEventData tempData = Util.genEventData("temp1", "25", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(tempData));

        // 测试温度0度，应该返回false（等于0）
        RhythmixEventData tempData2 = Util.genEventData("temp2", "0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData2));

        // 测试温度5度，应该返回false（不大于10）
        RhythmixEventData tempData3 = Util.genEventData("temp3", "5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData3));

        // 测试温度10度，应该返回false（不大于10）
        RhythmixEventData tempData4 = Util.genEventData("temp4", "10.1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData4));
    }

    /**
     * 测试状态转换：温度从正常升高到过热状态
     */
    @Test
    @DisplayName("传感器数据处理 - 状态转换：温度从正常升高到过热")
    void testTemperatureOverheatStateTransition() throws TranslatorException {
        String code = "{<=40}->{>80.1}";
        Executor executor = Compiler.compile(code);

        List<RhythmixEventData> tempData = new ArrayList<>();
        tempData.add(Util.genEventData("temp1", "35", new Timestamp(System.currentTimeMillis())));    // 正常温度
        tempData.add(Util.genEventData("temp2", "85", new Timestamp(System.currentTimeMillis() + 100))); // 过热温度

        boolean result = false;
        for (RhythmixEventData data : tempData) {
            result = executor.execute(data);
        }

        // 完成从正常到过热的状态转换，应该返回true
        Assertions.assertTrue(result);
    }

    // ==================== 边界值和异常情况测试 ====================

    /**
     * 测试浮点数比较
     */
    @Test
    @DisplayName("边界测试 - 浮点数比较")
    void testFloatingPointComparison() throws TranslatorException {
        String code = ">25.5";
        Executor executor = Compiler.compile(code);

        // 测试25.6，应该返回true
        RhythmixEventData data1 = Util.genEventData("float1", "25.6", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(data1));

        // 测试25.5，应该返回false
        RhythmixEventData data2 = Util.genEventData("float2", "25.5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(data2));

        // 测试25.4，应该返回false
        RhythmixEventData data3 = Util.genEventData("float3", "25.4", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(data3));
    }

    /**
     * 测试负数比较
     */
    @Test
    @DisplayName("边界测试 - 负数比较")
    void testNegativeNumberComparison() throws TranslatorException {
        String code = "<-10";
        Executor executor = Compiler.compile(code);

        // 测试-15，应该返回true
        RhythmixEventData data1 = Util.genEventData("neg1", "-15", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(data1));

        // 测试-10，应该返回false
        RhythmixEventData data2 = Util.genEventData("neg2", "-10", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(data2));

        // 测试-5，应该返回false
        RhythmixEventData data3 = Util.genEventData("neg3", "-5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(data3));
    }
}
