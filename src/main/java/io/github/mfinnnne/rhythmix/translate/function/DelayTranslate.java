package io.github.mfinnnne.rhythmix.translate.function;

import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.lexer.TokenType;
import io.github.mfinnnne.rhythmix.parser.ast.ASTNode;
import io.github.mfinnnne.rhythmix.translate.EnvProxy;
import io.github.mfinnnne.rhythmix.util.TranslateUtil;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import static io.github.mfinnnne.rhythmix.pebble.TemplateEngine.ENGINE;

/**
 * <p>DelayTranslate class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
public class DelayTranslate implements FunctionTranslate {


    /** {@inheritDoc} */
    @Override
    public List<String> getName() {
        return List.of("delay");
    }

    /** {@inheritDoc} */
    @Override
    public String translate(ASTNode astNode, Map<String,Object> context,EnvProxy env) throws TranslatorException {
        try {
            PebbleTemplate template = ENGINE.getTemplate("expr/delay.peb");
            String funcName = astNode.getLabel();
            Writer writer = new StringWriter();
            List<ASTNode> args = astNode.getChildren(0).getChildren();
            if (args.size() != 2) {
                throw new TranslatorException("{} function requires exactly two parameters", astNode.getLexeme(), funcName);
            }
            if (args.get(0).getLexeme().getType() != TokenType.INTEGER) {
                throw new TranslatorException("{} parameter type error, requires integer type, actual: {}",
                    astNode.getLexeme(), funcName, args.get(0).getLexeme().getType());
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

    /** {@inheritDoc} */
    @Override
    public boolean argsCheck(ASTNode astNode) {
        return true;
    }
}
