package com.celi.ferrum.translate.chain;

import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.parser.ast.ASTNode;
import com.celi.ferrum.translate.EnvProxy;
import com.celi.ferrum.translate.Translator;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.celi.ferrum.pebble.TemplateEngine.ENGINE;

public class Collect {

    private static final PebbleTemplate FILTER = ENGINE.getTemplate("expr/chain/collect.peb");

    public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {
        try {
            Writer writer = new StringWriter();
            Map<String, Object> context = new HashMap<>();
            String name = astNode.getLabel();
            context.put("funcName", name);
            FILTER.evaluate(writer, context);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
