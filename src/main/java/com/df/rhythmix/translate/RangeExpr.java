package com.df.rhythmix.translate;

import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.parser.ast.RangeStmt;
import com.df.rhythmix.util.PeekTokenIterator;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.lexer.TokenType;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.df.rhythmix.pebble.TemplateEngine.ENGINE;

public class RangeExpr {


    /**
     * 区间表达式 [1,9] >1&&<9
     *
     * @param tokens tokens
     * @return String     转换得到的 aviator代码
     * @throws TranslatorException 翻译错误
     */
    public static String translate(List<Token> tokens, EnvProxy env) throws TranslatorException {

        try {
            PeekTokenIterator it = new PeekTokenIterator(tokens.stream());
            ASTNode stmt = RangeStmt.parser(it);
            if (stmt == null) {
                throw new TranslatorException("区间表达式解析结果为空");
            }
            return translate(stmt, env);
        } catch (Exception e) {
            e.printStackTrace();
            throw new TranslatorException(e.getMessage());
        }
    }

    public static String translate(ASTNode astNode, Map<String, Object> context, EnvProxy env) throws TranslatorException {

        try {
            PebbleTemplate template = ENGINE.getTemplate("expr/range.peb");
            Token leftSymbol = astNode.getChildren(0).getLexeme();
            Token rightSymbol = astNode.getChildren(1).getLexeme();
            ASTNode arg1 = astNode.getChildren(0).getChildren(0);
            ASTNode arg2 = astNode.getChildren(1).getChildren(0);
            Writer writer = new StringWriter();

            TokenType arg1Type = TypeInfer.infer(arg1, env);
            TokenType arg2Type = TypeInfer.infer(arg2, env);
            if (!TypeInfer.isNumber(arg1Type) || !TypeInfer.isNumber(arg2Type)) {
                throw new TranslatorException("区间表达式参数必须为数值类型");
            }

            if (arg1Type != arg2Type) {
                throw new TranslatorException("区间表达式参数类型必须相同");
            }

            context.put("leftValue", Translator.translate(arg1, context, env));
            context.put("rightValue", Translator.translate(arg2, context, env));

            if (arg1Type == TokenType.INTEGER) {
                context.put("type", "long");
            } else {
                context.put("type", "double");
            }

            if ("(".equals(leftSymbol.getValue())) {
                context.put("leftSymbol", ">");
            }

            if ("[".equals(leftSymbol.getValue())) {
                context.put("leftSymbol", ">=");
            }

            if (")".equals(rightSymbol.getValue())) {
                context.put("rightSymbol", "<");
            }

            if ("]".equals(rightSymbol.getValue())) {
                context.put("rightSymbol", "<=");
            }
            template.evaluate(writer, context);
            return writer.toString().replaceAll("\\r|\\n|\\s", "");
        } catch (Exception e) {
            throw new TranslatorException(e.getMessage());
        }

    }

    public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {

        try {
            Map<String, Object> context = new HashMap<>();
            return translate(astNode, context, env);
        } catch (Exception e) {
            throw new TranslatorException(e.getMessage());
        }

    }
}
