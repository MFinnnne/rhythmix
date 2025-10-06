package io.github.mfinnnne.rhythmix.translate.chain;

import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.parser.ast.ASTNode;
import io.github.mfinnnne.rhythmix.translate.EnvProxy;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static io.github.mfinnnne.rhythmix.pebble.TemplateEngine.ENGINE;

/**
 * Translates the "clear" function in a chain expression.
 * This is a post-processing operator that clears the chain queues.
 * It can be used with a standard operation or a User-Defined Function (UDF).
 *
 * @author MFine
 * @version $Id: $Id
 */
public class Clear {

    /**
     * Translates a clear expression ASTNode into its string representation.
     *
     * @param astNode a {@link ASTNode} object.
     * @param env     a {@link EnvProxy} object.
     * @param isUDF   a boolean indicating whether a User-Defined Function is used.
     * @return a {@link java.lang.String} object.
     * @throws TranslatorException if any.
     */
    public static String translate(ASTNode astNode, EnvProxy env, boolean isUDF) throws TranslatorException {
        try {
            PebbleTemplate template = ENGINE.getTemplate("expr/chain/clear.peb");
            Writer writer = new StringWriter();
            Map<String, Object> context = new HashMap<>();
            String name = astNode.getLabel();
            if (isUDF) {
                context.put("isUDF", true);
            }
            context.put("funcName", name);
            template.evaluate(writer, context);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Translates a clear expression ASTNode into its string representation, assuming no UDF is used.
     *
     * @param astNode a {@link ASTNode} object.
     * @param env     a {@link EnvProxy} object.
     * @return a {@link java.lang.String} object.
     * @throws TranslatorException if any.
     */
    public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {
        return translate(astNode, env, false);
    }
}

