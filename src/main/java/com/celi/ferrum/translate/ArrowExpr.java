package com.celi.ferrum.translate;

import com.celi.ferrum.exception.LexicalException;
import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.parser.ast.*;
import com.celi.ferrum.util.Config;
import com.celi.ferrum.util.PeekTokenIterator;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.celi.ferrum.pebble.TemplateEngine.ENGINE;

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
                //preFuncFinishTime变量在作用域为当前表达式
                //此做法是为了在模板解析的时候 在模板上下文中查到 preFuncFinishTime 后直接应用引用引用已有变量
                //而不是在重新增加后缀，从而让preFuncFinishTime变成一个全新变量导致delay函数异常
                context.put("preFuncFinishTime","preFuncFinishTime"+Config.SPLIT_SYMBOL+ Config.VAR_COUNTER.get());
                for (ASTNode arg : children) {
                    if (arg.getType() == ASTNodeTypes.ARROW_EXPR) {
                        throw new TranslatorException("箭头表达式不允许嵌套箭头表达式");
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


