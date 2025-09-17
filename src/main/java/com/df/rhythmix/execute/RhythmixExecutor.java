package com.df.rhythmix.execute;

import com.df.rhythmix.lib.AviatorConfig;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.util.AviatorFunctionUtil;
import com.googlecode.aviator.Expression;
import lombok.*;

import java.util.HashMap;
import java.util.LinkedList;

@AllArgsConstructor
public class RhythmixExecutor {

    @Getter
    private String code;

    @Getter
    @Setter
    private EnvProxy envProxy;

    @Setter
    private HashMap<String, Object> originalEnv = new HashMap<>();

    public RhythmixExecutor() {
    }

    public RhythmixExecutor(String code, EnvProxy env) {
        this.code = code;
        this.envProxy = env;

        this.originalEnv.putAll(this.envProxy.getEnv());
        AviatorConfig.operatorOverloading();
    }

    public HashMap<String, Object> getOriginalEnv() {
        this.originalEnv.forEach((k, v) -> {
            if (v instanceof LinkedList) {
                this.originalEnv.put(k, new LinkedList<>());
            }
        });
        return originalEnv;
    }

    public synchronized boolean execute(Object event) {
        this.envProxy.rawPut("event", event);
        Expression expr = AviatorFunctionUtil.getExpr(code);
        Object res = expr.execute(envProxy.getEnv());
        Boolean res1 = (Boolean) res;
        if (res1) {
            resetEnv();
        }
        return res1;
    }

    public void resetEnv() {
        this.envProxy.getEnv().clear();
        this.envProxy.getEnv().putAll(this.getOriginalEnv());
    }

    public synchronized boolean execute(Object... events) {
        boolean res = false;
        Expression expr = AviatorFunctionUtil.getExpr(code);
        for (Object event : events) {
            this.envProxy.rawPut("event", event);
            res = (Boolean) expr.execute(envProxy.getEnv());
        }
        if (res) {
            resetEnv();
        }
        return res;
    }
}
