package com.df.rhythmix.exception;

import com.df.rhythmix.lexer.Token;

import java.util.List;

/**
 * Exception thrown during the translation phase.
 * <p>
 * This exception indicates an error that occurred while translating the abstract syntax tree (AST)
 * into the target executable code, such as semantic errors, unsupported operations, or issues with UDFs.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class TranslatorException extends RhythmixException {

    /**
     * Constructs a new translator exception for an unexpected token character.
     *
     * @param c the unexpected character from a token.
     */
    public TranslatorException(char c) {
        super(String.format("Unexpected token %c", c));
    }

    /**
     * Constructs a new translator exception with a specific message.
     *
     * @param msg the detail message.
     */
    public TranslatorException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new translator exception with a formatted message.
     *
     * @param template a message template with placeholders.
     * @param params   the parameters to be formatted into the message.
     */
    public TranslatorException(CharSequence template, Object... params) {
        super(template, params);
    }

    /**
     * Constructs a new translator exception with a message template and a list of tokens.
     *
     * @param template a message template.
     * @param tokens   a list of tokens to be included in the message.
     */
    public TranslatorException(CharSequence template, List<Token> tokens) {
        super(template, tokens);
    }

    /**
     * Constructs a new translator exception with a message and the token that caused the error.
     *
     * @param msg   the detail message.
     * @param token the token associated with the error, used for position information.
     */
    public TranslatorException(String msg, Token token) {
        super(msg, token);
    }

    /**
     * Constructs a new translator exception with a formatted message and a token.
     *
     * @param template a message template.
     * @param token    the token associated with the error.
     * @param params   the parameters to be formatted into the message.
     */
    public TranslatorException(CharSequence template, Token token, Object... params) {
        super(template, token, params);
    }

    /**
     * Constructs a new translator exception with a detailed message and precise location information.
     *
     * @param msg               the detail message.
     * @param characterPosition the character position within the source code where the error occurred.
     * @param line              the line number of the error.
     * @param column            the column number of the error.
     */
    public TranslatorException(String msg, int characterPosition, int line, int column) {
        super(msg, characterPosition, line, column);
    }

    /** {@inheritDoc} */
    @Override
    public String getExceptionType() {
        return "translation";
    }
}
