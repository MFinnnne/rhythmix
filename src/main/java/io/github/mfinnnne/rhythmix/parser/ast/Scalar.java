package io.github.mfinnnne.rhythmix.parser.ast;


import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;


/**
 * Represents a scalar literal value in the AST.
 * Scalars are constant values like numbers, strings, or booleans.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class Scalar extends Factor {
    /**
     * <p>Constructor for Scalar.</p>
     *
     * @param it a {@link PeekTokenIterator} object.
     */
    public Scalar(PeekTokenIterator it) {
        super(it);
    }
}
