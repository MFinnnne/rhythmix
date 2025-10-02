/*
 * author: MFine
 * date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-02-11 21:04:49
 * @LastEditors: MFine
 * @Description:
 */
package com.df.rhythmix.translate;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.lexer.TokenType;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.parser.ast.CompareStmt;
import com.df.rhythmix.util.PeekTokenIterator;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;

/**
 * <p>CompareExpr class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
public class CompareExpr {


    private static final List<String> COMPARE_EXPR_FIRST_TOKENS = List.of("!=", ">", "<", "==", "<=", ">=");

    /**
     * Comparison expressions: greater than, less than, equals, etc.
     *
     * @param tokens token collection
     * @return translated code
     * @throws com.df.rhythmix.exception.TranslatorException translation error
     * @param env a {@link com.df.rhythmix.translate.EnvProxy} object.
     */
    public static String translate(List<Token> tokens, EnvProxy env) throws TranslatorException {
        try {
            ASTNode astNode = CompareStmt.parser(new PeekTokenIterator(tokens.stream()));
            return translate(astNode, env);
        } catch (Exception e) {
            throw new TranslatorException(e.getMessage());
        }
    }


    /**
     * <p>translate.</p>
     *
     * @param astNode a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @param context a {@link java.util.Map} object.
     * @param env a {@link com.df.rhythmix.translate.EnvProxy} object.
     * @return a {@link java.lang.String} object.
     * @throws com.df.rhythmix.exception.TranslatorException if any.
     */
    public static String translate(ASTNode astNode, Map<String, Object> context, EnvProxy env) throws TranslatorException {

        try {
            Writer writer = new StringWriter();
            PebbleTemplate template = ENGINE.getTemplate("expr/compare.peb");
            String symbol = astNode.getLabel();
            context.put("symbol", symbol);
            ASTNode arg = astNode.getChildren(0);
            if (!arg.getChildren().isEmpty()) {
                throw new TranslatorException("Comparison expression parameter cannot be an expression");
            }
            if (!Arrays.asList("!=", "==").contains(symbol)) {
                if (!arg.getLexeme().isNumber() && !arg.getLexeme().isVariable()) {
                    throw new TranslatorException("{} cannot be followed by non-numeric type", symbol);
                }
            }
            if (arg.getLexeme().isVariable()) {
                context.put("comparedValue", env.getEnv().get(arg.getLabel()));
            } else {
                context.put("comparedValue", arg.getLexeme().getValue());

            }
            template.evaluate(writer, context);
            return writer.toString().replaceAll("\\r|\\n|\\s", "");
        } catch (Exception e) {
            throw new TranslatorException(e.getMessage());
        }

    }

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
            Map<String, Object> context = new HashMap<>();
            return translate(astNode, context, env);
        } catch (Exception e) {
            throw new TranslatorException(e.getMessage());
        }

    }

    /**
     * <p>isCompareExpr.</p>
     *
     * @param token a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean isCompareExpr(String token) {
        return COMPARE_EXPR_FIRST_TOKENS.contains(token);
    }
}
