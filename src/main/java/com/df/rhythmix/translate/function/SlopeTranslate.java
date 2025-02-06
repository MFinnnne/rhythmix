package com.df.rhythmix.translate.function;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;

public class SlopeTranslate implements FunctionTranslate {


    @Override
    public List<String> getName() {
        return List.of("slope");
    }

    @Override
    public String translate(ASTNode astNode, Map<String, Object> context, EnvProxy env) throws TranslatorException {
        return translate(astNode, env);
    }

    @Override
    public String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {

        try {
            Writer writer = new StringWriter();
            PebbleTemplate template = ENGINE.getTemplate("expr/slope.peb");
            String funcName = astNode.getLabel();
            List<ASTNode> args = astNode.getChildren(0).getChildren();
            ASTNode state = null;
            long slopeUnit;
            if (args.size() == 1) {
                state = args.get(0);
                slopeUnit = 1;

            } else if (args.size() == 3) {
                state = args.get(0);
                long number = Long.parseLong(args.get(1).getLabel());
                String unit = args.get(2).getLabel();
                switch (unit) {
                    case "s":
                        number = number * 1000;
                        break;
                    case "m":
                        number = number * 1000 * 60;
                        break;
                    case "h":
                        number = number * 1000 * 60 * 60;
                    case "d":
                        number = number * 1000 * 60 * 60 * 24;
                    default:
                        break;
                }
                slopeUnit = number;
            } else {
                throw new TranslatorException("slope function can have at most 2 parameters");
            }
            Map<String, Object> context = new HashMap<>();
            context.put("funcName", funcName);
            env.put("preValue", null);
            env.put("preTime", null);
            context.put("eventValue","slopeRes");
            assert state != null;
            context.put("stateCheckCode", Translator.translate(state, context, env));
            context.put("slopeUnit", slopeUnit);
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
