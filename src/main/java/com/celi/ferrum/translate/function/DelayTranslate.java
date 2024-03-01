package com.celi.ferrum.translate.function;

import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.lexer.TokenType;
import com.celi.ferrum.parser.ast.ASTNode;
import com.celi.ferrum.translate.EnvProxy;
import com.celi.ferrum.util.TranslateUtil;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.celi.ferrum.pebble.TemplateEngine.ENGINE;

public class DelayTranslate implements FunctionTranslate {


    @Override
    public List<String> getName() {
        return List.of("delay");
    }

    @Override
    public String translate(ASTNode astNode, Map<String,Object> context,EnvProxy env) throws TranslatorException {
        try {
            PebbleTemplate template = ENGINE.getTemplate("expr/delay.peb");
            String funcName = astNode.getLabel();
            Writer writer = new StringWriter();
            List<ASTNode> args = astNode.getChildren(0).getChildren();
            if (args.size() != 2) {
                throw new TranslatorException("{} 函数参数必须为一个", funcName);
            }
            if (args.get(0).getLexeme().getType() != TokenType.INTEGER) {
                throw new TranslatorException("{} 参数类型错误，要求整数类型，实际：{}", funcName, args.get(0).getLexeme().getType());
            }
            long delayTime = Long.parseLong(args.get(0).getLexeme().getValue());

            delayTime = TranslateUtil.toMs(delayTime, args.get(1).getLabel());
            context.put("funcName", funcName);
            env.put("preTime", null);
            context.put("delayTime", delayTime);
            env.put("delayStartTime",null);
            template.evaluate(writer, context);
            return writer.toString();
        } catch (Exception e) {
            throw new TranslatorException(e.getMessage());
        }
    }

    @Override
    public boolean argsCheck(ASTNode astNode) {
        return false;
    }
}
