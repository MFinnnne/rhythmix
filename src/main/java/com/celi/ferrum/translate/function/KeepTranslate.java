package com.celi.ferrum.translate.function;

import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.parser.ast.ASTNode;
import com.celi.ferrum.parser.ast.ASTNodeTypes;
import com.celi.ferrum.translate.EnvProxy;
import com.celi.ferrum.translate.Translator;
import com.celi.ferrum.util.ParserUtils;
import com.celi.ferrum.util.TranslateUtil;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.celi.ferrum.pebble.TemplateEngine.ENGINE;

public class KeepTranslate implements FunctionTranslate {


    @Override
    public List<String> getName() {
        return List.of("keep");
    }

    @Override
    public String translate(ASTNode astNode, Map<String, Object> context, EnvProxy env) throws TranslatorException {
        return translate(astNode, env);
    }

    public String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {
        try {
            PebbleTemplate template = ENGINE.getTemplate("expr/keep.peb");
            Writer writer = new StringWriter();

            String funcName = astNode.getLabel();
            List<ASTNode> args = astNode.getChildren(0).getChildren();
            if (args.size() != 3) {
                throw new TranslatorException("keep函数需要两个必须为2个");
            }
            if (!args.get(1).getLexeme().isNumber()) {
                throw new TranslatorException("参数错误，第二个参必须为整数加时间单位的格式");
            }
            long keepTime = Long.parseLong(args.get(1).getLabel());
            if (keepTime <= 0) {
                throw new TranslatorException("limit算子参数必须大于0");
            }
            String unit = args.get(2).getLabel();
            long ms = TranslateUtil.toMs(keepTime, unit);

            Map<String, Object> context = new HashMap<>();
            ASTNode state = args.get(0);
            String code = Translator.translate(state, context, env);
            if (!argsCheck(state)) {
                throw new TranslatorException("{} 函数第一个参数必须为状态参数", funcName);
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

    @Override
    public boolean argsCheck(ASTNode astNode) {
        List<ASTNodeTypes> types = ParserUtils.toBFSASTType(astNode);

        return types.stream()
                .noneMatch(item -> item != ASTNodeTypes.COMPARE_EXPR && item != ASTNodeTypes.RANGE_EXPR && item != ASTNodeTypes.UNARY_EXPR);
    }
}
