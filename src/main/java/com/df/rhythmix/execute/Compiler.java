package com.df.rhythmix.execute;

import com.df.rhythmix.exception.ErrorFormatter;
import com.df.rhythmix.exception.RhythmixException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.lib.Register;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import com.df.rhythmix.udf.CalculatorUDFRegistry;
import com.df.rhythmix.udf.FilterUDFRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class Compiler {
    static {
        Register.importFunction();
    }

    /**
     * Compile Rhythmix source code into an executable form.
     *
     * @param code The source code to compile
     * @return Compiled executor
     * @throws TranslatorException if compilation fails
     */
    public static Executor compile(String code) throws TranslatorException {
        try {
            EnvProxy env = new EnvProxy();
            String translatedCode = Translator.translate(code, env);
            env.rawPut("filterUDFMap", FilterUDFRegistry.getRegisteredUdfs());
            env.rawPut("calculatorUDFMap", CalculatorUDFRegistry.getRegisteredUdfs());
            return new Executor(translatedCode, env);
        } catch (RhythmixException e) {
            String formattedError = ErrorFormatter.formatError(e, code);
            log.error(formattedError);
            throw e;
        }
    }

    /**
     * Compile Rhythmix source code with custom UDF environment.
     *
     * @param code The source code to compile
     * @param udfEnv Custom UDF environment
     * @return Compiled executor
     * @throws TranslatorException if compilation fails
     */
    public static Executor compile(String code, HashMap<String, Object> udfEnv) throws TranslatorException {
        try {
            EnvProxy env = new EnvProxy();
            env.rawPutAll(udfEnv);
            String translatedCode = Translator.translate(code, env);
            env.rawPut("filterUDFMap", FilterUDFRegistry.getRegisteredUdfs());
            env.rawPut("calculatorUDFMap", CalculatorUDFRegistry.getRegisteredUdfs());
            return new Executor(translatedCode, env);
        } catch (RhythmixException e) {
            // Use ErrorFormatter.formatError() to display the error with source code context
            String formattedError = ErrorFormatter.formatError(e, code);
            log.error(formattedError);
            throw e;
        }
    }



    /**
     * Compile source code with custom UDF environment and return detailed error information if compilation fails.
     *
     * @param code The source code to compile
     * @param udfEnv Custom UDF environment (can be null)
     * @return CompilationResult containing either the executor or detailed error information
     */
    public static CompilationResult compileWithDetailedErrors(String code, HashMap<String, Object> udfEnv) {
        try {
            Executor executor = udfEnv != null ? compile(code, udfEnv) : compile(code);
            return CompilationResult.success(executor);
        } catch (RhythmixException e) {
            String formattedError = ErrorFormatter.formatError(e, code);
            return CompilationResult.failure(e, formattedError);
        }
    }

    /**
     * Result of compilation operation, containing either success or failure information.
     */
    public static class CompilationResult {
        private final boolean success;
        private final Executor executor;
        private final RhythmixException exception;

        private CompilationResult(boolean success, Executor executor, RhythmixException exception,
                                  String formattedError, String detailedErrorReport) {
            this.success = success;
            this.executor = executor;
            this.exception = exception;
        }

        public static CompilationResult success(Executor executor) {
            return new CompilationResult(true, executor, null, null, null);
        }

        public static CompilationResult failure(RhythmixException exception, String formattedError, String detailedErrorReport) {
            return new CompilationResult(false, null, exception, formattedError, detailedErrorReport);
        }

        public static CompilationResult failure(RhythmixException exception, String detailedErrorReport) {
            return new CompilationResult(false, null, exception, null, detailedErrorReport);
        }

        public Executor getExecutor() {
            if (!success) {
                throw new IllegalStateException("Compilation failed, no executor available");
            }
            return executor;
        }

        public RhythmixException getException() {
            if (success) {
                throw new IllegalStateException("Compilation succeeded, no exception available");
            }
            return exception;
        }

    }
}
