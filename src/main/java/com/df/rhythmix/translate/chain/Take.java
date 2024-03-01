package com.df.rhythmix.translate.chain;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.translate.EnvProxy;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

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
                throw new TranslatorException("链式表达式take函数参数个数最多一到两个");
            }
            context.put("startIndex", arg1);
            context.put("endIndex", arg2 == null ? "nil" : arg2);
            context.put("funcName", name);
            FILTER.evaluate(writer, context);
            return writer.toString();
        } catch (Exception e) {
            throw new TranslatorException("take函数应用存在错误，请检查");
        }
    }
}
