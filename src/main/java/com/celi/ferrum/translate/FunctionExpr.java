package com.celi.ferrum.translate;

import cn.hutool.core.util.ClassUtil;
import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.parser.ast.ASTNode;
import com.celi.ferrum.translate.function.FunctionTranslate;
import com.celi.ferrum.util.Config;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FunctionExpr {

    private static final Map<String, FunctionTranslate> FUNCTION_TRANSLATE_MAP = new HashMap<>();

    static {
        ClassUtil.scanPackage(FunctionTranslate.class.getPackageName()).forEach((clazz) -> {
            try {
                if (clazz.isInterface()) {
                    return;
                }
                if (FunctionTranslate.class.isAssignableFrom(clazz)) {
                    register((FunctionTranslate) clazz.getDeclaredConstructor().newInstance());
                }
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException e) {
                log.error(e.getMessage());
            }
        });
    }

    private static void register(FunctionTranslate functionTranslate) {
        functionTranslate.getName().forEach(name -> {
            FUNCTION_TRANSLATE_MAP.put(name, functionTranslate);
        });
    }

    public static String translate(ASTNode astNode) {
        String funcName = astNode.getLabel();
        return FUNCTION_TRANSLATE_MAP.get(funcName).translate(astNode);
    }

    public static String translate(ASTNode astNode,EnvProxy env) throws TranslatorException {
        String funcName =  astNode.getLabel();
        return FUNCTION_TRANSLATE_MAP.get(funcName).translate(astNode, env);
    }

    public static String translate(ASTNode astNode, Map<String,Object> context,EnvProxy env) throws TranslatorException {
        String funcName =  astNode.getLabel();
        return FUNCTION_TRANSLATE_MAP.get(funcName).translate(astNode,context, env);
    }

}
