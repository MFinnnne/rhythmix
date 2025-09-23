package com.df.rhythmix.translate.chain;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;

/**
 * Provides translators for calculator functions in a chain expression, such as sum, avg, etc.
 *
 * @author MFine
 * @version $Id: $Id
 */
public class Calculator {

    /**
     * Translates the "sum" function in a chain expression.
     */
    public static class Sum {
        private static final PebbleTemplate FILTER = ENGINE.getTemplate("expr/chain/sum.peb");

        /**
         * Translates the given ASTNode into a string representation of the sum function.
         *
         * @param astNode The ASTNode to be translated.
         * @param env     The environment proxy.
         * @return The translated string.
         * @throws TranslatorException if the translation fails.
         */
        public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {

            try {
                Writer writer = new StringWriter();
                Map<String, Object> context = new HashMap<>();
                String name = astNode.getLabel();
                if (!astNode.getChildren(0).getChildren().isEmpty()) {
                    throw new TranslatorException("sum函数不需要参数");
                }
                context.put("funcName", name);
                FILTER.evaluate(writer, context);
                return writer.toString();
            } catch (Exception e) {
                throw new TranslatorException("translate sum error", astNode.getLexeme());
            }
        }
    }

    /**
     * Translates the "avg" function in a chain expression.
     */
    public static class Avg {
        private static final PebbleTemplate FILTER = ENGINE.getTemplate("expr/chain/avg.peb");

        /**
         * Translates the given ASTNode into a string representation of the avg function.
         *
         * @param astNode The ASTNode to be translated.
         * @param env     The environment proxy.
         * @return The translated string.
         * @throws TranslatorException if the translation fails.
         */
        public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {

            try {
                Writer writer = new StringWriter();
                Map<String, Object> context = new HashMap<>();
                String name = astNode.getLabel();
                if (!astNode.getChildren(0).getChildren().isEmpty()) {
                    throw new TranslatorException("avg函数不需要参数");
                }
                context.put("funcName", name);
                FILTER.evaluate(writer, context);
                return writer.toString();
            } catch (Exception e) {
                throw new TranslatorException("translate average  error", astNode.getLexeme());
            }
        }
    }


    /**
     * Translates the "stddev" function in a chain expression.
     */
    public static class Stddev {
        private static final PebbleTemplate FILTER = ENGINE.getTemplate("expr/chain/stddev.peb");

        /**
         * Translates the given ASTNode into a string representation of the stddev function.
         *
         * @param astNode The ASTNode to be translated.
         * @param env     The environment proxy.
         * @return The translated string.
         * @throws TranslatorException if the translation fails.
         */
        public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {

            try {
                Writer writer = new StringWriter();
                Map<String, Object> context = new HashMap<>();
                String name = astNode.getLabel();
                if (!astNode.getChildren(0).getChildren().isEmpty()) {
                    throw new TranslatorException("stddev function does not require parameters");
                }
                context.put("funcName", name);
                FILTER.evaluate(writer, context);
                return writer.toString();
            } catch (Exception e) {
                throw new TranslatorException("translate Standard deviation  error", astNode.getLexeme());
            }
        }
    }

    /**
     * Translates the "count" function in a chain expression.
     */
    public static class Count {
        private static final PebbleTemplate FILTER = ENGINE.getTemplate("expr/chain/count.peb");

        /**
         * Translates the given ASTNode into a string representation of the count function.
         *
         * @param astNode The ASTNode to be translated.
         * @param env     The environment proxy.
         * @return The translated string.
         * @throws TranslatorException if the translation fails.
         */
        public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {

            try {
                Writer writer = new StringWriter();
                Map<String, Object> context = new HashMap<>();
                String name = astNode.getLabel();
                if (!astNode.getChildren(0).getChildren().isEmpty()) {
                    throw new TranslatorException("count函数不需要参数");
                }
                context.put("funcName", name);
                FILTER.evaluate(writer, context);
                return writer.toString();
            } catch (Exception e) {
                throw new TranslatorException("translate Count  error", astNode.getLexeme());
            }
        }
    }

    /**
     * Translates the "hitRate" function in a chain expression.
     */
    public static class HitRate {

        /**
         * Translates the given ASTNode into a string representation of the hitRate function.
         *
         * @param astNode The ASTNode to be translated.
         * @param env     The environment proxy.
         * @return The translated string.
         * @throws TranslatorException if the translation fails.
         */
        public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {
            try {
                PebbleTemplate template = ENGINE.getTemplate("expr/chain/hitRate.peb");
                Writer writer = new StringWriter();
                Map<String, Object> context = new HashMap<>();
                String name = astNode.getLabel();
                ASTNode state = astNode.getChildren(0).getChildren(0);
                context.put("eventValue", "!x.value");
                String stateCode = Translator.translate(state, context, env);
                context.put("funcName", name);
                context.put("stateCode", stateCode);
                template.evaluate(writer, context);
                return writer.toString();
            } catch (Exception e) {
                throw new TranslatorException("translate hitRate error", astNode.getLexeme());
            }
        }
    }

    /**
     * Translates a custom function in a chain expression.
     */
    public static class Custom {
        /**
         * Translates the given ASTNode into a string representation of the custom function.
         *
         * @param astNode The ASTNode to be translated.
         * @param env     The environment proxy.
         * @return The translated string.
         * @throws TranslatorException if the translation fails.
         */
        public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {
            try {

                PebbleTemplate template = ENGINE.getTemplate("expr/chain/calculator.peb");
                StringWriter stringWriter = new StringWriter();
                Map<String, Object> context = new HashMap<>();
                String funName = astNode.getLabel();
                context.put("funcName", funName);
                template.evaluate(stringWriter, context);
                return stringWriter.toString();

            } catch (IOException e) {
                throw new TranslatorException("translate {} error", astNode.getLabel(), astNode.getLexeme());
            }
        }
    }
}
