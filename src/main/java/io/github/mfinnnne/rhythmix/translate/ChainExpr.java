package io.github.mfinnnne.rhythmix.translate;

import io.github.mfinnnne.rhythmix.config.ChainFunctionConfig;
import io.github.mfinnnne.rhythmix.exception.LexicalException;
import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.lexer.Lexer;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.lexer.TokenType;
import io.github.mfinnnne.rhythmix.parser.ast.ASTNode;
import io.github.mfinnnne.rhythmix.parser.ast.ASTNodeTypes;
import io.github.mfinnnne.rhythmix.parser.ast.Expr;
import io.github.mfinnnne.rhythmix.translate.chain.*;
import io.github.mfinnnne.rhythmix.util.ParserUtils;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

import static io.github.mfinnnne.rhythmix.pebble.TemplateEngine.ENGINE;

/**
 * <p>ChainExpr class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
@Slf4j
public class ChainExpr {

    /**
     * <p>translate.</p>
     *
     * @param tokens a {@link java.util.List} object.
     * @param env a {@link EnvProxy} object.
     * @return a {@link java.lang.String} object.
     * @throws LexicalException if any.
     * @throws TranslatorException if any.
     * @throws java.io.IOException if any.
     * @throws ParseException if any.
     */
    public static String translate(List<Token> tokens, EnvProxy env) throws LexicalException, TranslatorException, IOException, ParseException {
        ASTNode astNode = Expr.F(new PeekTokenIterator(tokens.stream()));
        Map<String, Object> context = new HashMap<>();
        return translate(astNode, context, env);
    }

    /**
     * <p>translate.</p>
     *
     * @param tokens a {@link java.util.List} object.
     * @param context a {@link java.util.Map} object.
     * @param env a {@link EnvProxy} object.
     * @return a {@link java.lang.String} object.
     * @throws LexicalException if any.
     * @throws TranslatorException if any.
     * @throws java.io.IOException if any.
     * @throws ParseException if any.
     */
    public static String translate(List<Token> tokens, Map<String, Object> context, EnvProxy env) throws LexicalException, TranslatorException, IOException, ParseException {
        ASTNode astNode = Expr.F(new PeekTokenIterator(tokens.stream()));
        return translate(astNode, context, env);
    }

    /**
     * <p>translate.</p>
     *
     * @param astNode a {@link ASTNode} object.
     * @param context a {@link java.util.Map} object.
     * @param env a {@link EnvProxy} object.
     * @return a {@link java.lang.String} object.
     * @throws TranslatorException if any.
     */
    public static String translate(ASTNode astNode, Map<String, Object> context, EnvProxy env) throws TranslatorException {
        try {
            PebbleTemplate chainTemplate = ENGINE.getTemplate("expr/chain.peb");
            Writer writer = new StringWriter();
            env.put("rawChainQueue", new ArrayList<>());
            env.put("processedChainQueue", null);
            env.put("chainResult", null);
            env.put("debugChainResult", null);
            List<String> allCallStmtLabel = ParserUtils.getAllCallStmtLabel(astNode);
            filterAutoAdd(astNode, allCallStmtLabel);
            if (allCallStmtLabel.contains("limit") && allCallStmtLabel.contains("window")) {
                throw  new TranslatorException("limit and window cannot be used together", Objects.requireNonNull(ParserUtils.getNodeByLabel(astNode, "limit")).getLexeme());
            }
            ChainExprSyntaxCheck.check(astNode);
            String code = recursiveTrans(astNode, env);
            context.put("chainFuncs", ParserUtils.getAllCallStmtLabel(astNode));
            context.put("chainSobelCode", code);
            chainTemplate.evaluate(writer, context);
            return writer.toString();
        } catch (TranslatorException | LexicalException | ParseException e) {
            throw new TranslatorException("Translation failed: " + e.getMessage(),
                    e.getCharacterPosition(), e.getLine(), e.getColumn());
        }catch (IOException e){
            throw new TranslatorException(e.getMessage());
        }
    }

    private static void filterAutoAdd(ASTNode astNode, List<String> allCallStmtLabel) throws LexicalException, ParseException {
        String filterName = allCallStmtLabel.get(0);

        if (ChainFunctionConfig.getInstance().getStartFunc().contains(filterName)) {
            return;
        }

        if (!allCallStmtLabel.contains("filter")) {
            Lexer lexer = new Lexer();
            ArrayList<Token> tokens = lexer.analyse("filter()".chars().mapToObj(x -> (char) x));
            ASTNode colAST = Expr.parse(new PeekTokenIterator(tokens.stream()));
            Expr expr = new Expr(ASTNodeTypes.CHAIN_EXPR, new Token(TokenType.OPERATOR, "."));
            ASTNode copyVarNode = astNode.getChildren(0);
            ASTNode copyOpNode = astNode.getChildren(1);
            astNode.getChildren().set(0, colAST);
            astNode.getChildren().set(1, expr);
            expr.addChild(copyVarNode);
            expr.addChild(copyOpNode);
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
                        // Use the AST node's token for position information
                        if (ChainFunctionConfig.getInstance().getStartFunc().contains(name)) {
                            return Filter.translate(astNode, env, name);
                        }
                        if (ChainFunctionConfig.getInstance().getCalcFunc().contains(name)) {
                            return Calculator.Custom.translate(astNode, env);
                        }
                        if (ChainFunctionConfig.getInstance().getEndFunc().contains(name)) {
                            return Meet.translate(astNode, env,true);
                        }
                        Token errorToken = astNode.getLexeme();
                        throw new TranslatorException("Chain expression does not support '{}' operator", errorToken, name);
                }
            }
            String left = recursiveTrans(astNode.getChildren(0), env);
            String right = recursiveTrans(astNode.getChildren(1), env);
            return left + "\n" + right;
        }
        // Use the AST node's token for position information
        Token errorToken = astNode.getLexeme();
        throw new TranslatorException("Chain call translation error", errorToken);
    }
}
