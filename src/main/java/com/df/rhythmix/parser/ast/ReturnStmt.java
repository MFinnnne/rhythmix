package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.util.PeekTokenIterator;


/**
 * Represents a return statement in the AST, e.g., {@code return expr}.
 *
 * author MFine
 * @version 1.0
 * @since 1.0
 */
public class ReturnStmt extends Stmt {

    /**
     * <p>Constructor for ReturnStmt.</p>
     */
    protected ReturnStmt() {
        super(ASTNodeTypes.RETURN_STMT,"return");
    }

    /**
     * Parses a return statement followed by an expression.
     *
     * @param it the token iterator
     * @return the parsed {@link ReturnStmt}
     * @throws ParseException if the return statement is malformed
     */
    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        it.nextMatch("return");

        ReturnStmt returnStmt = new ReturnStmt();
        ASTNode parse = Expr.parse(it);
        returnStmt.addChild(parse);
        return returnStmt;
    }

}
