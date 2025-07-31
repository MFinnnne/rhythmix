package com.df.rhythmix.udf;

import cn.hutool.core.util.ClassUtil;
import com.googlecode.aviator.AviatorEvaluator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FilterUDF Registry with auto-discovery and registration capabilities.
 * <p>
 * This class automatically scans the classpath for FilterUDF implementations,
 * instantiates them, and registers them with AviatorScript using the
 * addInstanceFunctions mechanism for global availability.
 *
 * @author MFine
 * @version 1.0
 * @date 2025-07-18
 */
@Slf4j
public class FilterUDFRegistry {

    /**
     * Thread-safe map to store registered FilterUDF instances by name
     */
    private static final Map<String, FilterUDF> REGISTERED_UDFS = new ConcurrentHashMap<>();

    /**
     * Flag to ensure auto-import is only performed once
     * -- GETTER --
     * Check if auto-import has been completed
     *
     * @return true if auto-import has been completed
     */
    @Getter
    private static volatile boolean autoImportCompleted = false;

    /**
     * Lock object for synchronizing auto-import process
     */
    private static final Object IMPORT_LOCK = new Object();

    /**
     * Performs auto-discovery and registration of all FilterUDF implementations
     * found in the classpath. This method is thread-safe and will only execute
     * the discovery process once.
     */
    public static void autoImportFilterUDFs() {
        if (autoImportCompleted) {
            return;
        }

        synchronized (IMPORT_LOCK) {
            if (autoImportCompleted) {
                return;
            }

            log.info("Starting auto-discovery of FilterUDF implementations...");

            try {
                // Scan the entire classpath for FilterUDF implementations
                Set<Class<?>> filterUDFClasses = ClassUtil.scanPackageBySuper("", FilterUDF.class);

                int successCount = 0;
                int failureCount = 0;

                for (Class<?> clazz : filterUDFClasses) {
                    try {
                        // Skip interfaces and abstract classes
                        if (clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                            continue;
                        }

                        // Attempt to instantiate the FilterUDF
                        FilterUDF filterUDF = (FilterUDF) clazz.getDeclaredConstructor().newInstance();
                        String udfName = filterUDF.getName();

                        // Check for duplicate names
                        if (REGISTERED_UDFS.containsKey(udfName)) {
                            log.warn("Duplicate FilterUDF name '{}' found in class {}. Skipping registration.",
                                    udfName, clazz.getName());
                            failureCount++;
                            continue;
                        }

                        // Register with AviatorScript using addInstanceFunctions
                        AviatorEvaluator.addInstanceFunctions(udfName, filterUDF.getClass());
                        // Store in our registry for tracking
                        REGISTERED_UDFS.put(udfName, filterUDF);

                        log.info("Successfully registered FilterUDF: {} (class: {})", udfName, clazz.getName());
                        successCount++;

                    } catch (InstantiationException e) {
                        log.warn("Failed to instantiate FilterUDF class {}: No default constructor available",
                                clazz.getName());
                        failureCount++;
                    } catch (IllegalAccessException e) {
                        log.warn("Failed to instantiate FilterUDF class {}: Constructor not accessible",
                                clazz.getName());
                        failureCount++;
                    } catch (InvocationTargetException e) {
                        log.warn("Failed to instantiate FilterUDF class {}: Constructor threw exception: {}",
                                clazz.getName(), e.getCause().getMessage());
                        failureCount++;
                    } catch (NoSuchMethodException e) {
                        log.warn("Failed to instantiate FilterUDF class {}: No default constructor found",
                                clazz.getName());
                        failureCount++;
                    } catch (Exception e) {
                        log.error("Unexpected error while registering FilterUDF class {}: {}",
                                clazz.getName(), e.getMessage(), e);
                        failureCount++;
                    }
                }

                log.info("FilterUDF auto-discovery completed. Successfully registered: {}, Failed: {}",
                        successCount, failureCount);

            } catch (Exception e) {
                log.error("Error during FilterUDF auto-discovery: {}", e.getMessage(), e);
            } finally {
                autoImportCompleted = true;
            }
        }
    }

    /**
     * Manually register a FilterUDF instance. This can be used alongside
     * auto-discovery for additional FilterUDFs that need manual registration.
     *
     * @param filterUDF The FilterUDF instance to register
     * @return true if registration was successful, false if name already exists
     */
    public static boolean registerFilterUDF(FilterUDF filterUDF) {
        if (filterUDF == null) {
            log.warn("Attempted to register null FilterUDF");
            return false;
        }

        String udfName = filterUDF.getName();
        if (udfName == null || udfName.trim().isEmpty()) {
            log.warn("FilterUDF returned null or empty name: {}", filterUDF.getClass().getName());
            return false;
        }

        if (REGISTERED_UDFS.containsKey(udfName)) {
            log.warn("FilterUDF with name '{}' already registered. Skipping.", udfName);
            return false;
        }

        try {
            // Register with AviatorScript
            AviatorEvaluator.addInstanceFunctions(udfName, filterUDF.getClass());

            // Store in our registry
            REGISTERED_UDFS.put(udfName, filterUDF);

            log.info("Manually registered FilterUDF: {} (class: {})", udfName, filterUDF.getClass().getName());
            return true;

        } catch (Exception e) {
            log.error("Failed to register FilterUDF '{}': {}", udfName, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get a registered FilterUDF by name
     *
     * @param name The name of the FilterUDF
     * @return The FilterUDF instance, or null if not found
     */
    public static FilterUDF getFilterUDF(String name) {
        return REGISTERED_UDFS.get(name);
    }

    /**
     * Check if a FilterUDF with the given name is registered
     *
     * @param name The name to check
     * @return true if a FilterUDF with this name is registered
     */
    public static boolean isRegistered(String name) {
        return REGISTERED_UDFS.containsKey(name);
    }

    /**
     * Get all registered FilterUDF names
     *
     * @return Set of all registered FilterUDF names
     */
    public static Set<String> getRegisteredNames() {
        return REGISTERED_UDFS.keySet();
    }

    /**
     * Get the count of registered FilterUDFs
     *
     * @return Number of registered FilterUDFs
     */
    public static int getRegisteredCount() {
        return REGISTERED_UDFS.size();
    }

    /**
     * Clear all registered FilterUDFs (mainly for testing purposes)
     */
    public static void clear() {
        REGISTERED_UDFS.clear();
        autoImportCompleted = false;
    }


    public static Map<String, FilterUDF> getRegisteredUdfs() {
        return REGISTERED_UDFS;
    }

}
