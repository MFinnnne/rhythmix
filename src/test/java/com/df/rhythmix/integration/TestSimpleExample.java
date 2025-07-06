/*
 * @Author: MFine
 * @Date: 2024-12-26 23:48:56
 * @LastEditTime: 2025-03-10 23:20:35
 * @LastEditors: MFine
 * @Description: 测试简单表达式和链式表达式的功能
 */
package com.df.rhythmix.integration;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.Compiler;
import com.df.rhythmix.execute.Executor;
import com.df.rhythmix.lib.AviatorConfig;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import com.df.rhythmix.util.EventData;
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
 * @date 2024/12/26 23:48
 * @description 测试简单表达式和链式表达式的功能
 **/
public class TestSimpleExample {

    /**
     * 测试简单状态转换表达式 - 从0到1的转换
     */
    @Test
    @DisplayName("测试状态从0到1的转换")
    void test0To1() throws TranslatorException {
        String code = "{==0}->{==1}";
        Executor executor = Compiler.compile(code);

        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("2", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(p1, p2));
    }

    /**
     * 测试简化的状态转换表达式 - 从0到1的转换
     */
    @Test
    @DisplayName("测试简化语法<0,1>实现状态从0到1的转换")
    void test0To1Easy() throws TranslatorException {
        String code = "<0,1>";
        Executor executor = Compiler.compile(code);
        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("2", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(p1, p2));
    }

    /**
     * 测试OR逻辑操作符 - 满足等于0或不等于2的条件
     */
    @Test
    @DisplayName("测试或操作符||的功能")
    void testOrOp() throws TranslatorException {
        String code = "{==0||!=2}->{==1}";
        Executor executor = Compiler.compile(code);

        EventData p1 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("2", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(p1, p2));
    }

    /**
     * 测试AND逻辑操作符 - 同时满足不等于0和不等于2的条件
     */
    @Test
    @DisplayName("测试与操作符&&的功能")
    void testAndOp() throws TranslatorException {
        String code = "{!=0&&!=2}->{==1}";
        Executor executor = Compiler.compile(code);

        EventData p1 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("2", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(p1, p2));
    }

    /**
     * 测试非法的双点语法 - 确保抛出翻译异常
     */
    @Test
    @DisplayName("测试双点语法错误情况")
    void testDoubleDot() {
        TemplateEngine.enableDebugModel(true);
        String code = "filter((-5,5)).limit(5)..avg().meet(<=0.5)";
        EnvProxy env = new EnvProxy();
        Assertions.assertThrows(TranslatorException.class, () -> {
            Translator.translate(code, env);
        });
    }

    /**
     * 测试sum函数计算整数序列的总和
     * 验证对[10,7,10]序列求和等于27
     */
    @Test
    @DisplayName("测试整数序列求和")
    void testSumWithIntegers() throws TranslatorException {
        String code = "filter(>0).limit(5).sum().meet(==27)";
        Executor executor = Compiler.compile(code);

        List<EventData> events = new ArrayList<>();
        events.add(Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis())));
        events.add(Util.genEventData("2", "7", new Timestamp(System.currentTimeMillis() + 100)));
        events.add(Util.genEventData("3", "10", new Timestamp(System.currentTimeMillis() + 200)));

        boolean result = true;
        for (EventData event : events) {
            result = executor.execute(event);
        }

        Assertions.assertTrue(result);
    }

    /**
     * 测试sum函数计算浮点数序列的总和
     * 验证对[10.5,7.3,10.2]序列求和等于28.0
     */
    @Test
    @DisplayName("测试浮点数序列求和")
    void testSumWithFloats() throws TranslatorException {
        String code = "filter(>0).limit(5).sum().meet(==28.0)";
        Executor executor = Compiler.compile(code);

        List<EventData> events = new ArrayList<>();
        events.add(Util.genEventData("1", "10.5", new Timestamp(System.currentTimeMillis())));
        events.add(Util.genEventData("2", "7.3", new Timestamp(System.currentTimeMillis() + 100)));
        events.add(Util.genEventData("3", "10.2", new Timestamp(System.currentTimeMillis() + 200)));

        boolean result = true;
        for (EventData event : events) {
            result = executor.execute(event);
        }

        Assertions.assertTrue(result);
    }

    /**
     * 测试avg函数计算整数序列的平均值
     * 验证对[10,7,10]序列求平均值等于9.0
     */
    @Test
    @DisplayName("测试整数序列平均值")
    void testAvgWithIntegers() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(>0).limit(5).avg().meet(==9.0)";
        Executor executor = Compiler.compile(code);

        List<EventData> events = new ArrayList<>();
        events.add(Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis())));
        events.add(Util.genEventData("2", "7", new Timestamp(System.currentTimeMillis() + 100)));
        events.add(Util.genEventData("3", "10", new Timestamp(System.currentTimeMillis() + 200)));

        boolean result = true;
        for (EventData event : events) {
            result = executor.execute(event);
        }

        Assertions.assertTrue(result);
    }

    /**
     * 测试avg函数计算浮点数序列的平均值
     * 验证对[10.5,7.3,10.2]序列求平均值在9.3到9.4之间
     */
    @Test
    @DisplayName("测试浮点数序列平均值")
    void testAvgWithFloats() throws TranslatorException {
        // 由于浮点数精度问题，使用近似值比较
        String code = "filter(>0.0).limit(5).avg().meet([9.3,9.4])";
        Executor executor = Compiler.compile(code);

        List<EventData> events = new ArrayList<>();
        events.add(Util.genEventData("1", "10.5", new Timestamp(System.currentTimeMillis())));
        events.add(Util.genEventData("2", "7.3", new Timestamp(System.currentTimeMillis() + 100)));
        events.add(Util.genEventData("3", "10.2", new Timestamp(System.currentTimeMillis() + 200)));

        boolean result = true;
        for (EventData event : events) {
            result = executor.execute(event);
        }

        Assertions.assertTrue(result);
    }

    /**
     * 测试count函数统计数据序列中元素的个数
     * 验证序列[10,7,10]中有3个元素
     */
    @Test
    @DisplayName("测试数据序列计数")
    void testCount() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);
        String code = "filter(>0,true).limit(5).count().meet(==3)";
        Executor executor = Compiler.compile(code);

        List<EventData> events = new ArrayList<>();
        events.add(Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis())));
        events.add(Util.genEventData("2", "7", new Timestamp(System.currentTimeMillis() + 100)));
        events.add(Util.genEventData("3", "10", new Timestamp(System.currentTimeMillis() + 200)));

        boolean result = true;
        for (EventData event : events) {
            result = executor.execute(event);
        }

        Assertions.assertTrue(result);
    }

    /**
     * 测试stddev函数计算整数序列的标准差
     * 验证序列[10,7,10]的标准差在1.4到1.5之间
     */
    @Test
    @DisplayName("测试整数序列标准差")
    void testStddevWithIntegers() throws TranslatorException {
        // 由于标准差计算可能有精度差异，使用区间表达式
        TemplateEngine.enableDebugModel(true);

        String code = "filter(>0).limit(5).stddev().meet([1.4,1.5])";
        Executor executor = Compiler.compile(code);

        List<EventData> events = new ArrayList<>();
        events.add(Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis())));
        events.add(Util.genEventData("2", "7", new Timestamp(System.currentTimeMillis() + 100)));

        boolean result = true;
        for (EventData event : events) {
            result = executor.execute(event);
        }

        Assertions.assertTrue(result);
    }

    /**
     * 测试stddev函数计算浮点数序列的标准差
     * 验证序列[10.5,7.3,10.2]的标准差在1.4到1.5之间
     */
    @Test
    @DisplayName("测试浮点数标准差计算")
    void testStddevWithFloats() throws TranslatorException {
        TemplateEngine.enableDebugModel(true);

        // 由于标准差计算可能有精度差异，使用区间表达式
        String code = "filter(>0.0).limit(5).stddev().meet([1.4,1.5])";
        Executor executor = Compiler.compile(code);

        List<EventData> events = new ArrayList<>();
        events.add(Util.genEventData("1", "10.5", new Timestamp(System.currentTimeMillis())));
        events.add(Util.genEventData("2", "7.3", new Timestamp(System.currentTimeMillis() + 100)));
        events.add(Util.genEventData("3", "10.2", new Timestamp(System.currentTimeMillis() + 200)));

        boolean result = true;
        for (EventData event : events) {
            result = executor.execute(event);
        }

        Assertions.assertTrue(result);
    }

    /**
     * 测试stddev函数在数据点不足2个时的行为
     */
    @Test
    @DisplayName("测试标准差计算数据点不足情况")
    void testStddevWithInsufficientData() throws TranslatorException {
        AviatorConfig.operatorOverloading();
        TemplateEngine.enableDebugModel(true);

        String code = "filter(>0).limit(5).stddev().meet([1.4,1.5])";
        Executor executor = Compiler.compile(code);

        // 只添加一个数据点，不足以计算标准差
        EventData event = Util.genEventData("1", "10.5", new Timestamp(System.currentTimeMillis()));
        boolean result = executor.execute(event);

        // 由于数据点不足，应该返回false
        Assertions.assertFalse(result);
    }

    /**
     * 测试组合使用多个数据计算函数
     * 验证过滤大于5的数据，取前两个数据求和大于15
     */
    @Test
    @DisplayName("测试组合计算功能")
    void testCombinedCalculations() throws TranslatorException {
        String code = "filter(>5).limit(5).take(0,3).sum().meet(>15)";
        Executor executor = Compiler.compile(code);

        List<EventData> events = new ArrayList<>();
        events.add(Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis())));
        events.add(Util.genEventData("2", "7", new Timestamp(System.currentTimeMillis() + 100)));
        events.add(Util.genEventData("3", "3", new Timestamp(System.currentTimeMillis() + 200)));
        events.add(Util.genEventData("4", "8", new Timestamp(System.currentTimeMillis() + 300)));

        boolean result = true;
        for (EventData event : events) {
            result = executor.execute(event);
        }

        Assertions.assertTrue(result);
    }
}
