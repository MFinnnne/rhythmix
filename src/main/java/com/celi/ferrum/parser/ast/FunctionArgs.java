package com.celi.ferrum.parser.ast;

import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.util.PeekTokenIterator;



public class FunctionArgs extends ASTNode {
    public FunctionArgs() {
        super();
        this.label = "args";
    }

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
