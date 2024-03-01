package com.df.rhythmix.parser.ast;


import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.util.PeekTokenIterator;


public class IfStmt extends Stmt {
    protected IfStmt() {
        super( ASTNodeTypes.IF_STMT, "if");
    }

    public static ASTNode parse( PeekTokenIterator it) throws ParseException {
        Token token = it.nextMatch("if");
        it.nextMatch("(");
        IfStmt ifStmt = new IfStmt();
        ifStmt.setLexeme(token);
        ASTNode expr = Expr.parse(it);
        ifStmt.addChild(expr);
        it.nextMatch(")");
        ASTNode block = Block.parse( it);
        ifStmt.addChild(block);
        ASTNode tail = tail(it);
        if (tail != null) {
            ifStmt.addChild(tail);
        }
        return ifStmt;
    }

    public static ASTNode tail( PeekTokenIterator it) throws ParseException {
        if (!it.hasNext() || !it.peek().getValue().equals("else")) {
            return null;
        }
        it.nextMatch("else");
        Token lookahead = it.peek();
        if (lookahead.getValue().equals("{")) {
            return Block.parse( it);
        }
        if (lookahead.getValue().equals("if")){
            return parse( it);
        }
        return null;
    }
}
