package io.github.mfinnnne.rhythmix.lib;

import io.github.mfinnnne.rhythmix.exception.ComputeException;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import io.github.mfinnnne.rhythmix.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

class AviatorMathTest {

    @Test
    void sum() throws ComputeException {
        RhythmixEventData p1 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "7", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        List<Object> objects = new ArrayList<>();
        objects.add(p1);
        objects.add(p2);
        objects.add(p3);
        Number res = AviatorMath.sum(objects);
        Assertions.assertEquals(27, res);
    }

    @Test
    void avg() throws ComputeException {
        RhythmixEventData p1 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "7", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        List<Object> objects = new ArrayList<>();
        objects.add(p1);
        objects.add(p2);
        objects.add(p3);
        Number res = AviatorMath.avg(objects);
        Assertions.assertEquals(9.0, res);
    }

    @Test
    void count() throws ComputeException {
        RhythmixEventData p1 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "7", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        List<Object> objects = new ArrayList<>();
        objects.add(p1);
        objects.add(p2);
        objects.add(p3);
        Number res = AviatorMath.count(objects);
        Assertions.assertEquals(3, res);
    }

    @Test
    void stddev() throws ComputeException {
        RhythmixEventData p1 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p2 = Util.genEventData("1", "7", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData p3 = Util.genEventData("1", "10", new Timestamp(System.currentTimeMillis()));
        List<Object> objects = new ArrayList<>();
        objects.add(p1);
        objects.add(p2);
        objects.add(p3);
        Number stddev = AviatorMath.stddev(objects);
        Assertions.assertEquals("1.4142135623730951",stddev.toString());
    }
}