package io.github.mfinnnne.rhythmix.exception;

/**
 * Exception thrown during the type inference phase.
 * <p>
 * This exception indicates an error in determining the data type of an expression,
 * which is crucial for ensuring type safety and correctness before execution.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class TypeInferException extends RhythmixException {


    /**
     * Constructs a new type inference exception with a specific message.
     *
     * @param msg the detail message.
     */
    public TypeInferException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new type inference exception with a formatted message.
     *
     * @param template a message template with placeholders.
     * @param params   the parameters to be formatted into the message.
     */
    public TypeInferException(CharSequence template, Object... params) {
        super(template, params);
    }



    /** {@inheritDoc} */
    @Override
    public String getExceptionType() {
        return "type";
    }
}
