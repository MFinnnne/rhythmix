package com.celi.ferrum.parser.ast;


import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.util.PeekTokenIterator;

public class Block extends Stmt {
    protected Block() {
        super(ASTNodeTypes.BLOCK, "block");
    }

    public static ASTNode parse( PeekTokenIterator it) throws ParseException {
        it.nextMatch("{");
        var block = new Block();
        ASTNode stmt = null;
        while ((stmt = Stmt.parseStmt( it)) != null) {
            block.addChild(stmt);
            if (it.nextMatch1(";")) {
                it.nextMatch(";");
            }
        }
        it.nextMatch("}");
        return block;
    }
}
