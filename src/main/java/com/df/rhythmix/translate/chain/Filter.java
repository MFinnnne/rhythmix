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
import com.df.rhythmix.parser.ast.ASTNodeTypes;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import com.df.rhythmix.udf.FilterUDF;
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

            // Check if this is a UDF function call by analyzing the AST
            if (isFilterUDFCall(state, env)) {
                // Handle UDF function call
                String udfName = extractUDFName(state);
                boolean strict = astNode.getChildren(0).getChildren().size() > 1 &&
                               Boolean.parseBoolean(astNode.getChildren(0).getChildren(1).getLexeme().getValue());

                context.put("funcName", name);
                context.put("isUDF", true);
                context.put("udfName", udfName);
                context.put("strict", strict);
                FILTER.evaluate(writer, context);
                return writer.toString();
            } else {
                // Handle traditional comparison expression
                String stateCode = Translator.translate(state, context, env);
                boolean strict = astNode.getChildren(0).getChildren().size() > 1 &&
                               Boolean.parseBoolean(astNode.getChildren(0).getChildren(1).getLexeme().getValue());

                context.put("funcName", name);
                context.put("isUDF", false);
                context.put("stateCode", stateCode);
                context.put("strict", strict);
                FILTER.evaluate(writer, context);
                return writer.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Determines if the AST node represents a filter UDF function call
     * by checking if it's a VARIABLE node with CALL_STMT children and matches a registered UDF
     */
    private static boolean isFilterUDFCall(ASTNode state, EnvProxy env) {
        // Check if it's a variable with function call syntax (has CALL_STMT child)
        if (state.getType() == ASTNodeTypes.VARIABLE &&
            !state.getChildren().isEmpty() &&
            state.getChildren().get(0).getType() == ASTNodeTypes.CALL_STMT) {

            String functionName = state.getLabel();

            // Check if this function name corresponds to a registered FilterUDF
            Object udfObject = env.rawGet(functionName);
            return udfObject instanceof FilterUDF;
        }
        return false;
    }

    /**
     * Extracts the UDF function name from the AST node
     */
    private static String extractUDFName(ASTNode state) {
        return state.getLabel();
    }
}
