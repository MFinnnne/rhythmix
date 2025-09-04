package com.df.rhythmix.udf;

import com.df.rhythmix.config.ChainFunctionConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * FilterUDF Registry with auto-discovery and registration capabilities.
 * <p>
 * This class automatically scans the classpath for FilterUDF implementations,
 * instantiates them, and registers them with AviatorScript using the
 * addInstanceFunctions mechanism for global availability.
 * <p>
 * This is now a facade over the generic UDFRegistry for backward compatibility.
 *
 * @author MFine
 * @version 2.0
 * @date 2025-07-18
 */
@Slf4j
public class FilterUDFRegistry {

    /**
     * Generic UDF registry instance for FilterUDF
     */
    private static final UDFRegistry<ChainFilterUDF> registry = new UDFRegistry<>(ChainFilterUDF.class, "ChainFilterUDF");

    /**
     * Performs auto-discovery and registration of all FilterUDF implementations
     * found in the classpath. This method is thread-safe and will only execute
     * the discovery process once.
     */
    public static void autoImportFilterUDFs() {
        registry.autoImportUDFs();
        FilterUDFRegistry.getRegisteredUdfs().forEach((udfName, udf) -> {
            ChainFunctionConfig.getInstance().addStartFunc(udfName);
        });
    }

    /**
     * Manually register a FilterUDF instance. This can be used alongside
     * auto-discovery for additional FilterUDFs that need manual registration.
     *
     * @param chainFilterUDF The FilterUDF instance to register
     * @return true if registration was successful, false if name already exists
     */
    public static boolean registerFilterUDF(ChainFilterUDF chainFilterUDF) {
        return registry.registerUDF(chainFilterUDF);
    }

    /**
     * Get a registered FilterUDF by name
     *
     * @param name The name of the FilterUDF
     * @return The FilterUDF instance, or null if not found
     */
    public static ChainFilterUDF getFilterUDF(String name) {
        return registry.getUDF(name);
    }

    /**
     * Check if a FilterUDF with the given name is registered
     *
     * @param name The name to check
     * @return true if a FilterUDF with this name is registered
     */
    public static boolean isRegistered(String name) {
        return registry.isRegistered(name);
    }

    /**
     * Get all registered FilterUDF names
     *
     * @return Set of all registered FilterUDF names
     */
    public static Set<String> getRegisteredNames() {
        return registry.getRegisteredNames();
    }

    /**
     * Get the count of registered FilterUDFs
     *
     * @return Number of registered FilterUDFs
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
     * Clear all registered FilterUDFs (mainly for testing purposes)
     */
    public static void clear() {
        registry.clear();
    }

    /**
     * Get all registered FilterUDF instances
     *
     * @return Map of all registered FilterUDF instances
     */
    public static Map<String, ChainFilterUDF> getRegisteredUdfs() {
        return registry.getRegisteredUDFs();
    }

}
