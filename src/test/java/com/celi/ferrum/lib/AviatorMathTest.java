package com.celi.ferrum.lib;

import cii.da.message.codec.model.PointData;
import com.celi.ferrum.exception.ComputeException;
import com.celi.ferrum.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AviatorMathTest {

    @Test
    void sum() throws ComputeException {
        PointData p1 = Util.genPointData("1", "10", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "7", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "10", new Timestamp(System.currentTimeMillis()));
        List<Object> objects = new ArrayList<>();
        objects.add(p1);
        objects.add(p2);
        objects.add(p3);
        String res = AviatorMath.sum(objects);
        Assertions.assertEquals("27", res);
    }

    @Test
    void avg() throws ComputeException {
        PointData p1 = Util.genPointData("1", "10", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "7", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "10", new Timestamp(System.currentTimeMillis()));
        List<Object> objects = new ArrayList<>();
        objects.add(p1);
        objects.add(p2);
        objects.add(p3);
        String res = AviatorMath.avg(objects);
        Assertions.assertEquals("9.0", res);
    }

    @Test
    void count() throws ComputeException {
        PointData p1 = Util.genPointData("1", "10", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "7", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "10", new Timestamp(System.currentTimeMillis()));
        List<Object> objects = new ArrayList<>();
        objects.add(p1);
        objects.add(p2);
        objects.add(p3);
        String res = AviatorMath.count(objects);
        Assertions.assertEquals("3", res);
    }

    @Test
    void stddev() throws ComputeException {
        PointData p1 = Util.genPointData("1", "10", new Timestamp(System.currentTimeMillis()));
        PointData p2 = Util.genPointData("1", "7", new Timestamp(System.currentTimeMillis()));
        PointData p3 = Util.genPointData("1", "10", new Timestamp(System.currentTimeMillis()));
        List<Object> objects = new ArrayList<>();
        objects.add(p1);
        objects.add(p2);
        objects.add(p3);
        String stddev = AviatorMath.stddev(objects);
        Assertions.assertEquals("1.414",stddev);
    }
}