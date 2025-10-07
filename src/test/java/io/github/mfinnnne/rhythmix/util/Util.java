package io.github.mfinnnne.rhythmix.util;

import io.github.mfinnnne.rhythmix.execute.RhythmixExecutor;
import io.github.mfinnnne.rhythmix.translate.EnvProxy;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Util {
    public static RhythmixEventData genEventData(String pointId, String value, Timestamp ts) {
        return new RhythmixEventData(pointId, "1", value, ts);
    }

    public static Timestamp addSeconds(Timestamp oriTs, long sec) {
        long now = oriTs.getTime() + sec * 1000;
        return new Timestamp(now);
    }

    public static Timestamp addMs(Timestamp oriTs, long sec) {
        long now = oriTs.getTime() + sec;
        return new Timestamp(now);
    }

    public static Timestamp reduceSeconds(Timestamp oriTs, long sec) {
        long now = oriTs.getTime() - sec * 1000;
        return new Timestamp(now);
    }

    public static List<String> getChainProcessedQueueData(RhythmixExecutor executor) {
        final EnvProxy envProxy = executor.getEnvProxy();
        List<RhythmixEventData> data = new ArrayList<>();
        envProxy.getEnv().forEach((k, v) -> {
            if (k.contains("processedChainQueue")) {
                data.addAll(((List<RhythmixEventData>) v));
            }
        });
        return  data.stream().map(RhythmixEventData::getValue).collect(Collectors.toList());
    }

    public static List<String> getRawProcessedQueueData(RhythmixExecutor executor) {
        final EnvProxy envProxy = executor.getEnvProxy();
        List<RhythmixEventData> data = new ArrayList<>();
        envProxy.getEnv().forEach((k, v) -> {
            if (k.contains("rawChainQueue")) {
                data.addAll(((List<RhythmixEventData>) v));
            }
        });
        return  data.stream().map(RhythmixEventData::getValue).collect(Collectors.toList());
    }

}
