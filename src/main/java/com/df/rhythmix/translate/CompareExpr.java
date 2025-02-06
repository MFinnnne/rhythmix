/*
 * @Author: MFine
 * @Date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-02-06 22:28:34
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
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;

public class CompareExpr {


    private static final List<String> COMPARE_EXPR_FIRST_TOKENS = List.of("!=", ">", "<", "==", "<=", ">=");

    /**
     * 比较表达式 大于 小于  等于 .....
     *
     * @param tokens 符号集合
     * @return 翻译得到的代码
     * @throws TranslatorException 翻译错误
     */
    public static String translate(List<Token> tokens, EnvProxy env) throws TranslatorException {

        try {
            ASTNode astNode = CompareStmt.parser(new PeekTokenIterator(tokens.stream()));
            return translate(astNode, env);
        } catch (Exception e) {
            throw new TranslatorException(e.getMessage());
        }

    }


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
                if (!arg.getLexeme().isNumber()) {
                    throw new TranslatorException("{} cannot be followed by non-numeric type", symbol);
                }
            }
            if (arg.getLexeme().isVariable()) {
                throw new TranslatorException("Comparison expression parameter cannot be a variable");
            }
            context.put("comparedValue", arg.getLexeme().getValue());
            if (arg.getLexeme().getType() == TokenType.INTEGER) {
                context.put("type", "long");
            }

            if (arg.getLexeme().getType() == TokenType.FLOAT) {
                context.put("type", "double");
            }
            template.evaluate(writer, context);
            return writer.toString().replaceAll("\\r|\\n|\\s","");
        } catch (Exception e) {
            throw new TranslatorException(e.getMessage());
        }

    }

    public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {

        try {
            Map<String, Object> context = new HashMap<>();
            return translate(astNode, context, env);
        } catch (Exception e) {
            throw new TranslatorException(e.getMessage());
        }

    }

    public static boolean isCompareExpr(String token) {
        return COMPARE_EXPR_FIRST_TOKENS.contains(token);
    }
}
