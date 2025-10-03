package io.github.mfinnnne.rhythmix.parser.ast;


import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;

/**
 * Represents a block of statements in the AST.
 * A block is a sequence of statements enclosed in curly braces {@code { ... }}.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class Block extends Stmt {
    /**
     * <p>Constructor for Block.</p>
     */
    protected Block() {
        super(ASTNodeTypes.BLOCK, "block");
    }

    /**
     * Parses a block of statements from the token stream.
     * Expects an opening brace '{', followed by zero or more statements, and a closing brace '}'.
     *
     * @param it the token iterator.
     * @return an {@link ASTNode} representing the block.
     * @throws ParseException if the block is not correctly formed.
     */
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
