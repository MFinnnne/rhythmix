package com.celi.ferrum.parser.ast;

import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.util.PeekTokenIterator;

public class FunctionDefineStmt extends Stmt {
    protected FunctionDefineStmt() {
        super( ASTNodeTypes.FUNCTION_DECLARE_STMT, "function");
    }

    public static ASTNode parse( PeekTokenIterator it) throws ParseException {
        it.nextMatch("func");
        FunctionDefineStmt functionDefineStmt = new FunctionDefineStmt();
        Token peek = it.peek();
        functionDefineStmt.setLexeme(peek);
        Variable var = (Variable) Factor.parse( it);
        functionDefineStmt.addChild(var);
        it.nextMatch("(");
        ASTNode parse = FunctionArgs.parse( it);
        functionDefineStmt.addChild(parse);
        it.nextMatch(")");
        Token returnType = it.next();
        if (!returnType.isType()) {
            throw new ParseException(returnType);
        }
        assert var != null;
        var.setTypeLexeme(returnType);
        ASTNode block = Block.parse( it);
        functionDefineStmt.addChild(block);
        return functionDefineStmt;
    }

    public ASTNode getArgs() {
        return this.getChildren(1);
    }

    public Variable getFunctionVariable() {
        return (Variable) this.getChildren(0);
    }

    public String getFuncType() {
        return this.getFunctionVariable().getTypeLexeme().getValue();
    }

    public Block getBlock() {
        return (Block) this.getChildren(2);
    }
}
