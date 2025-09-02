/*
 * @Author: MFine
 * @Date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-07-12 13:17:04
 * @LastEditors: MFine
 * @Description:
 */
package com.df.rhythmix.lib;

import com.df.rhythmix.udf.FilterUDFRegistry;
import com.df.rhythmix.udf.CalculatorUDFRegistry;
import com.df.rhythmix.udf.MeetUDFRegistry;
import com.googlecode.aviator.AviatorEvaluator;

public class Register {

    public static void importFunction() {
        try {
            // Import existing static function classes
            AviatorEvaluator.importFunctions(Time.class);
            AviatorEvaluator.importFunctions(AviatorQueue.class);
            AviatorEvaluator.importFunctions(AviatorMath.class);

            // Auto-import FilterUDF instances
            FilterUDFRegistry.autoImportFilterUDFs();

            // Auto-import CalculatorUDF instances
            CalculatorUDFRegistry.autoImportCalculatorUDFs();

            // Auto-import MeetUDF instances
            MeetUDFRegistry.autoImportMeetUDFs();

        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
