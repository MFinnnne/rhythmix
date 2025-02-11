/*
 * @Author: MFine
 * @Date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-02-06 22:28:48
 * @LastEditors: MFine
 * @Description: 
 */
package com.df.rhythmix.translate;

import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.parser.ast.*;
import com.df.rhythmix.util.Config;
import com.df.rhythmix.util.PeekTokenIterator;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;

public class ArrowExpr {


    public static String translate(List<Token> tokens, EnvProxy env) throws LexicalException, TranslatorException, IOException, ParseException {
        ASTNode astNode = ArrowStmt.parse(new PeekTokenIterator(tokens.stream()));
        return translate(astNode, env);
    }


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

    public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {
        try {
            Map<String, Object> context = new HashMap<>();
            return translate(astNode, context, env);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}


