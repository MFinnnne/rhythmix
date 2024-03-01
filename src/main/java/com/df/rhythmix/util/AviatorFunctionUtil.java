package com.df.rhythmix.util;

import cn.hutool.crypto.digest.DigestUtil;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.type.AviatorFunction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AviatorFunctionUtil {

    public static <T extends AviatorFunction> T addFunction(Class<T> functionClass) {
        try {
            T instance = functionClass.getDeclaredConstructor().newInstance();
            Method method = functionClass.getDeclaredMethod("getName");
            String name = (String) method.invoke(instance);
            if (!AviatorEvaluator.containsFunction(name)) {
                AviatorEvaluator.addFunction(instance);
                return instance;
            }
            return (T) AviatorEvaluator.getFunction(name);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("udf获取失败");
    }

    public static <T extends AviatorFunction> T getFunction(Class<T> functionClass) {
        return addFunction(functionClass);
    }

    public static <T extends AviatorFunction> void alias(String name, Class<T> functionClass) {
        try {
            T instance = functionClass.getDeclaredConstructor().newInstance();
            Method method = functionClass.getDeclaredMethod("getName");
            String oriName = (String) method.invoke(instance);
            if (!AviatorEvaluator.containsFunction(name)) {
                AviatorFunction function = AviatorEvaluator.getFunction(oriName);
                AviatorEvaluator.getInstance().addFunction(name, function);
            }
            return;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("udf获取失败");
    }

    public  static Expression getExpr(String code){
        Expression expr = AviatorEvaluator.getInstance().getCachedExpression(DigestUtil.md5Hex(code));
        if (expr==null) {
            expr = AviatorEvaluator.getInstance().compile(DigestUtil.md5Hex(code), code, true);
        }
        return expr;
    }
}
