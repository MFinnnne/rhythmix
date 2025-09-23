package com.df.rhythmix.parser.ast;


import com.df.rhythmix.util.PeekTokenIterator;


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
     * @param it a {@link com.df.rhythmix.util.PeekTokenIterator} object.
     */
    public Scalar(PeekTokenIterator it) {
        super(it);
    }
}
