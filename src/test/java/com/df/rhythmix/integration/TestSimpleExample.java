package com.df.rhythmix.integration;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.execute.Compiler;
import com.df.rhythmix.execute.Executor;
import com.df.rhythmix.util.EventData;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

/**
 * @author MFine
 * @version 1.0
 * @date 2024/12/26 23:48
 **/
public class TestSimpleExample {
    @Test
    void test0To1() throws TranslatorException {
        String code = "{==0}->{==1}";
        Executor executor = Compiler.compile(code);

        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("2", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(p1, p2));
    }

    @Test
    void test0To1Easy() throws TranslatorException {
        String code = "<0,1>";
        Executor executor = Compiler.compile(code);
        EventData p1 = Util.genEventData("1", "0", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("2", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(p1, p2));
    }

    @Test
    void testOrOp() throws TranslatorException {
        String code = "{==0||!=2}->{==1}";
        Executor executor = Compiler.compile(code);

        EventData p1 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("2", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(p1, p2));
    }

    @Test
    void testAndOp() throws TranslatorException {
        String code = "{!=0&&!=2}->{==1}";
        Executor executor = Compiler.compile(code);

        EventData p1 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("2", "1", new Timestamp(System.currentTimeMillis()));
        Assertions.assertTrue(executor.execute(p1, p2));
    }
}
