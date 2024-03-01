package com.df.rhythmix.util;

import cii.da.message.codec.model.SensorEvent;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.stream.Stream;

@Slf4j
public class Util {
    public static SensorEvent genPointData(String pointId, String value, Timestamp ts) {
        String dataType = "string";
        if (value.equals("true") || value.equals("false")) {
            dataType = "bool";
        }
        try {
            Integer.parseInt(value);
            dataType = "int";
        } catch (Exception ignore) {

        }
        try {
            Float.parseFloat(value);
            dataType = "float";
        } catch (Exception ignore) {
        }

        return new SensorEvent(pointId, pointId, "1", RandomUtil.randomString(3), ts,
                pointId, dataType, value, dataType, "nj", String.valueOf(RandomUtil.randomInt(10)), String.valueOf(RandomUtil.randomInt(10)),
                RandomUtil.randomString(4), "k1", "k2", "k3", "k4", "k5");
    }

    public static Timestamp addSeconds(Timestamp oriTs, long sec) {
        long now = oriTs.getTime() + sec * 1000;
        return new Timestamp(now);
    }

    public static Timestamp addMs(Timestamp oriTs, long sec) {
        long now = oriTs.getTime() + sec ;
        return new Timestamp(now);
    }

    public static Timestamp reduceSeconds(Timestamp oriTs, long sec) {
        long now = oriTs.getTime() - sec * 1000;
        return new Timestamp(now);
    }


    public static void readFile(String pathname) throws IOException {
        FileWriter fileWriter = new FileWriter("D:\\celi\\project\\cii_code\\cii\\cii-process\\cii-virtual-point\\src\\test\\resources\\xg\\cg\\data.txt", true);
        try (Stream<String> stream = Files.lines(Paths.get(FileUtil.getAbsolutePath(pathname)))) {
            stream.forEach(item -> {
                if (item.contains("倾动角度")) {
                    try {
                        fileWriter.append(item);
                        fileWriter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


}
