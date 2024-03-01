package com.celi.ferrum.execute;

import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.execute.FerrumExecutor;
import com.celi.ferrum.translate.EnvProxy;
import com.celi.ferrum.translate.Translator;

import java.util.HashMap;
import java.util.Map;

public class FerrumCompiler {
    public static FerrumExecutor compile(String code) throws TranslatorException {
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code,env);
        return new FerrumExecutor(translatedCode,env);
    }

    public static FerrumExecutor compile(String code,HashMap<String,Object> udfEnv) throws TranslatorException {
        EnvProxy env = new EnvProxy();
        env.rawPutAll(udfEnv);
        String translatedCode = Translator.translate(code,env);
        return new FerrumExecutor(translatedCode,env);
    }
}
