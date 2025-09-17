package com.df.rhythmix.exception;

import com.df.rhythmix.lexer.Token;

/**
 * Exception thrown during the lexical analysis phase.
 * <p>
 * This exception indicates an error encountered by the lexer when tokenizing the source code,
 * such as encountering an unexpected or invalid character.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class LexicalException extends RhythmixException {

    /**
     * Constructs a new lexical exception for an unexpected character.
     *
     * @param c the unexpected character.
     */
    public LexicalException(char c) {
        super(String.format("Unexpected character %c", c));
    }

    /**
     * Constructs a new lexical exception with a specific message.
     *
     * @param msg the detail message.
     */
    public LexicalException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new lexical exception with a formatted message.
     *
     * @param s    the format string.
     * @param unit the value to be formatted into the string.
     */
    public LexicalException(String s, String unit) {
        super(String.format(s, unit));
    }

    /**
     * Constructs a new lexical exception with a message and the token that caused the error.
     *
     * @param msg   the detail message.
     * @param token the token associated with the error, used for position information.
     */
    public LexicalException(String msg, Token token) {
        super(msg, token);
    }

    /**
     * Constructs a new lexical exception for an unexpected character, including the token context.
     *
     * @param c     the unexpected character.
     * @param token the token associated with the error, used for position information.
     */
    public LexicalException(char c, Token token) {
        super(String.format("Unexpected character %c", c), token);
    }

    /**
     * Constructs a new lexical exception with a detailed message and precise location information.
     *
     * @param msg               the detail message.
     * @param characterPosition the character position within the source code where the error occurred.
     * @param line              the line number of the error.
     * @param column            the column number of the error.
     */
    public LexicalException(String msg, int characterPosition, int line, int column) {
        super(msg, characterPosition, line, column);
    }

    /** {@inheritDoc} */
    @Override
    public String getExceptionType() {
        return "lexical";
    }
}
