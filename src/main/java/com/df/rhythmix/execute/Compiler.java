package com.df.rhythmix.execute;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.lib.Register;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;

import java.util.HashMap;

public class Compiler {
    static {
        Register.importFunction();
    }
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
}
