/*
 * author: MFine
 * date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-02-06 22:28:48
 * @LastEditors: MFine
 * @Description: 
 */
package io.github.mfinnnne.rhythmix.translate;

import io.github.mfinnnne.rhythmix.exception.LexicalException;
import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.config.Config;
import io.github.mfinnnne.rhythmix.parser.ast.ASTNodeTypes;
import io.github.mfinnnne.rhythmix.parser.ast.ArrowStmt;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;
import io.github.mfinnnne.rhythmix.parser.ast.ASTNode;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.mfinnnne.rhythmix.pebble.TemplateEngine.ENGINE;

/**
 * <p>ArrowExpr class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
public class ArrowExpr {


    /**
     * <p>translate.</p>
     *
     * @param tokens a {@link java.util.List} object.
     * @param env a {@link EnvProxy} object.
     * @return a {@link java.lang.String} object.
     * @throws LexicalException if any.
     * @throws TranslatorException if any.
     * @throws java.io.IOException if any.
     * @throws ParseException if any.
     */
    public static String translate(List<Token> tokens, EnvProxy env) throws LexicalException, TranslatorException, IOException, ParseException {
        ASTNode astNode = ArrowStmt.parse(new PeekTokenIterator(tokens.stream()));
        return translate(astNode, env);
    }


    /**
     * <p>translate.</p>
     *
     * @param astNode a {@link ASTNode} object.
     * @param context a {@link java.util.Map} object.
     * @param env a {@link EnvProxy} object.
     * @return a {@link java.lang.String} object.
     * @throws TranslatorException if any.
     */
    public static String translate(ASTNode astNode, Map<String, Object> context, EnvProxy env) throws TranslatorException {
        try {
            try {
                Writer writer = new StringWriter();
                PebbleTemplate template = ENGINE.getTemplate("expr/arrow.peb");
                List<String> translatedCodes = new ArrayList<>();
                List<ASTNode> children = astNode.getChildren();
                env.put("preFuncFinishTime", null);
                // preFuncFinishTime variable scope is current expression
                // This approach allows the template to find and directly reference the existing preFuncFinishTime variable
                // Rather than creating a new variable by adding a suffix that would cause the delay function to fail
                context.put("preFuncFinishTime","preFuncFinishTime"+Config.SPLIT_SYMBOL+ Config.VAR_COUNTER.get());
                for (ASTNode arg : children) {
                    if (arg.getType() == ASTNodeTypes.ARROW_EXPR) {
                        throw new TranslatorException("Arrow expressions cannot be nested");
                    }
                    String code = Translator.translate(arg, context, env);
                    translatedCodes.add(code);
                }
                context.put("funcName","arrow");
                env.put("index", 0);
                context.put("codes", translatedCodes);
                context.put("codeSize", translatedCodes.size() - 1);
                template.evaluate(writer, context);
                return writer.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>translate.</p>
     *
     * @param astNode a {@link ASTNode} object.
     * @param env a {@link EnvProxy} object.
     * @return a {@link java.lang.String} object.
     * @throws TranslatorException if any.
     */
    public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {
        try {
            Map<String, Object> context = new HashMap<>();
            return translate(astNode, context, env);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}


