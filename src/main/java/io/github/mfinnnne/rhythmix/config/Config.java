package io.github.mfinnnne.rhythmix.config;

import cn.hutool.core.util.RandomUtil;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Global configuration class for Rhythmix.
 * <p>
 * This class holds constants and configuration values used throughout the application.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class Config {


    /**
     * A thread-safe counter for generating unique variable names.
     * This is used to avoid name collisions during code translation, especially when dealing with stateful operations.
     * It is initialized with a value based on the current time and a random number to ensure uniqueness across restarts.
     */
    public final static AtomicLong VAR_COUNTER = new AtomicLong(System.currentTimeMillis() + RandomUtil.randomLong(System.currentTimeMillis()));
    /**
     * The symbol used to split parts of generated variable names.
     * This helps in creating structured and unique identifiers.
     */
    public final static String SPLIT_SYMBOL = "$";

}
