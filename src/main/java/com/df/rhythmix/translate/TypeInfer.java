package com.df.rhythmix.translate;

import com.df.rhythmix.exception.TypeInferException;
import com.df.rhythmix.lexer.TokenType;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.parser.ast.ASTNodeTypes;

import java.util.regex.Pattern;

/**
 * 类型推断器
 * 用于推断一个表达式经过运算之后得类型，目前仅支持简单推断，主要用在函数参数的推断上。
 */
public class TypeInfer {

    static Pattern ptnBasicOperator = Pattern.compile("^[*+-/]$");
    static Pattern ptnBitOperator = Pattern.compile("^[|&]$");
    static Pattern ptnLoginOperator = Pattern.compile("^[|&]$");

    static Pattern number = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");

    public static TokenType infer(ASTNode astNode, EnvProxy env) throws TypeInferException {
        switch (astNode.getType()) {
            case BINARY_EXPR:
                TokenType leftType = infer(astNode.getChildren(0), env);
                TokenType rightType = infer(astNode.getChildren(1), env);
                String op = astNode.getLabel();
                if (ptnBasicOperator.matcher(op).matches()) {
                    if ("/".equals(op)) {
                        if (leftType == TokenType.INTEGER || leftType == TokenType.FLOAT || rightType == TokenType.INTEGER || rightType == TokenType.FLOAT) {
                            return TokenType.FLOAT;
                        }
                        throw new TypeInferException("{} 位运算符不支持此类操作", op);
                    }
                    if (leftType == TokenType.FLOAT) {
                        if (rightType == TokenType.INTEGER || rightType == TokenType.FLOAT) {
                            return TokenType.FLOAT;
                        }
                        throw new TypeInferException(" '{}' 操作符两边只能是整数或者浮点数", op);
                    }
                    if (leftType == TokenType.INTEGER) {
                        if (rightType == TokenType.INTEGER) {
                            return TokenType.INTEGER;
                        }
                        if (rightType == TokenType.FLOAT) {
                            return TokenType.FLOAT;
                        }
                    }
                }
                if (ptnBitOperator.matcher(op).matches()) {
                    if (leftType == TokenType.INTEGER && rightType == TokenType.INTEGER) {
                        return TokenType.INTEGER;
                    }
                    if (leftType == TokenType.FLOAT || rightType == TokenType.FLOAT) {
                        throw new TypeInferException("{} 位运算符不支持浮点数", op);
                    }
                    throw new TypeInferException("{} 位运算符不支持此类操作", op);
                }
                if (ptnLoginOperator.matcher(op).matches()) {
                    if (leftType == TokenType.BOOLEAN && rightType == TokenType.BOOLEAN) {
                        return TokenType.BOOLEAN;
                    }
                    throw new TypeInferException("{} 位运算符只能针对bool型", op);
                }
                break;
            case UNARY_EXPR:
                TokenType tokenType = infer(astNode.getChildren(0), env);
                if (tokenType == TokenType.INTEGER) {
                    return TokenType.INTEGER;
                }
                throw new TypeInferException("{} 只能针对整形运算", astNode.getLabel());
            case VARIABLE:
                if (!astNode.getChildren().isEmpty() && astNode.getChildren(0).getType() == ASTNodeTypes.CALL_STMT) {
                    throw new TypeInferException("目前不支持函数返回类型的推断");
                }
                if (env.containsKey(astNode.getLabel())) {
                    throw new TypeInferException("未定义的变量:'{}'", astNode.getLabel());
                }
                String value = env.rawGet(astNode.getLabel()).toString();
                if (!number.matcher(value).matches()) {
                    throw new TypeInferException("目前仅支持预定义数字类型的变量");
                }
                if (value.contains(".")) {
                    return TokenType.FLOAT;
                }
                return TokenType.INTEGER;
            case SCALAR:
                return astNode.getLexeme().getType();
            default:
                throw new TypeInferException("类型推断错误，{}", astNode.getLabel());
        }
        throw new TypeInferException("类型推断错误，{}", astNode.getLabel());
    }

    public static boolean isNumber(TokenType type) {
        return type == TokenType.FLOAT || type == TokenType.INTEGER;
    }


}
