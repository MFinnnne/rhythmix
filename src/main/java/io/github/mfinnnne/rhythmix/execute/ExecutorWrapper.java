package io.github.mfinnnne.rhythmix.execute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Wrapper class that holds a RhythmixExecutor along with its metadata.
 * <p>
 * This class is used by {@link RhythmixExecutorManager} to manage executor instances
 * with additional state information such as enabled/disabled status and the original
 * expression entity.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutorWrapper {

    private  String id;
    /**
     * The compiled executor instance.
     */
    private RhythmixExecutor executor;
    
    /**
     * The original expression entity that was used to create this executor.
     */
    private RhythmixExpressionEntity entity;
    
    /**
     * Whether this executor is currently enabled for execution.
     * Disabled executors are kept in the cache but should not be executed.
     */
    private boolean enabled;
    
    /**
     * Timestamp when this executor was created (in milliseconds).
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when this executor was last updated (in milliseconds).
     */
    private LocalDateTime updatedAt;
    
    /**
     * Checks if this executor can be executed.
     * An executor can be executed only if it is enabled.
     *
     * @return true if the executor is enabled, false otherwise
     */
    public boolean canExecute() {
        return enabled && executor != null;
    }
}

