package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.PeekTokenIterator;
import com.df.rhythmix.util.PriorityTable;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a comparison statement in the AST.
 * e.g., {@code > 10}, {@code == "hello"}
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class CompareStmt extends Stmt {


    /**
     * <p>Constructor for CompareStmt.</p>
     */
    protected CompareStmt() {
        super(ASTNodeTypes.COMPARE_EXPR, "compare expr");
    }


    private static final PriorityTable table = new PriorityTable(
            Arrays.asList("+", "-"),
            Arrays.asList("*", "/")
    );

    /** Constant <code>SYMBOL</code> */
    public static final List<String> SYMBOL = Arrays.asList("==", "!=", ">", "<", ">=", "<=");


    /**
     * Parses a comparison statement from the token stream.
     * It expects a comparison operator followed by an expression.
     *
     * @param it a {@link com.df.rhythmix.util.PeekTokenIterator} object.
     * @return a {@link com.df.rhythmix.parser.ast.ASTNode} object representing the comparison.
     * @throws com.df.rhythmix.exception.ParseException if a valid comparison operator is not found.
     */
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
        } finally {
            Expr.table = new PriorityTable();
        }
    }
}
