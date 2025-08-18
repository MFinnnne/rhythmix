package com.df.rhythmix.udf;

import com.df.rhythmix.config.ChainFunctionConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * CalculatorUDF Registry with auto-discovery and registration capabilities.
 * <p>
 * This class automatically scans the classpath for CalculatorUDF implementations,
 * instantiates them, and registers them with AviatorScript using the
 * addInstanceFunctions mechanism for global availability.
 * <p>
 * This is now a facade over the generic UDFRegistry for backward compatibility.
 *
 * @author MFine
 * @version 2.0
 */
@Slf4j
public class CalculatorUDFRegistry {

    /**
     * Generic UDF registry instance for CalculatorUDF
     */
    private static final UDFRegistry<CalculatorUDF> registry = new UDFRegistry<>(CalculatorUDF.class, "CalculatorUDF");

    /**
     * Performs auto-discovery and registration of all CalculatorUDF implementations
     * found in the classpath. This method is thread-safe and will only execute
     * the discovery process once.
     */
    public static void autoImportCalculatorUDFs() {
        registry.autoImportUDFs();
        CalculatorUDFRegistry.getRegisteredUdfs().forEach((udfName, udf) -> {
            ChainFunctionConfig.getInstance().addCalcFunc(udfName);
        });
    }

    /**
     * Manually register a CalculatorUDF instance. This can be used alongside
     * auto-discovery for additional CalculatorUDFs that need manual registration.
     *
     * @param calculatorUDF The CalculatorUDF instance to register
     * @return true if registration was successful, false if name already exists
     */
    public static boolean registerCalculatorUDF(CalculatorUDF calculatorUDF) {
        return registry.registerUDF(calculatorUDF);
    }

    /**
     * Get a registered CalculatorUDF by name
     *
     * @param name The name of the CalculatorUDF
     * @return The CalculatorUDF instance, or null if not found
     */
    public static CalculatorUDF getCalculatorUDF(String name) {
        return registry.getUDF(name);
    }

    /**
     * Check if a CalculatorUDF with the given name is registered
     *
     * @param name The name to check
     * @return true if a CalculatorUDF with this name is registered
     */
    public static boolean isRegistered(String name) {
        return registry.isRegistered(name);
    }

    /**
     * Get all registered CalculatorUDF names
     *
     * @return Set of all registered CalculatorUDF names
     */
    public static Set<String> getRegisteredNames() {
        return registry.getRegisteredNames();
    }

    /**
     * Get the count of registered CalculatorUDFs
     *
     * @return Number of registered CalculatorUDFs
     */
    public static int getRegisteredCount() {
        return registry.getRegisteredCount();
    }

    /**
     * Check if auto-import has been completed
     *
     * @return true if auto-import has been completed
     */
    public static boolean isAutoImportCompleted() {
        return registry.isAutoImportCompleted();
    }

    /**
     * Clear all registered CalculatorUDFs (mainly for testing purposes)
     */
    public static void clear() {
        registry.clear();
    }

    /**
     * Get all registered CalculatorUDF instances
     *
     * @return Map of all registered CalculatorUDF instances
     */
    public static Map<String, CalculatorUDF> getRegisteredUdfs() {
        return registry.getRegisteredUDFs();
    }
}
