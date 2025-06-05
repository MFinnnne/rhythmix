/*
 * @Author: MFine
 * @Date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-03-11 00:15:47
 * @LastEditors: MFine
 * @Description: 
 */
package com.df.rhythmix.translate.chain;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;

public class Filter {
    private static final PebbleTemplate FILTER = ENGINE.getTemplate("expr/chain/filter.peb");

    public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {

        try {
            Writer writer = new StringWriter();
            Map<String, Object> context = new HashMap<>();
            String name = astNode.getLabel();
            ASTNode state = astNode.getChildren(0).getChildren(0);
            String stateCode = Translator.translate(state, context, env);
            boolean strict = astNode.getChildren(0).getChildren().size() > 1 && Boolean.parseBoolean(astNode.getChildren(0).getChildren(1).getLexeme().getValue());
            context.put("funcName", name);
            context.put("stateCode", stateCode);
            context.put("strict", strict);
            FILTER.evaluate(writer, context);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
