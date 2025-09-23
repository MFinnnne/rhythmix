/*
 * author: MFine
 * date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-02-11 21:03:40
 * @LastEditors: MFine
 * @Description: 
 */
package com.df.rhythmix.translate.chain;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.util.TranslateUtil;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;

/**
 * Translates the "limit" function in a chain expression.
 * This function can limit the data stream by a specified number of items or by a time duration.
 *
 * @author MFine
 * @version $Id: $Id
 */
public class Limit {
    private static final PebbleTemplate FILTER = ENGINE.getTemplate("expr/chain/limit.peb");

    /**
     * Translates a limit expression ASTNode into its string representation.
     *
     * @param astNode The ASTNode representing the limit expression. It can specify a limit by count or by time.
     * @param env     The environment proxy.
     * @return The translated string representation of the limit expression.
     * @throws com.df.rhythmix.exception.TranslatorException if any.
     */
    public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {
        try {
            Writer writer = new StringWriter();
            Map<String, Object> context = new HashMap<>();
            String name = astNode.getLabel();
            // Time limit case
            if (astNode.getChildren(0).getChildren().size() == 2) {
                String number = astNode.getChildren(0).getChildren(0).getLabel();
                if (Long.parseLong(number) <= 0) {
                    throw new TranslatorException("Limit operator parameter must be greater than 0");
                }
                String unit = astNode.getChildren(0).getChildren(1).getLabel();
                long ms = TranslateUtil.toMs(Long.parseLong(number), unit);
                context.put("limitTime", ms);
            } else {
                String number = astNode.getChildren(0).getChildren(0).getLabel();
                if (Long.parseLong(number) <= 0) {
                    throw new TranslatorException("Limit operator parameter must be greater than 0");
                }
                context.put("limitLength", Integer.parseInt(number));
            }
            context.put("funcName", name);
            FILTER.evaluate(writer, context);
            return writer.toString();
        } catch (TranslatorException | IOException e) {
            throw new TranslatorException(e.getMessage());
        }
    }
}
