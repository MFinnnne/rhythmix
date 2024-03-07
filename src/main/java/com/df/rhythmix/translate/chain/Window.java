package com.df.rhythmix.translate.chain;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.util.TranslateUtil;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;

public class Window {
    private static final PebbleTemplate FILTER = ENGINE.getTemplate("expr/chain/window.peb");

    public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {

        try {
            Writer writer = new StringWriter();
            Map<String, Object> context = new HashMap<>();
            String name = astNode.getLabel();
            //说明此时是时间限制
            if (astNode.getChildren(0).getChildren().size() == 2) {
                String number = astNode.getChildren(0).getChildren(0).getLabel();
                if (Long.parseLong(number) <= 0) {
                    throw  new TranslatorException("window算子参数必须大于0");
                }
                String unit = astNode.getChildren(0).getChildren(1).getLabel();
                long ms = TranslateUtil.toMs(Long.parseLong(number), unit);
                context.put("windowTime", ms);
                env.rawPut("nextChainData",null);
                env.rawPut("hasNextChainData",false);
            } else {
                String number = astNode.getChildren(0).getChildren(0).getLabel();
                if (Long.parseLong(number) <= 0) {
                    throw  new TranslatorException("window算子参数必须大于0");
                }
                context.put("windowLength", Integer.parseInt(number));
            }
            context.put("funcName", name);
            FILTER.evaluate(writer, context);
            return writer.toString();
        } catch (TranslatorException | IOException e) {
            throw new TranslatorException(e.getMessage());
        }
    }
}
