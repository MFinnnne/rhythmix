package io.github.mfinnnne.rhythmix.exception;

import cn.hutool.core.util.StrUtil;
import io.github.mfinnnne.rhythmix.lexer.Token;
import lombok.Getter;

import java.util.List;

/**
 * Base class for all Rhythmix compiler exceptions.
 * <p>
 * Tracks precise source position information (character position, line, and column) and
 * supports generating readable error text. Subclasses represent specific compiler phases
 * such as lexical, parse, type inference, translation, and compute.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public abstract class RhythmixException extends Exception {

    /**
     * The detail message of the exception.
     */
    @Getter
    protected String msg;
    /**
     * -- GETTER --
     *  Get the character position where the error occurred.
     */
    @Getter
    protected int characterPosition = -1;
    /**
     * -- GETTER --
     *  Get the line number where the error occurred.
     *
     */
    @Getter
    protected int line = -1;
    /**
     * -- GETTER --
     *  Get the column number where the error occurred.
     *
     */
    @Getter
    protected int column = -1;


    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param msg the detail message
     */
    public RhythmixException(String msg) {
        super(msg);
        this.msg = msg;
    }

    /**
     * Constructs a new exception using a message template and parameters.
     *
     * @param template the message template
     * @param params parameters to interpolate into the template
     */
    public RhythmixException(CharSequence template, Object... params) {
        this.msg = StrUtil.format(template, params);
    }

    /**
     * Constructs a new exception using a message template and the concatenated values of tokens.
     * Copies position information from the first token when available.
     *
     * @param template the message template
     * @param tokens the token list contributing to the message
     */
    public RhythmixException(CharSequence template, List<Token> tokens) {
        this.msg = StrUtil.format(template, String.join("", tokens.stream().map(Token::getValue).toArray(String[]::new)));
        if (!tokens.isEmpty() && tokens.get(0) != null && tokens.get(0).hasPositionInfo()) {
            Token firstToken = tokens.get(0);
            this.characterPosition = firstToken.getStartPosition();
            this.line = firstToken.getLine();
            this.column = firstToken.getColumn();
        }
    }

    /**
     * Constructs a new exception with a message and position copied from the provided token.
     *
     * @param msg the detail message
     * @param token the token to copy position information from; may be {@code null}
     */
    public RhythmixException(String msg, Token token) {
        this.msg = msg;
        if (token != null && token.hasPositionInfo()) {
            this.characterPosition = token.getStartPosition();
            this.line = token.getLine();
            this.column = token.getColumn();
        }
    }

    /**
     * Constructs a new exception using a template and parameters, copying position from the token.
     *
     * @param template the message template
     * @param token the token to copy position information from; may be {@code null}
     * @param params parameters to interpolate into the template
     */
    public RhythmixException(CharSequence template, Token token, Object... params) {
        this.msg = StrUtil.format(template, params);
        if (token != null && token.hasPositionInfo()) {
            this.characterPosition = token.getStartPosition();
            this.line = token.getLine();
            this.column = token.getColumn();
        }
    }

    /**
     * Constructs a new exception with explicit position information.
     *
     * @param msg the detail message
     * @param characterPosition zero-based character offset in the source
     * @param line one-based line number in the source
     * @param column one-based column number in the source
     */
    public RhythmixException(String msg, int characterPosition, int line, int column) {
        this.msg = msg;
        this.characterPosition = characterPosition;
        this.line = line;
        this.column = column;
    }

    /**
     * Indicates whether this exception carries source position information.
     *
     * @return {@code true} if position is set; {@code false} otherwise
     */
    public boolean hasPositionInfo() {
        return characterPosition >= 0 && line >= 0 && column >= 0;
    }

    /**
     * Returns the detail message string of this throwable.
     * Prefers the internal formatted message when available.
     *
     * @return the detail message string
     */
    @Override
    public String getMessage() {
        return this.msg != null ? this.msg : super.getMessage();
    }

    /**
     * Returns a short exception phase identifier for formatting.
     *
     * @return exception type name (e.g., "lexical", "parse", "type")
     */
    public abstract String getExceptionType();

    /**
     * Returns a human-readable representation, including position information when available.
     *
     * @return a formatted string suitable for logs and diagnostics
     */
    @Override
    public String toString() {
        String exceptionType = getExceptionType();
        String message = getMessage();
        
        if (hasPositionInfo()) {
            return String.format("%s error at position %d (line %d, column %d): %s",
                capitalize(exceptionType), characterPosition + 1, line, column, message);
        }
        return message != null ? message : super.toString();
    }

    /**
     * Capitalizes the first character of the provided string.
     *
     * @param str the string to capitalize; may be {@code null}
     * @return the capitalized string, or the original value if null/empty
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    /**
     * Copies the source position information from the given token.
     *
     * @param token the token to copy position from; ignored if {@code null} or without position
     */
    public void setPositionInfo(Token token) {
        if (token != null && token.hasPositionInfo()) {
            this.characterPosition = token.getStartPosition();
            this.line = token.getLine();
            this.column = token.getColumn();
        }
    }

}
