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
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * The main compiler for the Rhythmix language.
 * <p>
 * This class provides static methods to compile Rhythmix source code into an executable
 * {@link RhythmixExecutor}. It handles the translation process, environment setup,
 * and error formatting.
 * <p>
 * Typical usage:
 * <pre>{@code
 * RhythmixExecutor executor = RhythmixCompiler.compile("a > 1 && b < 3");
 * boolean matched = executor.execute(eventObject);
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
     * Compiles Rhythmix source code into an executable form.
     * This method initializes a default environment with registered UDFs.
     *
     * @param code the Rhythmix source code to compile
     * @return a {@link RhythmixExecutor} instance ready for execution
     * @throws TranslatorException if a compilation error occurs
     */
    public static RhythmixExecutor compile(String code) throws TranslatorException {
        try {
            EnvProxy env = new EnvProxy();
            env.rawPut("filterUDFMap", FilterUDFRegistry.getRegisteredUdfs());
            env.rawPut("calculatorUDFMap", CalculatorUDFRegistry.getRegisteredUdfs());
            env.rawPut("meetUDFMap", MeetUDFRegistry.getRegisteredUdfs());
            String translatedCode = Translator.translate(code, env);
            return new RhythmixExecutor(translatedCode, env);
        } catch (RhythmixException e) {
            String formattedError = ErrorFormatter.formatError(e, code);
            throw new TranslatorException(formattedError);
        }
    }

    /**
     * Compiles Rhythmix source code with a custom User-Defined Function (UDF) environment.
     *
     * @param code   the Rhythmix source code to compile
     * @param udfEnv a {@link HashMap} containing custom UDFs to be made available during compilation and execution
     * @return a {@link RhythmixExecutor} instance ready for execution
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
            return new RhythmixExecutor(translatedCode, env);
        } catch (RhythmixException e) {
            // Use ErrorFormatter.formatError() to display the error with source code context
            String formattedError = ErrorFormatter.formatError(e, code);
            log.error(formattedError);
            throw e;
        }
    }


    /**
     * Compiles source code with a custom UDF environment and returns detailed error information if compilation fails.
     *
     * @param code   the Rhythmix source code to compile
     * @param udfEnv custom UDF environment (can be {@code null})
     * @return a {@link CompilationResult} containing either the executor or detailed error information
     */
    public static CompilationResult compileWithDetailedErrors(String code, HashMap<String, Object> udfEnv) {
        try {
            RhythmixExecutor rhythmixExecutor = udfEnv != null ? compile(code, udfEnv) : compile(code);
            return CompilationResult.success(rhythmixExecutor);
        } catch (RhythmixException e) {
            String formattedError = ErrorFormatter.formatError(e, code);
            return CompilationResult.failure(e, formattedError);
        }
    }

    /**
     * Represents the result of a compilation operation.
     * <p>
     * Contains either a successful compilation artifact ({@link RhythmixExecutor}) or the
     * exception detailing the failure. Use the static factory methods to construct instances.
     */
    public static class CompilationResult {
        private final boolean success;
        private final RhythmixExecutor rhythmixExecutor;
        private final RhythmixException exception;

        private CompilationResult(boolean success, RhythmixExecutor rhythmixExecutor, RhythmixException exception,
                                  String formattedError, String detailedErrorReport) {
            this.success = success;
            this.rhythmixExecutor = rhythmixExecutor;
            this.exception = exception;
        }

        /**
         * Creates a successful compilation result.
         *
         * @param rhythmixExecutor the successfully created executor
         * @return a new {@link CompilationResult} instance for a successful compilation
         */
        public static CompilationResult success(RhythmixExecutor rhythmixExecutor) {
            return new CompilationResult(true, rhythmixExecutor, null, null, null);
        }

        /**
         * Creates a failed compilation result with detailed error information.
         *
         * @param exception           the exception that occurred during compilation
         * @param formattedError      a formatted error message
         * @param detailedErrorReport an optional detailed report of the error
         * @return a new {@link CompilationResult} instance for a failed compilation
         */
        public static CompilationResult failure(RhythmixException exception, String formattedError, String detailedErrorReport) {
            return new CompilationResult(false, null, exception, formattedError, detailedErrorReport);
        }

        /**
         * Creates a failed compilation result with a detailed error report.
         *
         * @param exception           the exception that occurred
         * @param detailedErrorReport a detailed report of the error
         * @return a new {@link CompilationResult} instance for a failed compilation
         */
        public static CompilationResult failure(RhythmixException exception, String detailedErrorReport) {
            return new CompilationResult(false, null, exception, null, detailedErrorReport);
        }

        /**
         * Returns the executor from a successful compilation.
         *
         * @return the {@link RhythmixExecutor}
         * @throws IllegalStateException if the compilation was not successful
         */
        public RhythmixExecutor getExecutor() {
            if (!success) {
                throw new IllegalStateException("Compilation failed, no executor available");
            }
            return rhythmixExecutor;
        }

        /**
         * Returns the exception from a failed compilation.
         *
         * @return the {@link RhythmixException}
         * @throws IllegalStateException if the compilation was successful
         */
        public RhythmixException getException() {
            if (success) {
                throw new IllegalStateException("Compilation succeeded, no exception available");
            }
            return exception;
        }

    }
}
