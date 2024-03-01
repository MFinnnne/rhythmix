package com.celi.ferrum.lib;

import com.googlecode.aviator.AviatorEvaluator;

public class Register {

    public static void importFunction() {
        try {
            AviatorEvaluator.importFunctions(Time.class);
            AviatorEvaluator.importFunctions(AviatorQueue.class);
            AviatorEvaluator.importFunctions(AviatorMath.class);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
