package com.df.rhythmix.parser.ast;

import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.PeekTokenIterator;



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
