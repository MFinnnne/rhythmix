package com.df.rhythmix.udf;

import com.df.rhythmix.config.ChainFunctionConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * MeetUDF Registry with auto-discovery and registration capabilities.
 * <p>
 * This class automatically scans the classpath for MeetUDF implementations,
 * instantiates them, and registers them with AviatorScript using the
 * addInstanceFunctions mechanism for global availability.
 * <p>
 * This is a facade over the generic UDFRegistry for backward compatibility.
 *
 * @author MFine
 * @version 1.0
 */
@Slf4j
public class MeetUDFRegistry {

    /**
     * Generic UDF registry instance for MeetUDF
     */
    private static final UDFRegistry<ChainMeetUDF> registry = new UDFRegistry<>(ChainMeetUDF.class, "ChainMeetUDF");

    /**
     * Performs auto-discovery and registration of all MeetUDF implementations
     * found in the classpath. This method is thread-safe and will only execute
     * the discovery process once.
     */
    public static void autoImportMeetUDFs() {
        registry.autoImportUDFs();
        MeetUDFRegistry.getRegisteredUdfs().forEach((udfName, udf) -> ChainFunctionConfig.getInstance().addEndFunc(udfName));
    }

    /**
     * Manually register a MeetUDF instance. This can be used alongside
     * auto-discovery for additional MeetUDFs that need manual registration.
     *
     * @param chainMeetUDF The MeetUDF instance to register
     * @return true if registration was successful, false if name already exists
     */
    public static boolean registerMeetUDF(ChainMeetUDF chainMeetUDF) {
        return registry.registerUDF(chainMeetUDF);
    }

    /**
     * Get a registered MeetUDF by name
     *
     * @param name The name of the MeetUDF
     * @return The MeetUDF instance, or null if not found
     */
    public static ChainMeetUDF getMeetUDF(String name) {
        return registry.getUDF(name);
    }

    /**
     * Check if a MeetUDF with the given name is registered
     *
     * @param name The name to check
     * @return true if a MeetUDF with this name is registered
     */
    public static boolean isRegistered(String name) {
        return registry.isRegistered(name);
    }

    /**
     * Get all registered MeetUDF names
     *
     * @return Set of all registered MeetUDF names
     */
    public static Set<String> getRegisteredNames() {
        return registry.getRegisteredNames();
    }

    /**
     * Get the count of registered MeetUDFs
     *
     * @return Number of registered MeetUDFs
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
     * Clear all registered MeetUDFs (mainly for testing purposes)
     */
    public static void clear() {
        registry.clear();
    }

    /**
     * Get all registered MeetUDF instances
     *
     * @return Map of all registered MeetUDF instances
     */
    public static Map<String, ChainMeetUDF> getRegisteredUdfs() {
        return registry.getRegisteredUDFs();
    }
}
