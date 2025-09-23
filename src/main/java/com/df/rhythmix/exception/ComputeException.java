package com.df.rhythmix.exception;

/**
 * Exception thrown during the computation phase of an expression.
 * <p>
 * This exception indicates an error that occurred while executing the translated code,
 * such as type mismatches during arithmetic operations or other runtime issues.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class ComputeException extends RhythmixException {

    /**
     * <p>Constructor for ComputeException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public ComputeException(String message) {
        super(message);
    }

    /** {@inheritDoc} */
    @Override
    public String getExceptionType() {
        return "compute";
    }
}
