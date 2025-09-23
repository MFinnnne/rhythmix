package com.df.rhythmix.translate.chain;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.translate.EnvProxy;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;

/**
 * Translates the "collect" function in a chain expression.
 *
 * @author MFine
 * @version $Id: $Id
 */
public class Collect {

    private static final PebbleTemplate COLLECT = ENGINE.getTemplate("expr/chain/collect.peb");

    /**
     * Translates a collect expression ASTNode into its string representation.
     *
     * @param astNode The ASTNode representing the collect expression.
     * @param env     The environment proxy.
     * @return The translated string representation of the collect expression.
     * @throws com.df.rhythmix.exception.TranslatorException if any.
     */
    public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {
        try {
            Writer writer = new StringWriter();
            Map<String, Object> context = new HashMap<>();
            String name = astNode.getLabel();
            context.put("funcName", name);
            COLLECT.evaluate(writer, context);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
