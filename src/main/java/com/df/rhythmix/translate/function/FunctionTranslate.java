package com.df.rhythmix.translate.function;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.translate.EnvProxy;

import java.util.List;
import java.util.Map;

public interface FunctionTranslate {

    List<String> getName();

    default String translate(ASTNode astNode, Map<String,Object> context, EnvProxy env) throws TranslatorException{
        return null;
    }

    default String translate(ASTNode astNode, EnvProxy env) throws TranslatorException{
        return null;
    }

    default String translate(ASTNode astNode) {
        return null;
    }

    boolean argsCheck(ASTNode astNode);

}
