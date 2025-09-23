package com.df.rhythmix.translate.function;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.translate.EnvProxy;

import java.util.List;
import java.util.Map;

/**
 * <p>FunctionTranslate interface.</p>
 *
 * author MFine
 * version $Id: $Id
 */
public interface FunctionTranslate {

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<String> getName();

    /**
     * @param astNode Abstract Syntax Tree Node
     * @param context the context
     * @param env the environment
     * @return the translated string
     * @throws TranslatorException if the translation fails
     */
    default String translate(ASTNode astNode, Map<String,Object> context, EnvProxy env) throws TranslatorException{
        return null;
    }

    /**
     * @param astNode the ASTNode to translate
     * @param env the environment
     * @return the translated string
     * @throws TranslatorException if the translation fails
     */
    default String translate(ASTNode astNode, EnvProxy env) throws TranslatorException{
        return null;
    }

    /**
     * @param astNode the ASTNode to translate
     * @return the translated string
     */
    default String translate(ASTNode astNode) {
        return null;
    }

    /**
     * Checks if the ASTNode is valid
     * @param astNode the ASTNode to check
     * @return true if the ASTNode is valid, false otherwise
     */
    boolean argsCheck(ASTNode astNode);

}
