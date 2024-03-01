package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.PeekTokenIterator;
import com.df.rhythmix.util.PriorityTable;

import java.util.Arrays;
import java.util.List;

public class CompareStmt extends Stmt {


    protected CompareStmt() {
        super(ASTNodeTypes.COMPARE_EXPR, "compare expr");
    }


    private static final PriorityTable table = new PriorityTable(
            Arrays.asList("+", "-"),
            Arrays.asList("*", "/")
    );

    public static final List<String> SYMBOL = Arrays.asList("==", "!=", ">", "<", ">=", "<=");


    public static ASTNode parser(PeekTokenIterator it) throws ParseException {
        try {
            Token token = it.peek();
            CompareStmt compareStmt = new CompareStmt();
            if (SYMBOL.contains(token.getValue())) {
                compareStmt.lexeme = token;
                compareStmt.label = token.getValue();
                it.next();
            } else {
                throw new ParseException(token);
            }
            ASTNode arg = Expr.parse(it, table);
            compareStmt.addChild(arg);
            return compareStmt;
        } catch (ParseException e) {
            throw new ParseException(e.getMessage());
        } finally {
            Expr.table = new PriorityTable();
        }
    }
}
