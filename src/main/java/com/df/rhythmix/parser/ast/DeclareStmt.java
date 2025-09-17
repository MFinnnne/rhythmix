package com.df.rhythmix.parser.ast;


import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.PeekTokenIterator;

/**
 * Represents a variable declaration statement in the AST.
 * e.g., {@code let a = 5;}
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class DeclareStmt extends Stmt {
    /**
     * <p>Constructor for DeclareStmt.</p>
     */
    protected DeclareStmt() {
        super(ASTNodeTypes.DECLARE_STMT, "declare");
    }


    /**
     * Parses a variable declaration statement from the token stream.
     * Expects the 'let' keyword, followed by a variable, an equals sign, and an expression.
     *
     * @param it a {@link com.df.rhythmix.util.PeekTokenIterator} object.
     * @return a {@link com.df.rhythmix.parser.ast.ASTNode} object representing the declaration.
     * @throws com.df.rhythmix.exception.ParseException if the syntax is incorrect.
     */
    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        DeclareStmt declareStmt = new DeclareStmt();
        it.nextMatch("let");
        Token tkn = it.peek();
        ASTNode factor = Factor.parse( it);
        if (factor == null) {
            throw new ParseException(tkn);
        }
        declareStmt.addChild(factor);
        Token token = it.nextMatch("=");
        ASTNode parse = Expr.parse(it);
        declareStmt.addChild(parse);
        declareStmt.setLexeme(token);
        return declareStmt;
    }
}
