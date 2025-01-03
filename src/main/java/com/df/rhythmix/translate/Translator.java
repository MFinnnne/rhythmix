package com.df.rhythmix.translate;


import cn.hutool.core.util.StrUtil;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.lexer.Lexer;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.parser.ast.ASTNodeTypes;
import com.df.rhythmix.parser.ast.Expr;
import com.df.rhythmix.util.Config;
import com.df.rhythmix.util.PeekTokenIterator;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;


public class Translator {


    /**
     * 将lexer解析得到的token转换为aviator代码
     *
     * @return aviator代码
     */
    public static String translate(String code, EnvProxy env) throws TranslatorException {

        try {
            Map<String, Object> context = new HashMap<>();
            return translate(code, context, env);
        } catch (Exception e) {
            e.printStackTrace();
            throw new TranslatorException("非法表达式,这语法我是真看不懂或许我还不支持", e);
        }
    }


    /**
     * 将lexer解析得到的token转换为aviator代码
     *
     * @return aviator代码
     */
    public static String translate(String code, Map<String, Object> context, EnvProxy env) throws TranslatorException {

        try {
            PebbleTemplate baseTemplate = ENGINE.getTemplate("expr/base.peb");
            Lexer lexer = new Lexer();
            List<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
            if (tokens.isEmpty()) {
                return null;
            }
            // 突变表达式token集合转换为箭头表达式token集合
            if ("<".equals(tokens.get(0).getValue()) && ">".equals(tokens.get(tokens.size() - 1).getValue())) {
                tokens = MutExpr.translateMutExpr(tokens);
            }
            StringWriter writer = new StringWriter();
            ASTNode astNode = Expr.parse(new PeekTokenIterator(tokens.stream()));
            context.put("baseCodes",new ArrayList<>());
            String callCode = translate(astNode, context, env);
            ((ArrayList<String>) context.get("baseCodes")).add(callCode);
            baseTemplate.evaluate(writer, context);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new TranslatorException("非法表达式,这语法我是真看不懂或许我还不支持", e);
        }
    }



    /**
     * @param astNode 抽象语法树
     * @param context 代码翻译的上下文
     * @param env     代码运行环境上下文
     * @return 翻译后的代码
     * @throws TranslatorException 翻译异常
     */
    public static String translate(ASTNode astNode, Map<String, Object> context, EnvProxy env) throws TranslatorException {
        String code;
        switch (astNode.getType()) {
            case ARROW_EXPR:
                Config.VAR_COUNTER.incrementAndGet();
                code = ArrowExpr.translate(astNode, context, env);
                ((ArrayList<String>) context.get("baseCodes")).add(code);
                return "arrow" + Config.SPLIT_SYMBOL + Config.VAR_COUNTER.get() + "()";
            case RANGE_EXPR:
                return "(" + RangeExpr.translate(astNode, context, env) + ")";
            case COMPARE_EXPR:
                return "(" + CompareExpr.translate(astNode, context, env) + ")";
            case VARIABLE:
                // 函数调用
                if (!astNode.getChildren().isEmpty() && astNode.getChildren(0).getType() == ASTNodeTypes.CALL_STMT) {
                    Config.VAR_COUNTER.incrementAndGet();
                    if (astNode.getLabel().endsWith("!")) {
                        context.put("strict", true);
                        astNode.setLabel(astNode.getLabel().substring(0,astNode.getLabel().length()-1));
                    }
                    code = FunctionExpr.translate(astNode, context, env);
                    ((ArrayList<String>) context.get("baseCodes")).add(code);
                    return astNode.getLabel() + Config.SPLIT_SYMBOL + Config.VAR_COUNTER.get() + "()";
                } else {
                    if (!env.containsKey(astNode.getLabel())) {
                        throw new TranslatorException("未定义的变量:'{}'", astNode.getLabel());
                    }
                    return env.rawGet(astNode.getLabel()).toString();
                }
            case CHAIN_EXPR:
                Config.VAR_COUNTER.incrementAndGet();
                String name = "chain" + Config.SPLIT_SYMBOL + Config.VAR_COUNTER.get() + "()";
                context.put("funcName", name);

                code = ChainExpr.translate(astNode, context, env);
                ((ArrayList<String>) context.get("baseCodes")).add(code);
                return name;
            case BINARY_EXPR:
                String codeLeft = translate(astNode.getChildren(0), context, env);
                String codeRight = translate(astNode.getChildren(1), context, env);
                return StrUtil.format("({}{}{})", codeLeft, astNode.getLabel(), codeRight);
            case UNARY_EXPR:
                String exprCode = translate(astNode.getChildren(0), context, env);
                if ("++".equals(astNode.getLabel())) {
                    return StrUtil.format("({}={}+1)", exprCode);
                } else if ("--".equals(astNode.getLabel())){
                    return StrUtil.format("({}={}-1)", exprCode);
                } else if ("!".equals(astNode.getLabel())) {
                    return "(!"+exprCode+")";
                }
            case SCALAR:
                return astNode.getLabel();
            default:
                break;
        }
        throw new TranslatorException(astNode.getLabel());
    }

}
