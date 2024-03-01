package com.celi.ferrum.translate.chain;

import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.parser.ast.ASTNode;
import com.celi.ferrum.translate.EnvProxy;
import com.celi.ferrum.translate.Translator;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static com.celi.ferrum.pebble.TemplateEngine.ENGINE;

public class Meet {

    public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {
        try {
            PebbleTemplate template = ENGINE.getTemplate("expr/chain/meet.peb");
            Writer writer = new StringWriter();
            Map<String, Object> context = new HashMap<>();
            String name = astNode.getLabel();
            ASTNode state = astNode.getChildren(0).getChildren(0);
            context.put("eventValue","chainResult");
            String stateCode = Translator.translate(state, context, env);
            context.put("funcName", name);
            context.put("stateCode", stateCode);
            template.evaluate(writer, context);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
