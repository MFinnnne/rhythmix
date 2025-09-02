package com.df.rhythmix.translate.chain;

import com.df.rhythmix.config.ChainFunctionConfig;
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

public class Meet {

    public static String translate(ASTNode astNode, EnvProxy env, boolean isUDF) throws TranslatorException {
        try {
            PebbleTemplate template = ENGINE.getTemplate("expr/chain/meet.peb");
            Writer writer = new StringWriter();
            Map<String, Object> context = new HashMap<>();
            String name = astNode.getLabel();
            if (isUDF) {
                context.put("isUDF", true);
            } else {
                ASTNode state = astNode.getChildren(0).getChildren(0);
                context.put("eventValue", "chainResult");
                String stateCode = Translator.translate(state, context, env);
                context.put("stateCode", stateCode);
            }
            context.put("funcName", name);
            template.evaluate(writer, context);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {
        return translate(astNode, env, false);

    }

}
