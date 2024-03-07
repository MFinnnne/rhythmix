package com.df.rhythmix.execute;

import com.df.rhythmix.lib.Register;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.util.AviatorFunctionUtil;
import com.googlecode.aviator.Expression;
import lombok.*;

import java.util.HashMap;
import java.util.LinkedList;

@AllArgsConstructor
@NoArgsConstructor
public class Executor {

    @Getter
    private String code;

    @Getter
    @Setter
    private EnvProxy envpProxy;

    @Setter
    private HashMap<String, Object> originalEnv = new HashMap<>();

    static {
        Register.importFunction();
    }

    public Executor(String code, EnvProxy env) {
        this.code = code;
        this.envpProxy = env;
        this.originalEnv.putAll(this.envpProxy.getEnv());
    }

    public HashMap<String, Object> getOriginalEnv() {
        this.originalEnv.forEach((k, v) -> {
            if (v instanceof LinkedList) {
                this.originalEnv.put(k, new LinkedList<>());
            }
        });
        return originalEnv;
    }

    public boolean execute(Object event) {
        this.envpProxy.rawPut("event", event);
        Expression expr = AviatorFunctionUtil.getExpr(code);
        Object res = expr.execute(envpProxy.getEnv());
        Boolean res1 = (Boolean) res;
        if (res1) {
            resetEnv();
        }
        return res1;
    }

    public void resetEnv() {
        this.envpProxy.getEnv().clear();
        this.envpProxy.getEnv().putAll(this.getOriginalEnv());
    }

    public boolean execute(Object... events) {
        boolean res = false;
        Expression expr = AviatorFunctionUtil.getExpr(code);
        for (Object event : events) {
            this.envpProxy.rawPut("event", event);
            res = (Boolean) expr.execute(envpProxy.getEnv());
        }
        if (res) {
            resetEnv();
        }
        return res;
    }
}
