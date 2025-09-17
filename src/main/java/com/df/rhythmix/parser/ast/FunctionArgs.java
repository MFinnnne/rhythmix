package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.PeekTokenIterator;



/**
 * Represents the argument list of a function definition in the AST.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class FunctionArgs extends ASTNode {
    /**
     * <p>Constructor for FunctionArgs.</p>
     */
    public FunctionArgs() {
        super();
        this.label = "args";
    }

    /**
     * Parses the argument list of a function definition.
     * Expects a comma-separated list of typed variables.
     *
     * @param it a {@link com.df.rhythmix.util.PeekTokenIterator} object.
     * @return a {@link com.df.rhythmix.parser.ast.ASTNode} object representing the function arguments.
     * @throws com.df.rhythmix.exception.ParseException if the syntax is incorrect.
     */
    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        FunctionArgs functionArgs = new FunctionArgs();

        while (it.peek().isType()) {
            Token type = it.next();
            Variable var = (Variable) Factor.parse(it);
            assert var != null;
            var.setTypeLexeme(type);
            functionArgs.addChild(var);
            if (!it.peek().getValue().equals(")")) {
                it.nextMatch(",");
            }
        }

        return functionArgs;
    }
}
