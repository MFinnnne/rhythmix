/*
 * author: MFine
 * date: 2024-10-22 19:22:29
 * @LastEditTime: 2025-02-12 00:52:45
 * @LastEditors: MFine
 * @Description:
 */
package com.df.rhythmix.exception;


import cn.hutool.core.util.StrUtil;
import com.df.rhythmix.lexer.Token;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Exception thrown during the parsing phase.
 * <p>
 * This exception indicates a syntax error found by the parser, such as an unexpected token
 * or a violation of the language's grammar rules.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class ParseException extends RhythmixException {

    /**
     * Constructs a new parse exception with a specific message.
     *
     * @param msg the detail message.
     */
    public ParseException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new parse exception for an unexpected token.
     *
     * @param token the unexpected token that caused the error.
     */
    public ParseException(Token token) {
        super(String.format("Syntax error, unexpected character %s", token.getValue()), token);
    }

    /**
     * Constructs a new parse exception for an unexpected token when a specific character was expected.
     *
     * @param ch    the expected character or string.
     * @param token the unexpected token that was found.
     */
    public ParseException(String ch, Token token) {
        super(String.format("Syntax error, unexpected character \"%s\", expected \"%s\" ", token.getValue(), ch), token);
    }

    /**
     * Constructs a new parse exception using a message template and a variable number of tokens.
     * The position information from the first token is used for error reporting.
     *
     * @param template a message template with placeholders for the token values.
     * @param tokens   the tokens to be formatted into the message.
     */
    public ParseException(CharSequence template, Token... tokens) {
        super(StrUtil.format(template, String.join("", Arrays.stream(tokens).map(Token::getValue).collect(Collectors.joining()))));
        // Extract position info from the first token if available
        if (tokens.length > 0 && tokens[0] != null && tokens[0].hasPositionInfo()) {
            setPositionInfo(tokens[0]);
        }
    }

    /**
     * Constructs a new parse exception with a detailed message and precise location information.
     *
     * @param msg               the detail message.
     * @param characterPosition the character position within the source code where the error occurred.
     * @param line              the line number of the error.
     * @param column            the column number of the error.
     */
    public ParseException(String msg, int characterPosition, int line, int column) {
        super(msg, characterPosition, line, column);
    }

    /** {@inheritDoc} */
    @Override
    public String getExceptionType() {
        return "parse";
    }
}
