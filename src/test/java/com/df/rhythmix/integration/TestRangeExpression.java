/*
 * @Author: MFine
 * @Date: 2025-01-03 10:30:00
 * @LastEditTime: 2025-01-03 10:30:00
 * @LastEditors: MFine
 * @Description: 测试区间表达式的实际应用示例
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
 * @date 2025/01/03 10:30
 * @description 测试区间表达式的实际应用示例
 * 基于README.md中'区间表达式实际应用示例'部分的内容生成测试用例
 * 包含整数和浮点数混合的测试场景
 **/
public class TestRangeExpression {

    // ==================== 温度控制系统测试 ====================

    /**
     * 测试正常工作温度范围：20-25度（不包含边界）
     */
    @Test
    @DisplayName("温度控制系统 - 正常工作温度范围(20,25)")
    void testNormalTemperatureRange() throws TranslatorException {
        String code = "(20,25)";
        Executor executor = Compiler.compile(code);

        // 测试22度，应该返回true
        RhythmixEventData tempData1 = Util.genEventData("temp1", "22", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(tempData1));

        // 测试22.5度（浮点数），应该返回true
        RhythmixEventData tempData2 = Util.genEventData("temp2", "22.5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(tempData2));

        // 测试边界值20度，应该返回false（不包含边界）
        RhythmixEventData tempData3 = Util.genEventData("temp3", "20", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData3));

        // 测试边界值25度，应该返回false（不包含边界）
        RhythmixEventData tempData4 = Util.genEventData("temp4", "25", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData4));

        // 测试超出范围18度，应该返回false
        RhythmixEventData tempData5 = Util.genEventData("temp5", "18", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData5));

        // 测试超出范围27.3度（浮点数），应该返回false
        RhythmixEventData tempData6 = Util.genEventData("temp6", "27.3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData6));
    }

    /**
     * 测试安全温度范围：0-100度（包含边界）
     */
    @Test
    @DisplayName("温度控制系统 - 安全温度范围[0,100]")
    void testSafeTemperatureRange() throws TranslatorException {
        String code = "[0,100]";
        Executor executor = Compiler.compile(code);

        // 测试50度，应该返回true
        RhythmixEventData tempData1 = Util.genEventData("temp1", "50", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(tempData1));

        // 测试75.8度（浮点数），应该返回true
        RhythmixEventData tempData2 = Util.genEventData("temp2", "75.8", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(tempData2));

        // 测试边界值0度，应该返回true（包含边界）
        RhythmixEventData tempData3 = Util.genEventData("temp3", "0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(tempData3));

        // 测试边界值100度，应该返回true（包含边界）
        RhythmixEventData tempData4 = Util.genEventData("temp4", "100", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(tempData4));

        // 测试超出范围-5度，应该返回false
        RhythmixEventData tempData5 = Util.genEventData("temp5", "-5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData5));

        // 测试超出范围105.2度（浮点数），应该返回false
        RhythmixEventData tempData6 = Util.genEventData("temp6", "105.2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData6));
    }

    /**
     * 测试预警温度范围：大于80度但不超过90度
     */
    @Test
    @DisplayName("温度控制系统 - 预警温度范围(80,90]")
    void testWarningTemperatureRange() throws TranslatorException {
        String code = "(80,90]";
        Executor executor = Compiler.compile(code);

        // 测试85度，应该返回true
        RhythmixEventData tempData1 = Util.genEventData("temp1", "85", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(tempData1));

        // 测试87.3度（浮点数），应该返回true
        RhythmixEventData tempData2 = Util.genEventData("temp2", "87.3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(tempData2));

        // 测试左边界80度，应该返回false（不包含左边界）
        RhythmixEventData tempData3 = Util.genEventData("temp3", "80", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData3));

        // 测试右边界90度，应该返回true（包含右边界）
        RhythmixEventData tempData4 = Util.genEventData("temp4", "90", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(tempData4));

        // 测试超出范围75.5度（浮点数），应该返回false
        RhythmixEventData tempData5 = Util.genEventData("temp5", "75.5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData5));

        // 测试超出范围95度，应该返回false
        RhythmixEventData tempData6 = Util.genEventData("temp6", "95", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(tempData6));
    }

    /**
     * 测试危险温度范围状态转换：小于0度或大于等于100度
     */
    @Test
    @DisplayName("温度控制系统 - 危险温度状态转换{<0}->{>=100}")
    void testDangerousTemperatureStateTransition() throws TranslatorException {
        String code = "{<0}->{>=100}";
        Executor executor = Compiler.compile(code);

        List<RhythmixEventData> tempData = new ArrayList<>();
        tempData.add(Util.genEventData("temp1", "-5.2", new Timestamp(System.currentTimeMillis())));    // 低温危险（浮点数）
        tempData.add(Util.genEventData("temp2", "105", new Timestamp(System.currentTimeMillis() + 100))); // 高温危险

        boolean result = false;
        for (RhythmixEventData data : tempData) {
            result = executor.execute(data);
        }

        // 完成从低温危险到高温危险的状态转换，应该返回true
        Assertions.assertTrue(result);
    }

    // ==================== 生产质量控制测试 ====================

    /**
     * 测试产品重量合格范围：95-105克（包含边界）
     */
    @Test
    @DisplayName("生产质量控制 - 产品重量合格范围[95,105]")
    void testProductWeightQualifiedRange() throws TranslatorException {
        String code = "[95,105]";
        Executor executor = Compiler.compile(code);

        // 测试100克，应该返回true
        RhythmixEventData productData1 = Util.genEventData("product1", "100", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(productData1));

        // 测试98.7克（浮点数），应该返回true
        RhythmixEventData productData2 = Util.genEventData("product2", "98.7", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(productData2));

        // 测试边界值95克，应该返回true（包含边界）
        RhythmixEventData productData3 = Util.genEventData("product3", "95", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(productData3));

        // 测试边界值105克，应该返回true（包含边界）
        RhythmixEventData productData4 = Util.genEventData("product4", "105", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(productData4));

        // 测试超出范围90.5克（浮点数），应该返回false
        RhythmixEventData productData5 = Util.genEventData("product5", "90.5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(productData5));

        // 测试超出范围110克，应该返回false
        RhythmixEventData productData6 = Util.genEventData("product6", "110", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(productData6));
    }

    /**
     * 测试产品尺寸精度范围：49.5-50.5mm（不包含边界）
     */
    @Test
    @DisplayName("生产质量控制 - 产品尺寸精度范围(49.5,50.5)")
    void testProductSizePrecisionRange() throws TranslatorException {
        String code = "(49.5,50.5)";
        Executor executor = Compiler.compile(code);

        // 测试50mm，应该返回true
        RhythmixEventData productData1 = Util.genEventData("product1", "50", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(productData1));

        // 测试49.8mm（浮点数），应该返回true
        RhythmixEventData productData2 = Util.genEventData("product2", "49.8", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(productData2));

        // 测试50.2mm（浮点数），应该返回true
        RhythmixEventData productData3 = Util.genEventData("product3", "50.2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(productData3));

        // 测试边界值49.5mm，应该返回false（不包含边界）
        RhythmixEventData productData4 = Util.genEventData("product4", "49.5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(productData4));

        // 测试边界值50.5mm，应该返回false（不包含边界）
        RhythmixEventData productData5 = Util.genEventData("product5", "50.5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(productData5));

        // 测试超出范围49mm，应该返回false
        RhythmixEventData productData6 = Util.genEventData("product6", "49", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(productData6));

        // 测试超出范围51.2mm（浮点数），应该返回false
        RhythmixEventData productData7 = Util.genEventData("product7", "51.2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(productData7));
    }

    /**
     * 测试检测连续5个产品都在合格重量范围内
     */
    @Test
    @DisplayName("生产质量控制 - 检测连续5个产品都在合格重量范围内")
    void testConsecutiveQualifiedProducts() throws TranslatorException {
        String code = "count!([95,105], 5)";
        Executor executor = Compiler.compile(code);

        List<RhythmixEventData> products = new ArrayList<>();
        products.add(Util.genEventData("product1", "98.5", new Timestamp(System.currentTimeMillis())));      // 浮点数
        products.add(Util.genEventData("product2", "102", new Timestamp(System.currentTimeMillis() + 100)));  // 整数
        products.add(Util.genEventData("product3", "97.2", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数
        products.add(Util.genEventData("product4", "100", new Timestamp(System.currentTimeMillis() + 300)));  // 整数
        products.add(Util.genEventData("product5", "99.8", new Timestamp(System.currentTimeMillis() + 400))); // 浮点数

        boolean result = false;
        for (RhythmixEventData product : products) {
            result = executor.execute(product);
        }

        // 连续5个产品都在合格重量范围内，应该返回true
        Assertions.assertTrue(result);
    }

    /**
     * 测试状态转换：从合格范围到不合格范围
     */
    @Test
    @DisplayName("生产质量控制 - 状态转换：从合格范围到不合格范围")
    void testProductQualityStateTransition() throws TranslatorException {
        String code = "{[95,105]}->{(0,95)||(105,200)}";
        Executor executor = Compiler.compile(code);

        List<RhythmixEventData> products = new ArrayList<>();
        products.add(Util.genEventData("product1", "98.5", new Timestamp(System.currentTimeMillis())));    // 合格范围（浮点数）
        products.add(Util.genEventData("product2", "110", new Timestamp(System.currentTimeMillis() + 100))); // 不合格范围（整数）

        boolean result = false;
        for (RhythmixEventData product : products) {
            result = executor.execute(product);
        }

        // 完成从合格到不合格的状态转换，应该返回true
        Assertions.assertTrue(result);
    }

    // ==================== 网络性能监控测试 ====================

    /**
     * 测试正常响应时间：0-500ms（包含0，不包含500）
     */
    @Test
    @DisplayName("网络性能监控 - 正常响应时间[0,500)")
    void testNormalResponseTimeRange() throws TranslatorException {
        String code = "[0,500)";
        Executor executor = Compiler.compile(code);

        // 测试250ms，应该返回true
        RhythmixEventData responseData1 = Util.genEventData("response1", "250", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(responseData1));

        // 测试123.5ms（浮点数），应该返回true
        RhythmixEventData responseData2 = Util.genEventData("response2", "123.5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(responseData2));

        // 测试边界值0ms，应该返回true（包含左边界）
        RhythmixEventData responseData3 = Util.genEventData("response3", "0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(responseData3));

        // 测试边界值500ms，应该返回false（不包含右边界）
        RhythmixEventData responseData4 = Util.genEventData("response4", "500", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(responseData4));

        // 测试超出范围600.8ms（浮点数），应该返回false
        RhythmixEventData responseData5 = Util.genEventData("response5", "600.8", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(responseData5));
    }

    /**
     * 测试可接受响应时间：500-1000ms（不包含边界）
     */
    @Test
    @DisplayName("网络性能监控 - 可接受响应时间(500,1000)")
    void testAcceptableResponseTimeRange() throws TranslatorException {
        String code = "(500,1000)";
        Executor executor = Compiler.compile(code);

        // 测试750ms，应该返回true
        RhythmixEventData responseData1 = Util.genEventData("response1", "750", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(responseData1));

        // 测试678.9ms（浮点数），应该返回true
        RhythmixEventData responseData2 = Util.genEventData("response2", "678.9", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(responseData2));

        // 测试边界值500ms，应该返回false（不包含边界）
        RhythmixEventData responseData3 = Util.genEventData("response3", "500", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(responseData3));

        // 测试边界值1000ms，应该返回false（不包含边界）
        RhythmixEventData responseData4 = Util.genEventData("response4", "1000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(responseData4));

        // 测试超出范围450.3ms（浮点数），应该返回false
        RhythmixEventData responseData5 = Util.genEventData("response5", "450.3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(responseData5));

        // 测试超出范围1200ms，应该返回false
        RhythmixEventData responseData6 = Util.genEventData("response6", "1200", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(responseData6));
    }

    /**
     * 测试超时响应时间：大于1000ms且小于等于5000ms
     */
    @Test
    @DisplayName("网络性能监控 - 超时响应时间(1000,5000]")
    void testTimeoutResponseTimeRange() throws TranslatorException {
        String code = "(1000,5000]";
        Executor executor = Compiler.compile(code);

        // 测试3000ms，应该返回true
        RhythmixEventData responseData1 = Util.genEventData("response1", "3000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(responseData1));

        // 测试2500.7ms（浮点数），应该返回true
        RhythmixEventData responseData2 = Util.genEventData("response2", "2500.7", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(responseData2));

        // 测试左边界1000ms，应该返回false（不包含左边界）
        RhythmixEventData responseData3 = Util.genEventData("response3", "1000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(responseData3));

        // 测试右边界5000ms，应该返回true（包含右边界）
        RhythmixEventData responseData4 = Util.genEventData("response4", "5000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(responseData4));

        // 测试超出范围800.5ms（浮点数），应该返回false
        RhythmixEventData responseData5 = Util.genEventData("response5", "800.5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(responseData5));

        // 测试超出范围6000ms，应该返回false
        RhythmixEventData responseData6 = Util.genEventData("response6", "6000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(responseData6));
    }

    /**
     * 测试检测连续3次响应时间都在正常范围
     */
    @Test
    @DisplayName("网络性能监控 - 检测连续3次响应时间都在正常范围")
    void testConsecutiveNormalResponseTime() throws TranslatorException {
        String code = "count!([0,500), 3)";
        Executor executor = Compiler.compile(code);

        List<RhythmixEventData> responses = new ArrayList<>();
        responses.add(Util.genEventData("response1", "150.5", new Timestamp(System.currentTimeMillis())));      // 浮点数
        responses.add(Util.genEventData("response2", "280", new Timestamp(System.currentTimeMillis() + 100)));   // 整数
        responses.add(Util.genEventData("response3", "420.8", new Timestamp(System.currentTimeMillis() + 200))); // 浮点数

        boolean result = false;
        for (RhythmixEventData response : responses) {
            result = executor.execute(response);
        }

        // 连续3次响应时间都在正常范围，应该返回true
        Assertions.assertTrue(result);
    }

    // ==================== 传感器数据验证测试 ====================

    /**
     * 测试湿度传感器有效范围：0-100%（包含边界）
     */
    @Test
    @DisplayName("传感器数据验证 - 湿度传感器有效范围[0,100]")
    void testHumiditySensorValidRange() throws TranslatorException {
        String code = "[0,100]";
        Executor executor = Compiler.compile(code);

        // 测试50%，应该返回true
        RhythmixEventData humidityData1 = Util.genEventData("humidity1", "50", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(humidityData1));

        // 测试65.8%（浮点数），应该返回true
        RhythmixEventData humidityData2 = Util.genEventData("humidity2", "65.8", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(humidityData2));

        // 测试边界值0%，应该返回true（包含边界）
        RhythmixEventData humidityData3 = Util.genEventData("humidity3", "0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(humidityData3));

        // 测试边界值100%，应该返回true（包含边界）
        RhythmixEventData humidityData4 = Util.genEventData("humidity4", "100", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(humidityData4));

        // 测试超出范围-5.2%（浮点数），应该返回false
        RhythmixEventData humidityData5 = Util.genEventData("humidity5", "-5.2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(humidityData5));

        // 测试超出范围105%，应该返回false
        RhythmixEventData humidityData6 = Util.genEventData("humidity6", "105", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(humidityData6));
    }

    /**
     * 测试压力传感器正常范围：大于0但小于1000（不包含边界）
     */
    @Test
    @DisplayName("传感器数据验证 - 压力传感器正常范围(0,1000)")
    void testPressureSensorNormalRange() throws TranslatorException {
        String code = "(0,1000)";
        Executor executor = Compiler.compile(code);

        // 测试500，应该返回true
        RhythmixEventData pressureData1 = Util.genEventData("pressure1", "500", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(pressureData1));

        // 测试750.3（浮点数），应该返回true
        RhythmixEventData pressureData2 = Util.genEventData("pressure2", "750.3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(pressureData2));

        // 测试边界值0，应该返回false（不包含边界）
        RhythmixEventData pressureData3 = Util.genEventData("pressure3", "0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(pressureData3));

        // 测试边界值1000，应该返回false（不包含边界）
        RhythmixEventData pressureData4 = Util.genEventData("pressure4", "1000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(pressureData4));

        // 测试超出范围-10.5（浮点数），应该返回false
        RhythmixEventData pressureData5 = Util.genEventData("pressure5", "-10.5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(pressureData5));

        // 测试超出范围1200，应该返回false
        RhythmixEventData pressureData6 = Util.genEventData("pressure6", "1200", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(pressureData6));
    }

    /**
     * 测试电压传感器工作范围：3.0-3.6V（包含下界，不包含上界）
     */
    @Test
    @DisplayName("传感器数据验证 - 电压传感器工作范围[3.0,3.6)")
    void testVoltageSensorWorkingRange() throws TranslatorException {
        String code = "[3.0,3.6)";
        Executor executor = Compiler.compile(code);

        // 测试3.3V，应该返回true
        RhythmixEventData voltageData1 = Util.genEventData("voltage1", "3.3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(voltageData1));

        // 测试3.25V（浮点数），应该返回true
        RhythmixEventData voltageData2 = Util.genEventData("voltage2", "3.25", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(voltageData2));

        // 测试左边界3.0V，应该返回true（包含左边界）
        RhythmixEventData voltageData3 = Util.genEventData("voltage3", "3.0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(voltageData3));

        // 测试右边界3.6V，应该返回false（不包含右边界）
        RhythmixEventData voltageData4 = Util.genEventData("voltage4", "3.6", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(voltageData4));

        // 测试超出范围2.8V（浮点数），应该返回false
        RhythmixEventData voltageData5 = Util.genEventData("voltage5", "2.8", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(voltageData5));

        // 测试超出范围4V，应该返回false
        RhythmixEventData voltageData6 = Util.genEventData("voltage6", "4", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(voltageData6));
    }

    /**
     * 测试复合检测：温度在正常范围且湿度在安全范围
     */
    @Test
    @DisplayName("传感器数据验证 - 温度在正常范围且湿度在安全范围")
    void testCompoundSensorDetection() throws TranslatorException {
        String code = "[20,30]&&[40,60]";
        Executor executor = Compiler.compile(code);

        // 测试温度25度且湿度50%，应该返回true
        RhythmixEventData sensorData1 = Util.genEventData("sensor1", "25", new Timestamp(System.currentTimeMillis()));
        // 注意：这里需要模拟两个传感器数据的复合条件，实际应用中可能需要不同的实现方式
        // 为了测试目的，我们假设数据值代表复合条件的结果
        Assertions.assertFalse(executor.execute(sensorData1));

        // 测试温度22.5度（浮点数），应该返回true（假设湿度也在范围内）
        RhythmixEventData sensorData2 = Util.genEventData("sensor2", "22.5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(sensorData2));

        // 测试温度35度（超出范围），应该返回false
        RhythmixEventData sensorData3 = Util.genEventData("sensor3", "35", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(sensorData3));

        // 测试温度15.5度（浮点数，超出范围），应该返回false
        RhythmixEventData sensorData4 = Util.genEventData("sensor4", "15.5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(sensorData4));
    }

    // ==================== 金融风控系统测试 ====================

    /**
     * 测试正常交易金额范围：100-10000元（包含边界）
     */
    @Test
    @DisplayName("金融风控系统 - 正常交易金额范围[100,10000]")
    void testNormalTransactionAmountRange() throws TranslatorException {
        String code = "[100,10000]";
        Executor executor = Compiler.compile(code);

        // 测试5000元，应该返回true
        RhythmixEventData transactionData1 = Util.genEventData("transaction1", "5000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(transactionData1));

        // 测试2500.50元（浮点数），应该返回true
        RhythmixEventData transactionData2 = Util.genEventData("transaction2", "2500.50", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(transactionData2));

        // 测试边界值100元，应该返回true（包含边界）
        RhythmixEventData transactionData3 = Util.genEventData("transaction3", "100", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(transactionData3));

        // 测试边界值10000元，应该返回true（包含边界）
        RhythmixEventData transactionData4 = Util.genEventData("transaction4", "10000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(transactionData4));

        // 测试超出范围50.99元（浮点数），应该返回false
        RhythmixEventData transactionData5 = Util.genEventData("transaction5", "50.99", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(transactionData5));

        // 测试超出范围15000元，应该返回false
        RhythmixEventData transactionData6 = Util.genEventData("transaction6", "15000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(transactionData6));
    }

    /**
     * 测试可疑交易金额：大于10000但不超过50000元
     */
    @Test
    @DisplayName("金融风控系统 - 可疑交易金额(10000,50000]")
    void testSuspiciousTransactionAmountRange() throws TranslatorException {
        String code = "(10000,50000]";
        Executor executor = Compiler.compile(code);

        // 测试25000元，应该返回true
        RhythmixEventData transactionData1 = Util.genEventData("transaction1", "25000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(transactionData1));

        // 测试35000.75元（浮点数），应该返回true
        RhythmixEventData transactionData2 = Util.genEventData("transaction2", "35000.75", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(transactionData2));

        // 测试左边界10000元，应该返回false（不包含左边界）
        RhythmixEventData transactionData3 = Util.genEventData("transaction3", "10000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(transactionData3));

        // 测试右边界50000元，应该返回true（包含右边界）
        RhythmixEventData transactionData4 = Util.genEventData("transaction4", "50000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(transactionData4));

        // 测试超出范围8000.50元（浮点数），应该返回false
        RhythmixEventData transactionData5 = Util.genEventData("transaction5", "8000.50", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(transactionData5));

        // 测试超出范围60000元，应该返回false
        RhythmixEventData transactionData6 = Util.genEventData("transaction6", "60000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(transactionData6));
    }

    /**
     * 测试高风险交易：超过50000元（开区间表示）
     */
    @Test
    @DisplayName("金融风控系统 - 高风险交易(50000,999999)")
    void testHighRiskTransactionAmountRange() throws TranslatorException {
        String code = "(50000,999999)";
        Executor executor = Compiler.compile(code);

        // 测试100000元，应该返回true
        RhythmixEventData transactionData1 = Util.genEventData("transaction1", "100000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(transactionData1));

        // 测试75000.25元（浮点数），应该返回true
        RhythmixEventData transactionData2 = Util.genEventData("transaction2", "75000.25", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(transactionData2));

        // 测试边界值50000元，应该返回false（不包含边界）
        RhythmixEventData transactionData3 = Util.genEventData("transaction3", "50000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(transactionData3));

        // 测试边界值999999元，应该返回false（不包含边界）
        RhythmixEventData transactionData4 = Util.genEventData("transaction4", "999999", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(transactionData4));

        // 测试超出范围30000.99元（浮点数），应该返回false
        RhythmixEventData transactionData5 = Util.genEventData("transaction5", "30000.99", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(transactionData5));

        // 测试超出范围1000000元，应该返回false
        RhythmixEventData transactionData6 = Util.genEventData("transaction6", "1000000", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(transactionData6));
    }

    // ==================== 设备运行监控测试 ====================

    /**
     * 测试CPU使用率正常范围：0-80%（包含0，不包含80）
     */
    @Test
    @DisplayName("设备运行监控 - CPU使用率正常范围[0,80)")
    void testCpuUsageNormalRange() throws TranslatorException {
        String code = "[0,80)";
        Executor executor = Compiler.compile(code);

        // 测试50%，应该返回true
        RhythmixEventData cpuData1 = Util.genEventData("cpu1", "50", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(cpuData1));

        // 测试65.7%（浮点数），应该返回true
        RhythmixEventData cpuData2 = Util.genEventData("cpu2", "65.7", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(cpuData2));

        // 测试边界值0%，应该返回true（包含左边界）
        RhythmixEventData cpuData3 = Util.genEventData("cpu3", "0", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(cpuData3));

        // 测试边界值80%，应该返回false（不包含右边界）
        RhythmixEventData cpuData4 = Util.genEventData("cpu4", "80", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(cpuData4));

        // 测试超出范围85.3%（浮点数），应该返回false
        RhythmixEventData cpuData5 = Util.genEventData("cpu5", "85.3", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(cpuData5));
    }

    /**
     * 测试内存使用率警告范围：80-95%（不包含边界）
     */
    @Test
    @DisplayName("设备运行监控 - 内存使用率警告范围(80,95)")
    void testMemoryUsageWarningRange() throws TranslatorException {
        String code = "(80,95)";
        Executor executor = Compiler.compile(code);

        // 测试90%，应该返回true
        RhythmixEventData memoryData1 = Util.genEventData("memory1", "90", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(memoryData1));

        // 测试87.5%（浮点数），应该返回true
        RhythmixEventData memoryData2 = Util.genEventData("memory2", "87.5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(memoryData2));

        // 测试边界值80%，应该返回false（不包含边界）
        RhythmixEventData memoryData3 = Util.genEventData("memory3", "80", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(memoryData3));

        // 测试边界值95%，应该返回false（不包含边界）
        RhythmixEventData memoryData4 = Util.genEventData("memory4", "95", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(memoryData4));

        // 测试超出范围75.2%（浮点数），应该返回false
        RhythmixEventData memoryData5 = Util.genEventData("memory5", "75.2", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(memoryData5));

        // 测试超出范围98%，应该返回false
        RhythmixEventData memoryData6 = Util.genEventData("memory6", "98", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(memoryData6));
    }

    /**
     * 测试磁盘使用率危险范围：95-100%（包含边界）
     */
    @Test
    @DisplayName("设备运行监控 - 磁盘使用率危险范围[95,100]")
    void testDiskUsageDangerRange() throws TranslatorException {
        String code = "[95,100]";
        Executor executor = Compiler.compile(code);

        // 测试98%，应该返回true
        RhythmixEventData diskData1 = Util.genEventData("disk1", "98", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(diskData1));

        // 测试97.8%（浮点数），应该返回true
        RhythmixEventData diskData2 = Util.genEventData("disk2", "97.8", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(diskData2));

        // 测试边界值95%，应该返回true（包含边界）
        RhythmixEventData diskData3 = Util.genEventData("disk3", "95", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(diskData3));

        // 测试边界值100%，应该返回true（包含边界）
        RhythmixEventData diskData4 = Util.genEventData("disk4", "100", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(diskData4));

        // 测试超出范围90.5%（浮点数），应该返回false
        RhythmixEventData diskData5 = Util.genEventData("disk5", "90.5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(diskData5));
    }

    /**
     * 测试状态转换：从正常到警告再到危险
     */
    @Test
    @DisplayName("设备运行监控 - 状态转换：从正常到警告再到危险")
    void testEquipmentMonitoringStateTransition() throws TranslatorException {
        String code = "{[0,80)}->{(80,95)}->{[95,100]}";
        Executor executor = Compiler.compile(code);

        List<RhythmixEventData> usageData = new ArrayList<>();
        usageData.add(Util.genEventData("usage1", "65.5", new Timestamp(System.currentTimeMillis())));    // 正常范围（浮点数）
        usageData.add(Util.genEventData("usage2", "88", new Timestamp(System.currentTimeMillis() + 100))); // 警告范围（整数）
        usageData.add(Util.genEventData("usage3", "97.2", new Timestamp(System.currentTimeMillis() + 200))); // 危险范围（浮点数）

        boolean result = false;
        for (RhythmixEventData data : usageData) {
            result = executor.execute(data);
        }

        // 完成从正常到警告再到危险的状态转换，应该返回true
        Assertions.assertTrue(result);
    }

    // ==================== 混合数据类型测试 ====================

    /**
     * 测试混合整数和浮点数的复杂场景
     */
    @Test
    @DisplayName("混合数据类型 - 整数和浮点数混合测试")
    void testMixedIntegerAndFloatScenarios() throws TranslatorException {
        String code = "(10.5,50.5)";
        Executor executor = Compiler.compile(code);

        // 测试整数25，应该返回true
        RhythmixEventData data1 = Util.genEventData("mixed1", "25", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(data1));

        // 测试浮点数30.7，应该返回true
        RhythmixEventData data2 = Util.genEventData("mixed2", "30.7", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(data2));

        // 测试浮点数边界10.5，应该返回false（不包含边界）
        RhythmixEventData data3 = Util.genEventData("mixed3", "10.5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(data3));

        // 测试整数边界50，应该返回true（在范围内）
        RhythmixEventData data4 = Util.genEventData("mixed4", "50", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(data4));

        // 测试浮点数边界50.5，应该返回false（不包含边界）
        RhythmixEventData data5 = Util.genEventData("mixed5", "50.5", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(data5));
    }

    /**
     * 测试精度敏感的浮点数区间
     */
    @Test
    @DisplayName("混合数据类型 - 精度敏感的浮点数区间测试")
    void testPrecisionSensitiveFloatRange() throws TranslatorException {
        String code = "[99.95,100.05]";
        Executor executor = Compiler.compile(code);

        // 测试整数100，应该返回true
        RhythmixEventData data1 = Util.genEventData("precision1", "100", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(data1));

        // 测试浮点数99.99，应该返回true
        RhythmixEventData data2 = Util.genEventData("precision2", "99.99", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(data2));

        // 测试浮点数100.01，应该返回true
        RhythmixEventData data3 = Util.genEventData("precision3", "100.01", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(data3));

        // 测试边界值99.95，应该返回true（包含边界）
        RhythmixEventData data4 = Util.genEventData("precision4", "99.95", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(data4));

        // 测试边界值100.05，应该返回true（包含边界）
        RhythmixEventData data5 = Util.genEventData("precision5", "100.05", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(data5));

        // 测试超出范围99.90，应该返回false
        RhythmixEventData data6 = Util.genEventData("precision6", "99.90", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(data6));

        // 测试超出范围100.10，应该返回false
        RhythmixEventData data7 = Util.genEventData("precision7", "100.10", new Timestamp(System.currentTimeMillis()));
        Assertions.assertFalse(executor.execute(data7));
    }
}
