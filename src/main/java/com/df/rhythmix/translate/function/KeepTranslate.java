package com.df.rhythmix.translate.function;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.parser.ast.ASTNodeTypes;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import com.df.rhythmix.util.ParserUtils;
import com.df.rhythmix.util.TranslateUtil;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;

/**
 * <p>KeepTranslate class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
public class KeepTranslate implements FunctionTranslate {


    /** {@inheritDoc} */
    @Override
    public List<String> getName() {
        return List.of("keep");
    }

    /** {@inheritDoc} */
    @Override
    public String translate(ASTNode astNode, Map<String, Object> context, EnvProxy env) throws TranslatorException {
        return translate(astNode, env);
    }

    /** {@inheritDoc} */
    public String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {
        try {
            PebbleTemplate template = ENGINE.getTemplate("expr/keep.peb");
            Writer writer = new StringWriter();

            String funcName = astNode.getLabel();
            List<ASTNode> args = astNode.getChildren(0).getChildren();
            if (args.size() != 3) {
                throw new TranslatorException("keep function requires exactly 2 parameters");
            }
            if (!args.get(1).getLexeme().isNumber()) {
                throw new TranslatorException("Parameter error, second parameter must be integer with time unit");
            }
            long keepTime = Long.parseLong(args.get(1).getLabel());
            if (keepTime <= 0) {
                throw new TranslatorException("limit operator parameter must be greater than 0");
            }
            String unit = args.get(2).getLabel();
            long ms = TranslateUtil.toMs(keepTime, unit);

            Map<String, Object> context = new HashMap<>();
            ASTNode state = args.get(0);
            String code = Translator.translate(state, context, env);
            if (argsCheck(state)) {
                throw new TranslatorException("{} function first parameter must be a state parameter", funcName);
            }
            context.put("funcName", funcName);
            context.put("stateCheckCode", code);
            env.put("preTime", null);
            context.put("keepTime", ms);
            template.evaluate(writer, context);
            return writer.toString();
        } catch (Exception e) {
            throw new TranslatorException(e.getMessage());
        }


    }

    /** {@inheritDoc} */
    @Override
    public boolean argsCheck(ASTNode astNode) {
        List<ASTNodeTypes> types = ParserUtils.toBFSASTType(astNode);

        return types.stream()
                .anyMatch(item -> item != ASTNodeTypes.COMPARE_EXPR && item != ASTNodeTypes.RANGE_EXPR && item != ASTNodeTypes.UNARY_EXPR);
    }
}
