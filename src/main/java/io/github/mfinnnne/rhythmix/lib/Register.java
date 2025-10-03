/*
 * author: MFine
 * date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-07-12 13:17:04
 * @LastEditors: MFine
 * @Description:
 */
package io.github.mfinnnne.rhythmix.lib;

import io.github.mfinnnne.rhythmix.udf.FilterUDFRegistry;
import io.github.mfinnnne.rhythmix.udf.CalculatorUDFRegistry;
import io.github.mfinnnne.rhythmix.udf.MeetUDFRegistry;
import com.googlecode.aviator.AviatorEvaluator;

/**
 * Utility class for registering custom functions and UDFs with the AviatorEvaluator.
 * <p>
 * This class handles the importation of built-in function classes and triggers the
 * auto-discovery and registration of various User-Defined Functions (UDFs) such as
 * {@link FilterUDFRegistry}, {@link CalculatorUDFRegistry}, and {@link MeetUDFRegistry}.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class Register {

    /**
     * Imports all built-in functions and auto-imports all discoverable UDFs.
     * <p>
     * Call once at application startup to ensure functions are available to the engine.
     *
     * @throws RuntimeException if reflection-based UDF registration fails
     */
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
