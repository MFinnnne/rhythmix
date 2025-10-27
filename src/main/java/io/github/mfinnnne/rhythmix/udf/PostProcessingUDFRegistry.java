package io.github.mfinnnne.rhythmix.udf;

import io.github.mfinnnne.rhythmix.config.ChainFunctionConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * PostProcessingUDF Registry with auto-discovery and registration capabilities.
 * <p>
 * This class automatically scans the classpath for PostProcessingUDF implementations,
 * instantiates them, and registers them with AviatorScript using the
 * addInstanceFunctions mechanism for global availability.
 * <p>
 * This is a facade over the generic UDFRegistry for backward compatibility.
 *
 * @author MFine
 * @version 1.0
 */
@Slf4j
public class PostProcessingUDFRegistry {

    /**
     * Generic UDF registry instance for PostProcessingUDF
     */
    private static final UDFRegistry<ChainPostProcessingUDF> registry = 
        new UDFRegistry<>(ChainPostProcessingUDF.class, "ChainPostProcessingUDF");

    /**
     * Performs auto-discovery and registration of all PostProcessingUDF implementations
     * found in the classpath. This method is thread-safe and will only execute
     * the discovery process once.
     */
    public static void autoImportPostProcessingUDFs() {
        registry.autoImportUDFs();
        PostProcessingUDFRegistry.getRegisteredUdfs().forEach((udfName, udf) -> 
            ChainFunctionConfig.getInstance().addPostProcessing(udfName));
    }

    /**
     * Manually register a PostProcessingUDF instance. This can be used alongside
     * auto-discovery for additional PostProcessingUDFs that need manual registration.
     *
     * @param chainPostProcessingUDF The PostProcessingUDF instance to register
     * @return true if registration was successful, false if name already exists
     */
    public static boolean registerPostProcessingUDF(ChainPostProcessingUDF chainPostProcessingUDF) {
        return registry.registerUDF(chainPostProcessingUDF);
    }

    /**
     * Get a registered PostProcessingUDF by name
     *
     * @param name The name of the PostProcessingUDF
     * @return The PostProcessingUDF instance, or null if not found
     */
    public static ChainPostProcessingUDF getPostProcessingUDF(String name) {
        return registry.getUDF(name);
    }

    /**
     * Check if a PostProcessingUDF with the given name is registered
     *
     * @param name The name to check
     * @return true if a PostProcessingUDF with this name is registered
     */
    public static boolean isRegistered(String name) {
        return registry.isRegistered(name);
    }

    /**
     * Get all registered PostProcessingUDF names
     *
     * @return Set of all registered PostProcessingUDF names
     */
    public static Set<String> getRegisteredNames() {
        return registry.getRegisteredNames();
    }

    /**
     * Get the count of registered PostProcessingUDFs
     *
     * @return Number of registered PostProcessingUDFs
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
     * Clear all registered PostProcessingUDFs (mainly for testing purposes)
     */
    public static void clear() {
        registry.clear();
    }

    /**
     * Get all registered PostProcessingUDF instances
     *
     * @return Map of all registered PostProcessingUDF instances
     */
    public static Map<String, ChainPostProcessingUDF> getRegisteredUdfs() {
        return registry.getRegisteredUDFs();
    }
}

