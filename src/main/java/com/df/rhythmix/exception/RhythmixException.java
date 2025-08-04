package com.df.rhythmix.exception;

import cn.hutool.core.util.StrUtil;
import com.df.rhythmix.lexer.Token;
import lombok.Getter;

import java.util.List;

/**
 * Base exception class for all Rhythmix compiler exceptions.
 * Provides position information tracking and Rust-style error formatting support.
 * 
 * @author MFine
 * @version 1.0
 */
public abstract class RhythmixException extends Exception {

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
     * Constructor with message.
     */
    public RhythmixException(String msg) {
        super(msg);
        this.msg = msg;
    }

    /**
     * Constructor with message template and parameters.
     */
    public RhythmixException(CharSequence template, Object... params) {
        this.msg = StrUtil.format(template, params);
    }

    /**
     * Constructor with message template and token list.
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
     * Constructor with message and token for position information.
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
     * Constructor with message template, token, and parameters.
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
     * Constructor with explicit position information.
     */
    public RhythmixException(String msg, int characterPosition, int line, int column) {
        this.msg = msg;
        this.characterPosition = characterPosition;
        this.line = line;
        this.column = column;
    }

    /**
     * Check if position information is available.
     * @return true if position information is available, false otherwise
     */
    public boolean hasPositionInfo() {
        return characterPosition >= 0 && line >= 0 && column >= 0;
    }

    /**
     * Get the error message.
     * @return error message
     */
    @Override
    public String getMessage() {
        return this.msg != null ? this.msg : super.getMessage();
    }

    /**
     * Get the exception type name for error formatting.
     * @return exception type name (e.g., "lexical", "parse", "type")
     */
    public abstract String getExceptionType();

    /**
     * Get a formatted string representation of the exception.
     * Includes position information if available.
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
     * Capitalize the first letter of a string.
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    /**
     * Set position information from a token.
     */
    public void setPositionInfo(Token token) {
        if (token != null && token.hasPositionInfo()) {
            this.characterPosition = token.getStartPosition();
            this.line = token.getLine();
            this.column = token.getColumn();
        }
    }
}
