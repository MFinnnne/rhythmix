package io.github.mfinnnne.rhythmix.execute;

import io.github.mfinnnne.rhythmix.exception.ErrorFormatter;
import io.github.mfinnnne.rhythmix.exception.RhythmixException;
import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.lib.Register;
import io.github.mfinnnne.rhythmix.translate.EnvProxy;
import io.github.mfinnnne.rhythmix.translate.Translator;
import io.github.mfinnnne.rhythmix.udf.CalculatorUDFRegistry;
import io.github.mfinnnne.rhythmix.udf.FilterUDFRegistry;
import io.github.mfinnnne.rhythmix.udf.MeetUDFRegistry;
import io.github.mfinnnne.rhythmix.udf.PostProcessingUDFRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * The main compiler for the Rhythmix language.
 * <p>
 * This class provides static methods to compile Rhythmix source code into an executable
 * {@link RhythmixExecutor} with built-in monitoring capabilities. It handles the translation
 * process, environment setup, and error formatting.
 * <p>
 * Monitoring is automatically enabled in all compiled executors, tracking event processing,
 * state transitions, and execution performance.
 * <p>
 * Typical usage:
 * <pre>{@code
 * RhythmixExecutor executor = RhythmixCompiler.compile("a > 1 && b < 3");
 * boolean matched = executor.execute(eventObject);
 *
 * // Access monitoring data
 * ExecutionMonitorData data = executor.getMonitoringData();
 * executor.printReport();
 * }</pre>
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
@Slf4j
public class RhythmixCompiler {
    static {
        Register.importFunction();
    }

    /**
     * Compiles Rhythmix source code into an executable form with monitoring enabled.
     * This method initializes a default environment with registered UDFs.
     * Monitoring is automatically enabled to track event processing, state transitions,
     * and execution performance.
     *
     * @param code the Rhythmix source code to compile
     * @return a {@link RhythmixExecutor} instance ready for execution with monitoring enabled
     * @throws TranslatorException if a compilation error occurs
     */
    public static RhythmixExecutor compile(String code) throws TranslatorException {
        try {
            EnvProxy env = new EnvProxy();
            env.rawPut("filterUDFMap", FilterUDFRegistry.getRegisteredUdfs());
            env.rawPut("calculatorUDFMap", CalculatorUDFRegistry.getRegisteredUdfs());
            env.rawPut("meetUDFMap", MeetUDFRegistry.getRegisteredUdfs());
            env.rawPut("postProcessingUDFMap", PostProcessingUDFRegistry.getRegisteredUdfs());
            String translatedCode = Translator.translate(code, env);
            return new RhythmixExecutor(translatedCode, env, code);
        } catch (RhythmixException e) {
            String formattedError = ErrorFormatter.formatError(e, code);
            throw new TranslatorException(formattedError);
        }
    }

    /**
     * Compiles Rhythmix source code with a custom User-Defined Function (UDF) environment
     * and monitoring enabled.
     *
     * @param code   the Rhythmix source code to compile
     * @param udfEnv a {@link HashMap} containing custom UDFs to be made available during compilation and execution
     * @return a {@link RhythmixExecutor} instance ready for execution with monitoring enabled
     * @throws TranslatorException if a compilation error occurs
     */
    public static RhythmixExecutor compile(String code, HashMap<String, Object> udfEnv) throws TranslatorException {
        try {
            EnvProxy env = new EnvProxy();
            env.rawPutAll(udfEnv);
            String translatedCode = Translator.translate(code, env);
            env.rawPut("filterUDFMap", FilterUDFRegistry.getRegisteredUdfs());
            env.rawPut("calculatorUDFMap", CalculatorUDFRegistry.getRegisteredUdfs());
            env.rawPut("meetUDFMap", MeetUDFRegistry.getRegisteredUdfs());
            env.rawPut("postProcessingUDFMap", PostProcessingUDFRegistry.getRegisteredUdfs());
            return new RhythmixExecutor(translatedCode, env, code);
        } catch (RhythmixException e) {
            // Use ErrorFormatter.formatError() to display the error with source code context
            String formattedError = ErrorFormatter.formatError(e, code);
            log.error(formattedError);
            throw e;
        }
    }

}
