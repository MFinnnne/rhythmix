package io.github.mfinnnne.rhythmix.parser.ast;

import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;

/**
 * Represents a variable identifier in the AST.
 * It can also hold an optional type token, used in function argument declarations.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class Variable extends Factor{
    private Token typeLexeme = null;

    /**
     * <p>Constructor for Variable.</p>
     *
     * @param it a {@link PeekTokenIterator} object.
     */
    public Variable( PeekTokenIterator it) {
        super( it);
    }

    /**
     * Gets the type token associated with this variable, if any.
     * This is typically used for typed function arguments.
     *
     * @return a {@link Token} representing the type, or {@code null}.
     */
    public Token getTypeLexeme() {
        return typeLexeme;
    }

    /**
     * Sets the type token for this variable.
     *
     * @param typeLexeme a {@link Token} object representing the type.
     */
    public void setTypeLexeme(Token typeLexeme) {
        this.typeLexeme = typeLexeme;
    }
}
