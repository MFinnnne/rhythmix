package io.github.mfinnnne.rhythmix.execute;

import io.github.mfinnnne.rhythmix.config.RhythmixConfig;
import io.github.mfinnnne.rhythmix.exception.RhythmixException;
import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.monitor.ExecutionRecord;
import io.github.mfinnnne.rhythmix.monitor.RhythmixMonitor;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the lifecycle of RhythmixExecutor instances.
 * <p>
 * This class provides centralized management for creating, updating, deleting, enabling,
 * and disabling RhythmixExecutor instances. It maintains a cache of compiled executors
 * for efficient reuse and integrates with the {@link RhythmixMonitor} interface to
 * trigger appropriate lifecycle callbacks.
 * <p>
 * Thread-safety: This class is thread-safe and can be used in concurrent environments.
 * All operations on the executor cache are synchronized using a {@link ConcurrentHashMap}.
 * <p>
 * Typical usage:
 * <pre>{@code
 * RhythmixExecutorManager manager = RhythmixExecutorManager.getInstance();
 *
 * // Create a new executor
 * RhythmixExpressionEntity entity = new RhythmixExpressionEntity();
 * entity.setId("expr-1");
 * entity.setExpression("a > 1 && b < 3");
 * entity.setEnable(true);
 *
 * RhythmixExecutor executor = manager.create(entity);
 *
 * // Execute events
 * boolean result = executor.execute(eventData);
 *
 * // Update the expression
 * entity.setExpression("a > 2 && b < 5");
 * manager.update(entity);
 *
 * // Disable the executor
 * manager.disable(entity);
 *
 * // Delete the executor
 * manager.delete(entity);
 * }</pre>
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
@Slf4j
public class RhythmixExecutorManager {

    /**
     * Singleton instance of the manager.
     */
    private static volatile RhythmixExecutorManager instance;

    /**
     * Lock object for double-checked locking pattern.
     */
    private static final Object LOCK = new Object();

    /**
     * Cache of compiled executors, keyed by expression entity ID.
     * Uses ConcurrentHashMap for thread-safe operations.
     */
    private final Map<String, ExecutorWrapper> executorCache;

    private final Map<String, List<ExecutorWrapper>> executorRouteMap;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private RhythmixExecutorManager() {
        this.executorCache = new ConcurrentHashMap<>();
        this.executorRouteMap = new ConcurrentHashMap<>();
    }


    /**
     * Gets the singleton instance of the RhythmixExecutorManager.
     * Uses double-checked locking for thread-safe lazy initialization.
     *
     * @return the singleton instance
     */
    public static RhythmixExecutorManager getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new RhythmixExecutorManager();
                }
            }
        }
        return instance;
    }

    /**
     * Creates a new executor from the expression entity.
     * <p>
     * This method compiles the expression using {@link RhythmixCompiler}, stores the
     * created executor in the cache, and triggers the {@link RhythmixMonitor#onExpressionCreated}
     * callback.
     * <p>
     * If an executor with the same ID already exists, it will be replaced.
     *
     * @param entity the expression entity containing the expression to compile
     * @return the created RhythmixExecutor instance
     * @throws TranslatorException if compilation fails
     * @throws IllegalArgumentException if entity or entity ID is null
     */
    public RhythmixExecutor create(RhythmixExpressionEntity entity) throws TranslatorException {

        try {
            log.debug("Creating executor for expression ID: {}", entity.getId());

            // Compile the expression
            RhythmixExecutor executor = RhythmixCompiler.compile(entity.getExpression());

            // Create wrapper with metadata
            ExecutorWrapper wrapper = ExecutorWrapper.builder()
                    .id(entity.getId())
                    .executor(executor)
                    .entity(entity)
                    .enabled(entity.isEnable())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Store in cache
            executorCache.put(entity.getId(), wrapper);
            entity.getFilterIds().forEach((filterId) -> {
                executorRouteMap.computeIfAbsent(filterId, k -> new ArrayList<>()).add(wrapper);
                executorRouteMap.computeIfPresent(filterId, (k, v) -> {
                    v.add(wrapper);
                    return v;
                });
            });
            // Notify monitor of successful creation
            invokeMonitorSafely(() -> getMonitor().onExpressionCreated(entity));

            log.info("Successfully created executor for expression ID: {}", entity.getId());
            return executor;

        } catch (Exception e) {
            // Notify monitor of compilation error for non-RhythmixException
            invokeMonitorSafely(() -> getMonitor().onCompilationError(entity, e.getMessage()));
            log.error("Failed to create executor for expression ID: {}", entity.getId(), e);
            throw new TranslatorException("Compilation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing executor by recompiling the expression.
     * <p>
     * This method recompiles the expression, replaces the stored executor in the cache,
     * and triggers the {@link RhythmixMonitor#onExpressionUpdated} callback.
     * <p>
     * If no executor exists for the given entity ID, a new one will be created.
     *
     * @param entity the expression entity with updated expression
     * @return the updated RhythmixExecutor instance
     * @throws TranslatorException if compilation fails
     * @throws IllegalArgumentException if entity or entity ID is null
     */
    public RhythmixExecutor update(RhythmixExpressionEntity entity) throws TranslatorException {
        try {
            log.debug("Updating executor for expression ID: {}", entity.getId());

            // Compile the updated expression
            RhythmixExecutor executor = RhythmixCompiler.compile(entity.getExpression());

            // Get existing wrapper or create new one
            ExecutorWrapper existingWrapper = executorCache.get(entity.getId());
            LocalDateTime createdAt = existingWrapper != null ? existingWrapper.getCreatedAt() : LocalDateTime.now();

            // Create updated wrapper
            ExecutorWrapper wrapper = ExecutorWrapper.builder()
                    .executor(executor)
                    .entity(entity)
                    .enabled(entity.isEnable())
                    .createdAt(createdAt)
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Replace in cache
            executorCache.put(entity.getId(), wrapper);
            entity.getFilterIds().forEach((filterId) -> {
                executorRouteMap.computeIfPresent(filterId, (k, v) -> {
                    v.forEach((executorWrapper) -> {
                        if (executorWrapper.getId().equals(entity.getId())) {
                            v.remove(executorWrapper);
                            v.add(wrapper);
                        }
                    });
                    return v;
                });
            });

            // Notify monitor of successful update
            invokeMonitorSafely(() -> getMonitor().onExpressionUpdated(entity));

            log.info("Successfully updated executor for expression ID: {}", entity.getId());
            return executor;

        }catch (Exception e) {
            // Notify monitor of compilation error for non-RhythmixException
            invokeMonitorSafely(() -> getMonitor().onCompilationError(entity, e.getMessage()));
            log.error("Failed to update executor for expression ID: {}", entity.getId(), e);
            throw new TranslatorException("Compilation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes an executor from the cache.
     * <p>
     * This method removes the executor from storage and triggers the
     * {@link RhythmixMonitor#onExpressionDeleted} callback.
     *
     * @param entity the expression entity to delete
     * @throws IllegalArgumentException if entity or entity ID is null
     */
    public void delete(RhythmixExpressionEntity entity) {
        log.debug("Deleting executor for expression ID: {}", entity.getId());

        // Remove from cache
        ExecutorWrapper removed = executorCache.remove(entity.getId());
        entity.getFilterIds().forEach((filterId) -> {
            executorRouteMap.computeIfPresent(filterId, (k, v) -> {
                v.forEach((executorWrapper) -> {
                    if (executorWrapper.getId().equals(entity.getId())) {
                        v.remove(executorWrapper);
                    }
                });
                return v;
            });
        });

        if (removed != null) {
            // Notify monitor of deletion
            invokeMonitorSafely(() -> getMonitor().onExpressionDeleted(entity));
            log.info("Successfully deleted executor for expression ID: {}", entity.getId());
        } else {
            log.warn("Attempted to delete non-existent executor for expression ID: {}", entity.getId());
        }
    }

    /**
     * Enables an executor, allowing it to be executed.
     * <p>
     * This method marks the executor as enabled. If the executor doesn't exist,
     * this method does nothing.
     *
     * @param entity the expression entity to enable
     * @throws IllegalArgumentException if entity or entity ID is null
     */
    public void enable(RhythmixExpressionEntity entity) {
        log.debug("Enabling executor for expression ID: {}", entity.getId());

        ExecutorWrapper wrapper = executorCache.get(entity.getId());
        if (wrapper != null) {
            wrapper.setEnabled(true);
            wrapper.setUpdatedAt(LocalDateTime.now());
            log.info("Successfully enabled executor for expression ID: {}", entity.getId());
        } else {
            log.warn("Attempted to enable non-existent executor for expression ID: {}", entity.getId());
        }
    }

    /**
     * Disables an executor, preventing it from being executed.
     * <p>
     * This method marks the executor as disabled. The executor remains in the cache
     * but should not be executed. If the executor doesn't exist, this method does nothing.
     *
     * @param entity the expression entity to disable
     * @throws IllegalArgumentException if entity or entity ID is null
     */
    public void disable(RhythmixExpressionEntity entity) {

        log.debug("Disabling executor for expression ID: {}", entity.getId());

        ExecutorWrapper wrapper = executorCache.get(entity.getId());
        if (wrapper != null) {
            wrapper.setEnabled(false);
            wrapper.setUpdatedAt(LocalDateTime.now());
            log.info("Successfully disabled executor for expression ID: {}", entity.getId());
        } else {
            log.warn("Attempted to disable non-existent executor for expression ID: {}", entity.getId());
        }
    }

    /**
     * Retrieves an executor by entity ID.
     *
     * @param entityId the ID of the expression entity
     * @return an Optional containing the executor if found, empty otherwise
     */
    public Optional<RhythmixExecutor> getExecutor(String entityId) {
        if (entityId == null) {
            return Optional.empty();
        }

        ExecutorWrapper wrapper = executorCache.get(entityId);
        return wrapper != null ? Optional.of(wrapper.getExecutor()) : Optional.empty();
    }

    /**
     * Retrieves an executor wrapper by entity ID.
     * <p>
     * The wrapper contains the executor along with its metadata (enabled status, timestamps, etc.).
     *
     * @param entityId the ID of the expression entity
     * @return an Optional containing the executor wrapper if found, empty otherwise
     */
    public Optional<ExecutorWrapper> getExecutorWrapper(String entityId) {
        if (entityId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(executorCache.get(entityId));
    }

    /**
     * Checks if an executor exists for the given entity ID.
     *
     * @param entityId the ID of the expression entity
     * @return true if an executor exists, false otherwise
     */
    public boolean exists(String entityId) {
        return entityId != null && executorCache.containsKey(entityId);
    }

    /**
     * Checks if an executor is enabled for the given entity ID.
     *
     * @param entityId the ID of the expression entity
     * @return true if the executor exists and is enabled, false otherwise
     */
    public boolean isEnabled(String entityId) {
        return getExecutorWrapper(entityId)
                .map(ExecutorWrapper::isEnabled)
                .orElse(false);
    }

    /**
     * Gets the total number of executors in the cache.
     *
     * @return the number of cached executors
     */
    public int size() {
        return executorCache.size();
    }

    /**
     * Clears all executors from the cache.
     * <p>
     * This method removes all executors but does not trigger deletion callbacks.
     * Use with caution.
     */
    public void clear() {
        log.info("Clearing all executors from cache. Current size: {}", executorCache.size());
        executorCache.clear();
        executorRouteMap.clear();
    }


    /**
     * Gets the global monitor instance from RhythmixConfig.
     *
     * @return the RhythmixMonitor instance
     */
    private RhythmixMonitor getMonitor() {
        return RhythmixConfig.getMonitor();
    }

    /**
     * Safely invokes a monitor callback, catching and logging any exceptions.
     * <p>
     * This ensures that monitoring failures do not interrupt normal execution flow.
     * If the monitor is null or throws an exception, the error is logged but not propagated.
     *
     * @param callback the monitor callback to invoke
     */
    private void invokeMonitorSafely(Runnable callback) {
        try {
            RhythmixMonitor monitor = getMonitor();
            if (monitor != null) {
                callback.run();
            }
        } catch (Exception e) {
            log.warn("Monitor callback failed: {}", e.getMessage(), e);
        }
    }

    private void execute(RhythmixEventData event) {
        executorRouteMap.forEach((k, v) -> {
            v.forEach((executorWrapper) -> {
                if (executorWrapper.isEnabled()) {

                    try {
                        invokeMonitorSafely(() -> getMonitor().onBeforeExecution(executorWrapper.getEntity(), event));
                        boolean execute = executorWrapper.getExecutor().execute(event);
                        final ExecutionRecord executionRecord = executorWrapper.getExecutor().getRhythmixExecutionData().getCurrentExecutionRecord();
                        if (!Objects.equals(executionRecord.getStatePositionAfterExecution(), executionRecord.getCurrentStatePosition())) {
                            invokeMonitorSafely(() -> getMonitor().onStatePositionChanged(executorWrapper.getEntity(),
                                    executionRecord.getCurrentStatePosition(), executionRecord.getStatePositionAfterExecution(),
                                    executorWrapper.getExecutor().getRhythmixExecutionData()));
                        }
                        if (execute) {
                            invokeMonitorSafely(() -> getMonitor().onExecutionSuccess(executorWrapper.getEntity(), event));
                        }
                        invokeMonitorSafely(() -> getMonitor().onAfterExecution(executorWrapper.getEntity(), event));
                    } catch (Exception e) {
                        invokeMonitorSafely(() -> getMonitor().onExecutionError(executorWrapper.getEntity(),
                                executorWrapper.getExecutor().getRhythmixExecutionData(), e));
                    }
                }
            });
        });
    }

    /**
     * Resets the singleton instance.
     * <p>
     * This method is primarily for testing purposes. It clears the cache and
     * resets the singleton instance.
     * <p>
     * <b>Warning:</b> This method should only be used in test environments.
     */
    public static void reset() {
        synchronized (LOCK) {
            if (instance != null) {
                instance.clear();
                instance = null;
            }
        }
    }
}

