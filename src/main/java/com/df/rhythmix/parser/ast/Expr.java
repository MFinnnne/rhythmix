package com.df.rhythmix.parser.ast;

import cn.hutool.core.util.StrUtil;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.*;

import java.util.List;
import java.util.stream.Collectors;


public class Expr extends ASTNode {

    static PriorityTable table = new PriorityTable();


    public Expr() {
        super();
    }

    public Expr(ASTNodeTypes type, Token lexeme) {
        super();
        this.type = type;
        this.lexeme = lexeme;
        this.label = lexeme.getValue();
    }


    // left: E(k) -> E(k) op(k) E(k+1) | E(k+1)
    // right:
    // E(k) -> E(K+1)E_(k)
    //    var e = new Expr();e.left = E(k+1);e.op = op(K);e.right=E(k+1)E_(k)
    // E_(k)->op(k)E(k+1)E_(k)|ε
    //最高优先级处理
    //  E(t)->F E_(k)|U E_(k)
    //  E_(t)-> op(t) E(t)E_(t) | ε
    //
    public static ASTNode E(int k, PeekTokenIterator it) throws ParseException {
        if (k < table.size() - 1) {
            return combine(it, () -> E(k + 1, it), () -> E_(k, it));
        } else {
            return race(it,
                    () -> combine(it, () -> U(it), () -> E_(k, it)),
                    () -> combine(it, () -> F(it), () -> E_(k, it))
            );
        }
    }

    public static ASTNode U(PeekTokenIterator it) throws ParseException {
        Token token = it.peek();
        String value = token.getValue();
        ASTNode expr = null;
        if ("[".equals(value)) {
            return RangeStmt.parser(it);
        } else if ("(".equals(value)) {
            return parseRangeOrArrowFuncOrBrackExpr(it);
        } else if ("++".equals(value) || "--".equals(value) ) {
            return parseUnaryExpr(it, value);
        } else if (table.get(1).contains(value)) {
            if ("<".equals(token.getValue())) {
                if (MutStmt.isMutStmt(it)) {
                    return MutStmt.parse(it);
                }
            }
            expr = CompareStmt.parser(it);
            return expr;
        } else if ("{".equals(token.getValue())) {
            //由于目前'{'开头的只有箭头表达式 因此暂时对于箭头表达式的判断就只是判断是否 '{'开头
            //如果后续不符合语法规则则会报解析错误
            expr = ArrowStmt.parse(it);
            return expr;
        }
        return null;
    }

    private static Expr parseUnaryExpr(PeekTokenIterator it, String value) throws ParseException {
        var t = it.peek();
        it.nextMatch(value);
        Expr unaryExpr = new Expr(ASTNodeTypes.UNARY_EXPR, t);
        unaryExpr.addChild(E(0, it));
        return unaryExpr;
    }

    private static ASTNode parseRangeOrArrowFuncOrBrackExpr(PeekTokenIterator it) throws ParseException {
        ASTNode expr;
        if (it.hasNext() && ArrowFuncStmt.isArrowFunc(it)) {
            return ArrowFuncStmt.parse(it);
        }
        expr = RangeStmt.parser(it);
        if (expr == null) {
            it.nextMatch("(");
            expr = E(0, it);
            it.nextMatch(")");
            return expr;
        }
        return expr;
    }

    public static ASTNode F(PeekTokenIterator it) throws ParseException {
        ASTNode parse = Factor.parse(it);
        Token lookahead = it.peek();
        if (parse instanceof Variable) {
            // 如果变量后面跟着感叹号表示这是严格连续标识
            if (it.hasNext() && "!".equals(lookahead.getValue())) {
                parse.setLabel(parse.getLabel() + lookahead.getValue());
                parse.getLexeme().setValue(parse.getLexeme().getValue() + lookahead.getValue());
                it.next();
                lookahead = it.peek();
            }

            if (it.hasNext() && "(".equals(lookahead.getValue())) {
                ASTNode node = CallStmt.parse(it);
                parse.addChild(node);
                if (it.hasNext() && it.nextMatch1(".")) {
                    Expr chainExpr = new Expr(ASTNodeTypes.CHAIN_EXPR, it.nextMatch("."));
                    chainExpr.addChild(parse);
                    ASTNode f = F(it);
                    if (f.getChildren().isEmpty()) {
                        throw new ParseException("链式调用的对象必须是函数，现在是：{}", f.getLexeme());
                    } else if (f.getChildren(0).getType() == ASTNodeTypes.CALL_STMT
                            || f.getType() == ASTNodeTypes.CHAIN_EXPR) {
                        chainExpr.addChild(f);
                    }
                    return chainExpr;
                }
            } else {
                return parse;
            }
        }
        return parse;
    }

    private static ASTNode race(PeekTokenIterator it, ExprHOF aFunc, ExprHOF bFunc) throws ParseException {
        if (!it.hasNext()) {
            return null;
        }
        var a = aFunc.hoc();
        if (a != null) {
            return a;
        }
        return bFunc.hoc();
    }

    private static ASTNode E_(int k, PeekTokenIterator it) throws ParseException {
        var token = it.peek();
        var value = token.getValue();
        if ((k < table.size() && table.get(k).contains(value))) {
            Expr expr;
            if ("!".equals(value)) {
                expr = new Expr(ASTNodeTypes.UNARY_EXPR, it.nextMatch(value));
            } else {
                expr= new Expr(ASTNodeTypes.BINARY_EXPR, it.nextMatch(value));
            }
            expr.addChild(combine(it,
                    () -> E(k + 1, it),
                    () -> E_(k, it)
            ));
            return expr;
        }
        return null;
    }


    private static ASTNode combine(PeekTokenIterator it, ExprHOF aFunc, ExprHOF bFunc) throws ParseException {
        var a = aFunc.hoc();
        if (a == null) {
            return it.hasNext() ? bFunc.hoc() : null;
        }
        var b = it.hasNext() ? bFunc.hoc() : null;
        if (b == null) {
            return a;
        }
        Expr expr = new Expr(ASTNodeTypes.BINARY_EXPR, b.getLexeme());
        expr.addChild(a);
        expr.addChild(b.getChildren(0));
        return expr;
    }

    public static ASTNode parse(PeekTokenIterator tokenIt) throws ParseException {
        Expr.table = new PriorityTable();
        return E(0, tokenIt);
    }
    

    public static ASTNode parse(PeekTokenIterator tokenIt, PriorityTable table) throws ParseException {
        Expr.table = table;
        return E(0, tokenIt);
    }

    public static ASTNode parse(PeekTokenIterator tokenIt, PriorityTable table, List<ASTNodeTypes> forbidChildExpr) throws ParseException {
        Expr.table = table;
        ASTNode expr = E(0, tokenIt);
        List<ASTNodeTypes> types = ParserUtils.toBFSASTType(expr);
        List<ASTNodeTypes> collect = types.stream().filter(forbidChildExpr::contains).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            String log = StrUtil.format("此表达式不支持嵌套 {}", collect);
            throw new ParseException(log);
        }
        return expr;
    }
}