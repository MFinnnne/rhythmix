package com.df.rhythmix.config;

import lombok.Getter;

import java.util.*;

/**
 * Singleton configuration class for chain expression function lists.
 * This class manages the dynamic configuration of function categories and call tree relationships.
 *
 * Thread-safe singleton implementation using double-checked locking pattern.
 *
 * @author MFine
 * @version 1.0
 */
@Getter
public class ChainFunctionConfig {

    // Singleton instance with volatile for thread safety
    private static volatile ChainFunctionConfig instance;

    // Lock object for synchronization
    private static final Object lock = new Object();

    // Function lists - using ArrayList for mutability
    private final List<String> startFunc = new ArrayList<>();
    private final List<String> endFunc = new ArrayList<>();
    private final List<String> limitFunc = new ArrayList<>();
    private final List<String> calcFunc = new ArrayList<>();
    private final Map<String, List<String>> callTree = new HashMap<>();


    /**
     * Private constructor to prevent direct instantiation.
     * Initialize with default configuration.
     */
    private ChainFunctionConfig() {
        initializeDefaults();
    }

    /**
     * Get the singleton instance using double-checked locking pattern.
     * Thread-safe lazy initialization.
     *
     * @return the singleton instance of ChainFunctionConfig
     */
    public static ChainFunctionConfig getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ChainFunctionConfig();
                }
            }
        }
        return instance;
    }

    /**
     * Reset the singleton instance (mainly for testing purposes).
     * This method is synchronized to ensure thread safety.
     */
    public static synchronized void resetInstance() {
        instance = null;
    }

    /**
     * Create a new instance with custom configuration (for testing or special cases).
     * This bypasses the singleton pattern and should be used carefully.
     *
     * @return a new ChainFunctionConfig instance
     */
    public static ChainFunctionConfig createNewInstance() {
        return new ChainFunctionConfig();
    }

    private void initializeDefaults() {
        // START_FUNC: functions that can start a chain
        startFunc.add("filter");

        // END_FUNC: functions that can end a chain
        endFunc.add("meet");

        // LIMIT_FUNC: functions that limit/control data flow
        limitFunc.addAll(Arrays.asList("limit", "take", "window"));

        // CALC_FUNC: functions that perform calculations
        calcFunc.addAll(Arrays.asList("sum", "hitRate", "count", "avg", "stddev"));

        // Build CALL_TREE based on function categories
        buildCallTree();
    }

    private void buildCallTree() {
        // Combine limit and calc functions for chain transitions
        List<String> limitAndCalc = new ArrayList<>();
        limitAndCalc.addAll(limitFunc);
        limitAndCalc.addAll(calcFunc);

        startFunc.forEach(s -> {
            callTree.put(s, new ArrayList<>(limitAndCalc));
        });
        // filter can be followed by any limit or calc function

        // limit functions can be followed by calc functions or other limit functions
        for (String limitFuncName : limitFunc) {
            callTree.put(limitFuncName, new ArrayList<>(calcFunc));
            // Allow chaining between limit functions
            List<String> allowedAfterLimit = new ArrayList<>(calcFunc);
            allowedAfterLimit.addAll(limitFunc);
            callTree.put(limitFuncName, allowedAfterLimit);
        }

        // calc functions can only be followed by end functions
        for (String calcFuncName : calcFunc) {
            callTree.put(calcFuncName, new ArrayList<>(endFunc));
        }
    }

      /**
     * Update startFunc by adding one or more function names.
     * Thread-safe method using synchronization.
     *
     * @param functionNames one or more function names to add to startFunc
     */
    public synchronized void addStartFunc(String... functionNames) {
        for (String funcName : functionNames) {
            if (!this.startFunc.contains(funcName)) {
                this.startFunc.add(funcName);
            }
        }
        // Rebuild call tree to reflect changes
        buildCallTree();
    }

    public synchronized  void addCalcFunc(String... functionNames) {
        for (String funcName : functionNames) {
            if (!this.calcFunc.contains(funcName)) {
                this.calcFunc.add(funcName);
            }
        }
        // Rebuild call tree to reflect changes
        buildCallTree();
    }
}
