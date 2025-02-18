package com.df.rhythmix.lib;

import com.df.rhythmix.exception.ComputeException;
import com.df.rhythmix.util.EventData;
import com.df.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

class AviatorMathTest {

    @Test
    void sum() throws ComputeException {
        EventData p1 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "7", new Timestamp(System.currentTimeMillis()));
        EventData p3 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        List<Object> objects = new ArrayList<>();
        objects.add(p1);
        objects.add(p2);
        objects.add(p3);
        String res = AviatorMath.sum(objects);
        Assertions.assertEquals("27", res);
    }

    @Test
    void avg() throws ComputeException {
        EventData p1 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "7", new Timestamp(System.currentTimeMillis()));
        EventData p3 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        List<Object> objects = new ArrayList<>();
        objects.add(p1);
        objects.add(p2);
        objects.add(p3);
        String res = AviatorMath.avg(objects);
        Assertions.assertEquals("9.0", res);
    }

    @Test
    void count() throws ComputeException {
        EventData p1 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "7", new Timestamp(System.currentTimeMillis()));
        EventData p3 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        List<Object> objects = new ArrayList<>();
        objects.add(p1);
        objects.add(p2);
        objects.add(p3);
        String res = AviatorMath.count(objects);
        Assertions.assertEquals("3", res);
    }

    @Test
    void stddev() throws ComputeException {
        EventData p1 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        EventData p2 = Util.genEventData("1", "7", new Timestamp(System.currentTimeMillis()));
        EventData p3 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        List<Object> objects = new ArrayList<>();
        objects.add(p1);
        objects.add(p2);
        objects.add(p3);
        String stddev = AviatorMath.stddev(objects);
        Assertions.assertEquals("1.414",stddev);
    }
}