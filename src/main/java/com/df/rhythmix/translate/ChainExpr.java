package com.df.rhythmix.translate;

import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.parser.ast.*;
import com.df.rhythmix.translate.chain.*;
import com.df.rhythmix.util.ParserUtils;
import com.df.rhythmix.util.PeekTokenIterator;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;

public class ChainExpr {


    public static String translate(List<Token> tokens, EnvProxy env) throws LexicalException, TranslatorException, IOException, ParseException {
        ASTNode astNode = Expr.F(new PeekTokenIterator(tokens.stream()));
        Map<String, Object> context = new HashMap<>();
        return translate(astNode, context, env);
    }

    public static String translate(List<Token> tokens, Map<String, Object> context, EnvProxy env) throws LexicalException, TranslatorException, IOException, ParseException {
        ASTNode astNode = Expr.F(new PeekTokenIterator(tokens.stream()));
        return translate(astNode, context, env);
    }

    public static String translate(ASTNode astNode, Map<String, Object> context, EnvProxy env) throws TranslatorException {
        try {
            PebbleTemplate chainTemplate = ENGINE.getTemplate("expr/chain.peb");
            Writer writer = new StringWriter();
            env.put("rawChainQueue", new LinkedList<>());
            env.put("processedChainQueue", null);
            env.put("chainResult", null);
            env.put("debugChainResult", null);
            List<String> allCallStmtLabel = ParserUtils.getAllCallStmtLabel(astNode);
            ChainExprSyntaxCheck.check(allCallStmtLabel);
            String code = recursiveTrans(astNode, env);
            context.put("chainFuncs", allCallStmtLabel);
            context.put("chainSobelCode", code);
            chainTemplate.evaluate(writer, context);
            return writer.toString();
        } catch (TranslatorException | IOException e) {
            e.printStackTrace();
            throw new TranslatorException(e.getMessage());
        }
    }

    private static String recursiveTrans(ASTNode astNode, EnvProxy env) throws IOException, TranslatorException {
        if (!astNode.getChildren().isEmpty()) {
            if (astNode.getType() == ASTNodeTypes.VARIABLE) {
                String name = astNode.getLabel();
                switch (name) {
                    case "filter":
                        return Filter.translate(astNode, env);
                    case "limit":
                        return Limit.translate(astNode, env);
                    case "sum":
                        return Calculator.Sum.translate(astNode, env);
                    case "avg":
                        return Calculator.Avg.translate(astNode, env);
                    case "collect":
                        return Collect.translate(astNode, env);
                    case "window":
                        return Window.translate(astNode, env);
                    case "take":
                        return Take.translate(astNode, env);
                    case "meet":
                        return Meet.translate(astNode, env);
                    case "stddev":
                        return Calculator.Stddev.translate(astNode, env);
                    case "count":
                        return Calculator.Count.translate(astNode, env);
                    case "hitRate":
                        return Calculator.HitRate.translate(astNode, env);
                    default:
                        throw new TranslatorException("chain表达式暂不支持 {} 算子", name);
                }
            }
            String left = recursiveTrans(astNode.getChildren(0), env);
            String right = recursiveTrans(astNode.getChildren(1), env);
            return left + "\n" + right;
        }
        throw new TranslatorException("链式调用翻译错误");
    }


}
