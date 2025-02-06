package com.df.rhythmix.translate;

import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.lexer.Lexer;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.lexer.TokenType;
import com.df.rhythmix.parser.ast.*;
import com.df.rhythmix.translate.chain.*;
import com.df.rhythmix.util.ParserUtils;
import com.df.rhythmix.util.PeekTokenIterator;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;

@Slf4j
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
            collectAutoComplete(astNode, allCallStmtLabel);
            checkLimitAndWindow(astNode);
            allCallStmtLabel = ParserUtils.getAllCallStmtLabel(astNode);
            ChainExprSyntaxCheck.check(allCallStmtLabel);
            String code = recursiveTrans(astNode, env);
            context.put("chainFuncs", allCallStmtLabel);
            context.put("chainSobelCode", code);
            chainTemplate.evaluate(writer, context);
            return writer.toString();
        } catch (TranslatorException | IOException | LexicalException | ParseException e) {
            e.printStackTrace();
            throw new TranslatorException(e.getMessage());
        }
    }

    private static void checkLimitAndWindow(ASTNode astNode) throws LexicalException, ParseException {
        ASTNode limit = ParserUtils.getNodeByLabel(astNode, "limit");
        ASTNode window = ParserUtils.getNodeByLabel(astNode, "window");
        if (limit != null && window != null) {
            List<ASTNode> limitVar = limit.getChildren(0).getChildren();
            List<ASTNode> windowVar = window.getChildren(0).getChildren();
            if (limitVar.size() == windowVar.size()) {
                if (!limitVar.get(0).getLabel().equals(windowVar.get(0).getLabel())) {
                    throw new ParseException("Both limit and window parameters must be the same");
                }
            }
        }
        if (limit == null && window != null) {
            ASTNode parent = ParserUtils.getNodeParentByLabel(astNode, "window");
            List<ASTNode> windowVar = window.getChildren(0).getChildren();
            StringBuilder stringBuilder = new StringBuilder();
            for (ASTNode node : windowVar) {
                stringBuilder.append(node.getLabel());
            }
            assert parent != null;
            List<ASTNode> copyChild = parent.getChildren();
            Lexer lexer = new Lexer();
            String str = "limit(" + stringBuilder + ")";
            ArrayList<Token> tokens = lexer.analyse(str.chars().mapToObj(x -> (char) x));
            ASTNode limitNode = Expr.parse(new PeekTokenIterator(tokens.stream()));
            Expr expr = new Expr(ASTNodeTypes.CHAIN_EXPR, new Token(TokenType.OPERATOR, "."));
            expr.getChildren().addAll(copyChild);
            ArrayList<ASTNode> newChild = new ArrayList<>();
            newChild.add(limitNode);
            newChild.add(expr);
            parent.setChildren(newChild);
        }
    }

    private static void collectAutoComplete(ASTNode astNode, List<String> allCallStmtLabel) throws LexicalException, ParseException {
        if (!allCallStmtLabel.contains("collect")) {
            Lexer lexer = new Lexer();
            ArrayList<Token> tokens = lexer.analyse("collect()".chars().mapToObj(x -> (char) x));
            ASTNode colAST = Expr.parse(new PeekTokenIterator(tokens.stream()));
            Expr expr = new Expr(ASTNodeTypes.CHAIN_EXPR, new Token(TokenType.OPERATOR, "."));
            if ("filter".equals(allCallStmtLabel.get(0))) {
                ASTNode copyVarNode = astNode.getChildren(1).getChildren(0);
                ASTNode copyOpNode = astNode.getChildren(1).getChildren(1);
                astNode.getChildren(1).getChildren().set(0, colAST);
                astNode.getChildren(1).getChildren().set(1, expr);
                expr.addChild(copyVarNode);
                expr.addChild(copyOpNode);
            } else {
                ASTNode copyVarNode = astNode.getChildren(0);
                ASTNode copyOpNode = astNode.getChildren(1);
                astNode.getChildren().set(0, colAST);
                astNode.getChildren().set(1, expr);
                expr.addChild(copyVarNode);
                expr.addChild(copyOpNode);
            }
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
                        throw new TranslatorException("Chain expression does not support {} operator", name);
                }
            }
            String left = recursiveTrans(astNode.getChildren(0), env);
            String right = recursiveTrans(astNode.getChildren(1), env);
            return left + "\n" + right;
        }
        throw new TranslatorException("Chain call translation error");
    }

}
