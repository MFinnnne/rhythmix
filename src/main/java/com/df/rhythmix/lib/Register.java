/*
 * @Author: MFine
 * @Date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-07-12 13:17:04
 * @LastEditors: MFine
 * @Description: 
 */
package com.df.rhythmix.lib;

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
