package com.df.rhythmix.execute;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import com.df.rhythmix.udf.FilterUDF;

import java.util.HashMap;
import java.util.Map;

public class Compiler {
    public static Executor compile(String code) throws TranslatorException {
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code,env);
        return new Executor(translatedCode,env);
    }

    public static Executor compile(String code, HashMap<String,Object> udfEnv) throws TranslatorException {
        EnvProxy env = new EnvProxy();
        env.rawPutAll(udfEnv);
        String translatedCode = Translator.translate(code,env);
        return new Executor(translatedCode,env);
    }

    /**
     * Compile with filter UDFs support
     * @param code The expression code to compile
     * @param filterUDFs Map of filter UDF name to FilterUDF implementation
     * @return Compiled executor
     * @throws TranslatorException If compilation fails
     */
    public static Executor compile(String code, Map<String, FilterUDF> filterUDFs) throws TranslatorException {
        EnvProxy env = new EnvProxy();

        // Register filter UDFs in the environment using their names
        for (FilterUDF filterUDF : filterUDFs.values()) {
            env.rawPut(filterUDF.getName(), filterUDF);
        }

        String translatedCode = Translator.translate(code, env);
        return new Executor(translatedCode, env);
    }

    /**
     * Compile with both regular UDFs and filter UDFs support
     * @param code The expression code to compile
     * @param udfEnv Regular UDF environment
     * @param filterUDFs Map of filter UDF name to FilterUDF implementation
     * @return Compiled executor
     * @throws TranslatorException If compilation fails
     */
    public static Executor compile(String code, HashMap<String,Object> udfEnv, Map<String, FilterUDF> filterUDFs) throws TranslatorException {
        EnvProxy env = new EnvProxy();
        env.rawPutAll(udfEnv);

        // Register filter UDFs in the environment using their names
        for (FilterUDF filterUDF : filterUDFs.values()) {
            env.rawPut(filterUDF.getName(), filterUDF);
        }

        String translatedCode = Translator.translate(code, env);
        return new Executor(translatedCode, env);
    }
}
