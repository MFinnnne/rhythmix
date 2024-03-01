package com.df.rhythmix.translate.function;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.parser.ast.ASTNodeTypes;
import com.df.rhythmix.translate.EnvProxy;
import com.df.rhythmix.translate.Translator;
import com.df.rhythmix.util.ParserUtils;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;

public class CountTranslate implements FunctionTranslate {


    @Override
    public List<String> getName() {
        return List.of("count", "count!");
    }

    @Override
    public String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {
        Map<String,Object> context = new HashMap<>();
        return translate(astNode, context,env);
    }

    @Override
    public String translate(ASTNode astNode,Map<String,Object> context, EnvProxy env) throws TranslatorException {
        try {
            PebbleTemplate template = ENGINE.getTemplate("expr/count.peb");
            Writer writer = new StringWriter();
            String funcName = astNode.getLabel();

            List<ASTNode> args = astNode.getChildren(0).getChildren();
            if (args.size() != 2) {
                throw new TranslatorException("{} 函数参数必须为两个", funcName);
            }
            context.put("funcName", funcName);
            ASTNode state = args.get(0);
            if (!argsCheck(state)) {
                throw new TranslatorException("{} 函数第一个参数必须为状态参数", funcName);
            }
            String code = Translator.translate(state, context, env);
            long times = Long.parseLong(args.get(1).getLexeme().getValue());
            env.put("countTime", 0);
            context.put("targetCountTimes", times);
            context.put("stateCheckCode", code);
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
