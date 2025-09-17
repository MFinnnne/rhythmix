package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.PeekTokenIterator;
import com.df.rhythmix.util.PriorityTable;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

@Slf4j
/**
 * <p>RangeStmt class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
public class RangeStmt extends Stmt {


    /**
     * <p>Constructor for RangeStmt.</p>
     */
    protected RangeStmt() {
        super(ASTNodeTypes.RANGE_EXPR, "range expr");
    }

    private static final List<String> LEFT = Arrays.asList("(", "[");
    private static final List<String> RIGHT = Arrays.asList(")", "]");

    private static final PriorityTable table = new PriorityTable(
            Arrays.asList("+", "-"),
            Arrays.asList("*", "/")
    );

    /**
     * <p>parser.</p>
     *
     * @param it a {@link com.df.rhythmix.util.PeekTokenIterator} object.
     * @return a {@link com.df.rhythmix.parser.ast.ASTNode} object.
     * @throws com.df.rhythmix.exception.ParseException if any.
     */
    public static ASTNode parser(PeekTokenIterator it) throws ParseException {
        try {
            boolean isRange = isRangeStmt(it);
            if (!isRange) {
                return null;
            }
            Token token = it.peek();
            RangeStmt rangeStmt = new RangeStmt();
            rangeStmt.lexeme = token;
            Expr leftExpr;
            if (LEFT.contains(token.getValue())) {
                leftExpr = new Expr(ASTNodeTypes.UNARY_EXPR, token);
                rangeStmt.addChild(leftExpr);
                it.next();
            } else {
                return null;
            }
            if (it.nextMatch1(",")) {
                throw new ParseException(it.peek());
            }
            ASTNode arg1 = Expr.parse(it);
            if (arg1 == null) {
                return null;
            }
            if (arg1 instanceof RangeStmt) {
                return null;
            }
            leftExpr.addChild(arg1);
            if (it.nextMatch1(",")) {
                it.next();
            } else {
                return null;
            }
            if (!it.peek().isNumber() && !it.peek().isVariable() && !it.peek().isScalar()){
                throw new ParseException(it.peek());
            }
            ASTNode arg2 = Expr.parse(it);
            if (arg2 == null) {
                return null;
            }
            if (arg2 instanceof RangeStmt) {
                return null;
            }
            Expr rightExpr;
            token = it.peek();
            if (RIGHT.contains(token.getValue())) {
                rightExpr = new Expr(ASTNodeTypes.UNARY_EXPR, token);
                rightExpr.addChild(arg2);
                rangeStmt.addChild(rightExpr);
                it.next();
            } else {
                throw new ParseException(token);
            }
            return rangeStmt;
        } catch (ParseException e) {
            throw e;
        } finally {
            Expr.table = new PriorityTable();
        }
    }

    /**
     * <p>isRangeStmt.</p>
     *
     * @param iterator a {@link com.df.rhythmix.util.PeekTokenIterator} object.
     * @return a boolean.
     */
    public static boolean isRangeStmt(PeekTokenIterator iterator) {
        Stack<Token> stack = new Stack<>();
        boolean condition = false;
        iterator.record();
        try {
            while (iterator.hasNext()) {
                if (iterator.nextMatchContain("(", "[")) {
                    stack.push(iterator.peek());
                }
                if (iterator.nextMatchContain(")", "]")) {
                    stack.pop();
                }
                if (stack.size() == 1 && iterator.nextMatch1(",")) {
                    condition = true;
                }
                if (stack.size() == 0) {
                    if (condition) {
                        return true;
                    } else {
                        return false;
                    }
                }
                iterator.next();
            }

            return false;
        } finally {
            iterator.backRecord();
        }
    }
}
