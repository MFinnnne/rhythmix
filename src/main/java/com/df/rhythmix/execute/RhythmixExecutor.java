package com.df.rhythmix.execute;

import com.df.rhythmix.lib.AviatorConfig;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.util.AviatorFunctionUtil;
import com.googlecode.aviator.Expression;
import lombok.*;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Executes compiled Rhythmix code.
 * <p>
 * This class takes the translated code and the environment from the {@link RhythmixCompiler}
 * and executes it against event data. It manages the execution state and environment,
 * including resetting the environment after a successful execution that returns {@code true}.
 * <p>
 * Thread-safety: methods {@link #execute(Object)} and {@link #execute(Object...)} are synchronized
 * to avoid concurrent access to the shared {@link EnvProxy}.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
@AllArgsConstructor
public class RhythmixExecutor {

    @Getter
    private String code;

    @Getter
    @Setter
    private EnvProxy envProxy;

    @Setter
    private HashMap<String, Object> originalEnv = new HashMap<>();

    /**
     * Default constructor.
     */
    public RhythmixExecutor() {
    }

    /**
     * Constructs a RhythmixExecutor with the translated code and execution environment.
     *
     * @param code the translated code string
     * @param env  the execution environment proxy
     */
    public RhythmixExecutor(String code, EnvProxy env) {
        this.code = code;
        this.envProxy = env;

        this.originalEnv.putAll(this.envProxy.getEnv());
        AviatorConfig.operatorOverloading();
    }

    /**
     * Gets a fresh copy of the original environment.
     * This is used to reset the environment state. Any {@link LinkedList} instances
     * are replaced with new, empty instances to clear their state.
     *
     * @return a clean initial environment map
     */
    public HashMap<String, Object> getOriginalEnv() {
        this.originalEnv.forEach((k, v) -> {
            if (v instanceof LinkedList) {
                this.originalEnv.put(k, new LinkedList<>());
            }
        });
        return originalEnv;
    }

    /**
     * Executes the compiled code against a single event.
     * The environment is reset if the execution returns {@code true}.
     *
     * @param event the event data to process
     * @return {@code true} if the expression's conditions are met; {@code false} otherwise
     */
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

    /**
     * Resets the execution environment to its original state.
     * This clears any accumulated state from previous executions.
     */
    public void resetEnv() {
        this.envProxy.getEnv().clear();
        this.envProxy.getEnv().putAll(this.getOriginalEnv());
    }

    /**
     * Executes the compiled code against a sequence of events.
     * The environment is reset if the final execution returns {@code true}.
     *
     * @param events a variable number of event data objects to process in sequence
     * @return {@code true} if the expression's conditions are met after processing all events; {@code false} otherwise
     */
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
