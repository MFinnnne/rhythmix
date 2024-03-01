package com.celi.ferrum.parser.ast;

import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.util.PeekTokenIterator;

public class Stmt extends ASTNode {

    protected Stmt(ASTNodeTypes types, String label) {
        super(types, label);
    }

    public static ASTNode parseStmt(PeekTokenIterator it) throws ParseException {
        Token token = it.next();
        Token lookahead = it.peek();
        it.putBack();

        if (token.isVariable() && "=".equals(lookahead.getValue())) {
            return AssignStmt.parse(it);
        } else if ("let".equals(token.getValue())) {
            return DeclareStmt.parse(it);
        } else if ("func".equals(token.getValue())) {
            return FunctionDefineStmt.parse(it);
        } else if ("return".equals(token.getValue())) {
            return ReturnStmt.parse(it);
        } else if ("if".equals(token.getValue())) {
            return IfStmt.parse(it);
        } else if (token.isVariable() && "(".equals(lookahead.getValue())) {
            return  Expr.parse(it);
        }
        return null;
    }


}