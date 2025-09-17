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
 * <p>Collect class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
public class Collect {

    private static final PebbleTemplate COLLECT = ENGINE.getTemplate("expr/chain/collect.peb");

    /**
     * <p>translate.</p>
     *
     * @param astNode a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @param env a {@link com.df.rhythmix.translate.EnvProxy} object.
     * @return a {@link java.lang.String} object.
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
