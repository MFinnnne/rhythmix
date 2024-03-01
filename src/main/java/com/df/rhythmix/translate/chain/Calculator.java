package com.df.rhythmix.translate.chain;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;

public class Calculator {

    public static class Sum {
        private static final PebbleTemplate FILTER = ENGINE.getTemplate("expr/chain/sum.peb");

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
                throw new RuntimeException(e);
            }
        }
    }

    public static class Avg {
        private static final PebbleTemplate FILTER = ENGINE.getTemplate("expr/chain/avg.peb");

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
                throw new RuntimeException(e);
            }
        }
    }


    public static class Stddev {
        private static final PebbleTemplate FILTER = ENGINE.getTemplate("expr/chain/stddev.peb");

        public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {

            try {
                Writer writer = new StringWriter();
                Map<String, Object> context = new HashMap<>();
                String name = astNode.getLabel();
                if (!astNode.getChildren(0).getChildren().isEmpty()) {
                    throw new TranslatorException("stddev函数不需要参数");
                }
                context.put("funcName", name);
                FILTER.evaluate(writer, context);
                return writer.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Count {
        private static final PebbleTemplate FILTER = ENGINE.getTemplate("expr/chain/count.peb");

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
                throw new RuntimeException(e);
            }
        }
    }

    public static class HitRate {

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
                throw new RuntimeException(e);
            }
        }
    }
}
