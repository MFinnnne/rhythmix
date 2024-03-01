package com.celi.ferrum.translate.function;

import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.parser.ast.ASTNode;
import com.celi.ferrum.translate.EnvProxy;

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
