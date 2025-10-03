/*
 * author: MFine
 * date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-02-06 22:28:02
 * @LastEditors: MFine
 * @Description: 
 */
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
 * Translates the "take" function in a chain expression.
 * This function allows for taking a slice of the data stream by specifying a start and optional end index.
 *
 * @author MFine
 * @version $Id: $Id
 */
public class Take {
    private static final PebbleTemplate FILTER = ENGINE.getTemplate("expr/chain/take.peb");

    /**
     * Translates a take expression ASTNode into its string representation.
     *
     * @param astNode The ASTNode representing the take expression, which includes start and optional end indices.
     * @param env     The environment proxy.
     * @return The translated string representation of the take expression.
     * @throws TranslatorException if any.
     */
    public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {
        try {
            Writer writer = new StringWriter();
            Map<String, Object> context = new HashMap<>();
            String name = astNode.getLabel();
            ASTNode arg1Node, arg2Node;
            String arg1, arg2 = null;
            if (astNode.getChildren(0).getChildren().size() == 1) {
                arg1Node = astNode.getChildren(0).getChildren(0);
                arg1 = arg1Node.getLabel();
            } else if (astNode.getChildren(0).getChildren().size() == 2) {
                arg1Node = astNode.getChildren(0).getChildren(0);
                arg2Node = astNode.getChildren(0).getChildren(1);
                arg1 = arg1Node.getLabel();
                arg2 = arg2Node.getLabel();
            } else {
                throw new TranslatorException("take function in chain expression can have 1-2 parameters");
            }
            context.put("startIndex", arg1);
            context.put("endIndex", arg2 == null ? "nil" : arg2);
            context.put("funcName", name);
            FILTER.evaluate(writer, context);
            return writer.toString();
        } catch (Exception e) {
            throw new TranslatorException("Error in take function application, please check");
        }
    }
}
