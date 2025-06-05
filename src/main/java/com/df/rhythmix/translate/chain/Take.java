/*
 * @Author: MFine
 * @Date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-02-06 22:28:02
 * @LastEditors: MFine
 * @Description: 
 */
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

public class Take {
    private static final PebbleTemplate FILTER = ENGINE.getTemplate("expr/chain/take.peb");

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
