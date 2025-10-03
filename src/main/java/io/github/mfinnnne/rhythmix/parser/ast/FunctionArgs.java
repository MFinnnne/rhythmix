package io.github.mfinnnne.rhythmix.parser.ast;

import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;



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
     * @param it a {@link PeekTokenIterator} object.
     * @return a {@link ASTNode} object representing the function arguments.
     * @throws ParseException if the syntax is incorrect.
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
