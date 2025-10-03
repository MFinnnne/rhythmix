package io.github.mfinnnne.rhythmix.udf;

import cn.hutool.core.util.ClassUtil;
import com.googlecode.aviator.AviatorEvaluator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Generic UDF Registry with auto-discovery and registration capabilities.
 * <p>
 * This class provides a generic framework for automatically scanning the classpath
 * for UDF implementations, instantiating them, and registering them with AviatorScript.
 * It supports any UDF interface type through generics.
 *
 * @param <T> The UDF interface type (e.g., FilterUDF, CalculatorUDF)
 * author MFine
 * version 1.0
 */
@Slf4j
public class UDFRegistry<T> {

    /**
     * Thread-safe map to store registered UDF instances by name
     * -- GETTER --
     *  Get all registered UDF instances
     *
     * @return Map of all registered UDF instances

     */
    @Getter
    private final Map<String, T> registeredUDFs = new ConcurrentHashMap<>();

    /**
     * Flag to ensure auto-import is only performed once
     * -- GETTER --
     *  Check if auto-import has been completed
     *
     * @return true if auto-import has been completed

     */
    @Getter
    private volatile boolean autoImportCompleted = false;

    /**
     * Lock object for synchronizing auto-import process
     */
    private final Object importLock = new Object();

    /**
     * The UDF interface class
     */
    private final Class<T> udfInterface;

    /**
     * Display name for logging purposes
     */
    private final String displayName;

    /**
     * Constructor for creating a UDF registry for a specific interface type
     *
     * @param udfInterface The UDF interface class
     * @param displayName Display name for logging
     */
    public UDFRegistry(Class<T> udfInterface, String displayName) {
        this.udfInterface = udfInterface;
        this.displayName = displayName;
    }

    /**
     * Performs auto-discovery and registration of all UDF implementations
     * found in the classpath. This method is thread-safe and will only execute
     * the discovery process once.
     */
    public void autoImportUDFs() {
        if (autoImportCompleted) {
            return;
        }

        synchronized (importLock) {
            if (autoImportCompleted) {
                return;
            }

            log.info("Starting auto-discovery of {} implementations...", displayName);

            try {
                // Scan the entire classpath for UDF implementations
                Set<Class<?>> udfClasses = ClassUtil.scanPackageBySuper("", udfInterface);

                int successCount = 0;
                int failureCount = 0;

                for (Class<?> clazz : udfClasses) {
                    try {
                        // Skip interfaces and abstract classes
                        if (clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                            continue;
                        }

                        // Attempt to instantiate the UDF
                        @SuppressWarnings("unchecked")
                        T udf = (T) clazz.getDeclaredConstructor().newInstance();
                        String udfName = getUDFName(udf);

                        // Check for duplicate names
                        if (registeredUDFs.containsKey(udfName)) {
                            log.warn("Duplicate {} name '{}' found in class {}. Skipping registration.",
                                    displayName, udfName, clazz.getName());
                            failureCount++;
                            continue;
                        }

                        // Register with AviatorScript using addInstanceFunctions
                        AviatorEvaluator.addInstanceFunctions(udfName, clazz);
                        // Store in our registry for tracking
                        registeredUDFs.put(udfName, udf);
                        log.info("Successfully registered {}: {} (class: {})", displayName, udfName, clazz.getName());
                        successCount++;

                    } catch (InstantiationException e) {
                        log.warn("Failed to instantiate {} class {}: No default constructor available",
                                displayName, clazz.getName());
                        failureCount++;
                    } catch (IllegalAccessException e) {
                        log.warn("Failed to instantiate {} class {}: Constructor not accessible",
                                displayName, clazz.getName());
                        failureCount++;
                    } catch (InvocationTargetException e) {
                        log.warn("Failed to instantiate {} class {}: Constructor threw exception: {}",
                                displayName, clazz.getName(), e.getCause().getMessage());
                        failureCount++;
                    } catch (NoSuchMethodException e) {
                        log.warn("Failed to instantiate {} class {}: No default constructor found",
                                displayName, clazz.getName());
                        failureCount++;
                    } catch (Exception e) {
                        log.error("Unexpected error while registering {} class {}: {}",
                                displayName, clazz.getName(), e.getMessage(), e);
                        failureCount++;
                    }
                }

                log.info("{} auto-discovery completed. Successfully registered: {}, Failed: {}",
                        displayName, successCount, failureCount);

            } catch (Exception e) {
                log.error("Error during {} auto-discovery: {}", displayName, e.getMessage(), e);
            } finally {
                autoImportCompleted = true;
            }
        }
    }

    /**
     * Manually register a UDF instance. This can be used alongside
     * auto-discovery for additional UDFs that need manual registration.
     *
     * @param udf The UDF instance to register
     * @return true if registration was successful, false if name already exists
     */
    public boolean registerUDF(T udf) {
        if (udf == null) {
            log.warn("Attempted to register null {}", displayName);
            return false;
        }

        String udfName = getUDFName(udf);
        if (udfName == null || udfName.trim().isEmpty()) {
            log.warn("{} returned null or empty name: {}", displayName, udf.getClass().getName());
            return false;
        }

        if (registeredUDFs.containsKey(udfName)) {
            log.warn("{} with name '{}' already registered. Skipping.", displayName, udfName);
            return false;
        }

        try {
            // Register with AviatorScript
            AviatorEvaluator.addInstanceFunctions(udfName, udf.getClass());

            // Store in our registry
            registeredUDFs.put(udfName, udf);

            log.info("Manually registered {}: {} (class: {})", displayName, udfName, udf.getClass().getName());
            return true;

        } catch (Exception e) {
            log.error("Failed to register {} '{}': {}", displayName, udfName, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get a registered UDF by name
     *
     * @param name The name of the UDF
     * @return The UDF instance, or null if not found
     */
    public T getUDF(String name) {
        return registeredUDFs.get(name);
    }

    /**
     * Check if a UDF with the given name is registered
     *
     * @param name The name to check
     * @return true if a UDF with this name is registered
     */
    public boolean isRegistered(String name) {
        return registeredUDFs.containsKey(name);
    }

    /**
     * Get all registered UDF names
     *
     * @return Set of all registered UDF names
     */
    public Set<String> getRegisteredNames() {
        return registeredUDFs.keySet();
    }

    /**
     * Get the count of registered UDFs
     *
     * @return Number of registered UDFs
     */
    public int getRegisteredCount() {
        return registeredUDFs.size();
    }

    /**
     * Clear all registered UDFs (mainly for testing purposes)
     */
    public void clear() {
        registeredUDFs.clear();
        autoImportCompleted = false;
    }

    /**
     * Extract the name from a UDF instance using reflection
     *
     * @param udf The UDF instance
     * @return The UDF name
     */
    private String getUDFName(T udf) {
        try {
            if (udf instanceof ChainFilterUDF) {
                return ((ChainFilterUDF) udf).getName();
            } else if (udf instanceof ChainCalculatorUDF) {
                return ((ChainCalculatorUDF) udf).getName();
            } else if (udf instanceof ChainMeetUDF) {
                return ((ChainMeetUDF) udf).getName();
            } else {
                // Try to call getName() method via reflection
                return (String) udf.getClass().getMethod("getName").invoke(udf);
            }
        } catch (Exception e) {
            log.error("Failed to get name from {} instance: {}", displayName, e.getMessage());
            return null;
        }
    }
}
