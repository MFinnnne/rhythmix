package com.df.rhythmix.lexer;

import com.df.rhythmix.exception.LexicalException;

/**
 * Represents a token produced by the {@link Lexer}.
 * <p>
 * A token is a fundamental unit of source code, consisting of a type (e.g., VARIABLE, OPERATOR)
 * and a value (the actual text). It also holds positional information (line, column)
 * for accurate error reporting.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class Token {
    private final TokenType type;
    private  String value;
    private final int startPosition;
    private final int endPosition;
    private final int line;
    private final int column;

    /**
     * Gets the string value of the token.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the string value of the token.
     *
     * @param value a {@link java.lang.String} object.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Constructs a new token without position information.
     *
     * @param type  the type of the token.
     * @param value the string value of the token.
     */
    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
        this.startPosition = -1;
        this.endPosition = -1;
        this.line = -1;
        this.column = -1;
    }

    /**
     * Constructs a new token with full position information.
     *
     * @param type          the type of the token.
     * @param value         the string value of the token.
     * @param startPosition the starting character position in the source.
     * @param endPosition   the ending character position in the source.
     * @param line          the line number where the token appears.
     * @param column        the column number where the token begins.
     */
    public Token(TokenType type, String value, int startPosition, int endPosition, int line, int column) {
        this.type = type;
        this.value = value;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.line = line;
        this.column = column;
    }

    /**
     * Gets the type of the token.
     *
     * @return a {@link com.df.rhythmix.lexer.TokenType} object.
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Gets the starting character position of the token in the source code.
     *
     * @return the starting position, or -1 if not available.
     */
    public int getStartPosition() {
        return startPosition;
    }

    /**
     * Gets the ending character position of the token in the source code.
     *
     * @return the ending position, or -1 if not available.
     */
    public int getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the line number where the token appears.
     *
     * @return the line number, or -1 if not available.
     */
    public int getLine() {
        return line;
    }

    /**
     * Gets the column number where the token begins.
     *
     * @return the column number, or -1 if not available.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Checks if the token has valid position information.
     *
     * @return {@code true} if line, column, and position are all non-negative, {@code false} otherwise.
     */
    public boolean hasPositionInfo() {
        return startPosition >= 0 && endPosition >= 0 && line >= 0 && column >= 0;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("type %s,value %s", type, value);
    }

    /**
     * Checks if the token is a variable.
     *
     * @return {@code true} if the token type is {@link TokenType#VARIABLE}.
     */
    public boolean isVariable() {
        return type == TokenType.VARIABLE;
    }

    /**
     * Checks if the token represents a value (a variable or a scalar).
     *
     * @return {@code true} if the token is a variable or a scalar.
     */
    public boolean isValue() {
        return this.isVariable() || this.isScalar();
    }

    /**
     * Checks if the token is a scalar value (e.g., number, string, boolean).
     *
     * @return {@code true} if the token type is one of the scalar types.
     */
    public boolean isScalar() {
        return type == TokenType.FLOAT || type == TokenType.INTEGER
                || type == TokenType.STRING || type == TokenType.BOOLEAN;
    }

    /**
     * Checks if the token's value represents a data type keyword.
     *
     * @return {@code true} if the value is "bool", "int", "float", "void", or "string".
     */
    public boolean isType() {
        return this.value.equals("bool") || this.value.equals("int") || this.value.equals("float") || this.value.equals("void") || this.value.equals("string");
    }

    /**
     * Checks if the token is a bracket.
     *
     * @return {@code true} if the token type is {@link TokenType#BRACKET}.
     */
    public boolean isBracket() {
        return this.type == TokenType.BRACKET;
    }

    /**
     * Factory method to create a variable or keyword token from a character stream.
     *
     * @param it the character iterator.
     * @return the created {@link Token}.
     */
    public static Token makeVarOrKeyword(PeekIterator<Character> it) {
        return makeVarOrKeyword(it, -1, -1, -1);
    }

    /**
     * Factory method to create a variable or keyword token with position information.
     *
     * @param it       the character iterator.
     * @param startPos the starting position in the source.
     * @param line     the line number.
     * @param column   the column number.
     * @return the created {@link Token}.
     */
    public static Token makeVarOrKeyword(PeekIterator<Character> it, int startPos, int line, int column) {
        int currentPos = startPos;
        String s = "";
        while (it.hasNext()) {
            Character lookahead = it.peek();
            if (AlphabetHelper.isLiteral(lookahead)) {
                s += lookahead;
                currentPos++;
            } else {
                break;
            }
            it.next();
        }
        int endPos = startPos >= 0 ? currentPos - 1 : -1;

        if (KeyWords.isKeyword(s)) {
            return new Token(TokenType.KEYWORD, s, startPos, endPos, line, column);
        }
        if (s.equals("true") || s.equals("false")) {
            return new Token(TokenType.BOOLEAN, s, startPos, endPos, line, column);
        }
        return new Token(TokenType.VARIABLE, s, startPos, endPos, line, column);
    }

    /**
     * Factory method to create a string token from a character stream.
     *
     * @param it the character iterator.
     * @return the created {@link Token}.
     * @throws LexicalException if the string literal is malformed.
     */
    public static Token makeString(PeekIterator<Character> it) throws LexicalException {
        return makeString(it, -1, -1, -1);
    }

    /**
     * Factory method to create a string token with position information.
     *
     * @param it       the character iterator.
     * @param startPos the starting position in the source.
     * @param line     the line number.
     * @param column   the column number.
     * @return the created {@link Token}.
     * @throws LexicalException if the string literal is malformed.
     */
    public static Token makeString(PeekIterator<Character> it, int startPos, int line, int column) throws LexicalException {
        StringBuilder s = new StringBuilder();
        int state = 0;
        int currentPos = startPos;
        while (it.hasNext()) {
            char c = it.next();
            if (currentPos >= 0) currentPos++;
            switch (state) {
                case 0:
                    if (c == '\'') {
                        state = 1;
                        s.append(c);
                    }
                    if (c == '\"') {
                        state = 2;
                        s.append(c);
                    }
                    break;
                case 1:
                    if (c == '\'') {
                        s.append(c);
                        int endPos = startPos >= 0 ? currentPos - 1 : -1;
                        return new Token(TokenType.STRING, s.toString(), startPos, endPos, line, column);
                    } else {
                        s.append(c);
                    }
                    break;
                case 2:
                    if (c == '"') {
                        s.append(c);
                        int endPos = startPos >= 0 ? currentPos - 1 : -1;
                        return new Token(TokenType.STRING, s.toString(), startPos, endPos, line, column);
                    }
                    s.append(c);
                    break;
                default:
                    break;
            }
        }
        throw new LexicalException("unexpected error");
    }


    /**
     * Factory method to create an operator token from a character stream.
     *
     * @param it the character iterator.
     * @return the created {@link Token}.
     * @throws LexicalException if an unexpected character sequence is found.
     */
    public static Token makeOp(PeekIterator<Character> it) throws LexicalException {
        return makeOp(it, -1, -1, -1);
    }

    /**
     * Factory method to create an operator token with position information.
     *
     * @param it       the character iterator.
     * @param startPos the starting position in the source.
     * @param line     the line number.
     * @param column   the column number.
     * @return the created {@link Token}.
     * @throws LexicalException if an unexpected character sequence is found.
     */
    public static Token makeOp(PeekIterator<Character> it, int startPos, int line, int column) throws LexicalException {
        int state = 0;
        int currentPos = startPos;
        while (it.hasNext()) {
            Character lookahead = it.next();
            if (currentPos >= 0) currentPos++;
            switch (state) {
                case 0:
                    switch (lookahead) {
                        case '+':
                            state = 1;
                            break;
                        case '-':
                            state = 2;
                            break;
                        case '*':
                            state = 3;
                            break;
                        case '/':
                            state = 4;
                            break;
                        case '>':
                            state = 5;
                            break;
                        case '<':
                            state = 6;
                            break;
                        case '=':
                            state = 7;
                            break;
                        case '!':
                            state = 8;
                            break;
                        case '&':
                            state = 9;
                            break;
                        case '|':
                            state = 10;
                            break;
                        case '^':
                            state = 11;
                            break;
                        case '%':
                            state = 12;
                            break;
                        case ',':
                            return new Token(TokenType.OPERATOR, ",", startPos, startPos >= 0 ? startPos : -1, line, column);
                        case ';':
                            return new Token(TokenType.OPERATOR, ";", startPos, startPos >= 0 ? startPos : -1, line, column);
                        case '.':
                            return new Token(TokenType.OPERATOR,".", startPos, startPos >= 0 ? startPos : -1, line, column);
                        default:
                            break;
                    }
                    break;
                case 1:
                    switch (lookahead) {
                        case '+':
                            return new Token(TokenType.OPERATOR, "++", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                        case '=':
                            return new Token(TokenType.OPERATOR, "+=", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                        default:
                            it.putBack();
                            if (currentPos >= 0) currentPos--;
                            return new Token(TokenType.OPERATOR, "+", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                    }
                case 2:
                    switch (lookahead) {
                        case '-':
                            return new Token(TokenType.OPERATOR, "--", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                        case '=':
                            return new Token(TokenType.OPERATOR, "-=", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                        case '>':
                            return new Token(TokenType.OPERATOR, "->", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                        default:
                            it.putBack();
                            if (currentPos >= 0) currentPos--;
                            return new Token(TokenType.OPERATOR, "-", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                    }
                case 3:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "*=", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                    }
                    it.putBack();
                    if (currentPos >= 0) currentPos--;
                    return new Token(TokenType.OPERATOR, "*", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                case 4:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "/=", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                    }
                    it.putBack();
                    if (currentPos >= 0) currentPos--;
                    return new Token(TokenType.OPERATOR, "/", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                case 5:
                    switch (lookahead) {
                        case '>':
                            return new Token(TokenType.OPERATOR, ">>", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                        case '=':
                            return new Token(TokenType.OPERATOR, ">=", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                        default:
                            it.putBack();
                            if (currentPos >= 0) currentPos--;
                            return new Token(TokenType.OPERATOR, ">", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                    }
                case 6:
                    switch (lookahead) {
                        case '>':
                            return new Token(TokenType.OPERATOR, "<<", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                        case '=':
                            return new Token(TokenType.OPERATOR, "<=", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                        default:
                            it.putBack();
                            if (currentPos >= 0) currentPos--;
                            return new Token(TokenType.OPERATOR, "<", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                    }
                case 7:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "==", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                    }
                    if (lookahead == '>') {
                        return new Token(TokenType.OPERATOR, "=>", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                    }
                    it.putBack();
                    if (currentPos >= 0) currentPos--;
                    return new Token(TokenType.OPERATOR, "=", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                case 8:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "!=", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                    }
                    it.putBack();
                    if (currentPos >= 0) currentPos--;
                    return new Token(TokenType.OPERATOR, "!", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                case 9:
                    switch (lookahead) {
                        case '=':
                            return new Token(TokenType.OPERATOR, "&=", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                        case '&':
                            return new Token(TokenType.OPERATOR, "&&", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                        default:
                            it.putBack();
                            if (currentPos >= 0) currentPos--;
                            return new Token(TokenType.OPERATOR, "&", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                    }
                case 10:
                    switch (lookahead) {
                        case '=':
                            return new Token(TokenType.OPERATOR, "|=", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                        case '|':
                            return new Token(TokenType.OPERATOR, "||", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                        default:
                            it.putBack();
                            if (currentPos >= 0) currentPos--;
                            return new Token(TokenType.OPERATOR, "|", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                    }
                case 11:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "^=", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                    }
                    it.putBack();
                    if (currentPos >= 0) currentPos--;
                    return new Token(TokenType.OPERATOR, "^", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                case 12:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "%=", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                    }
                    it.putBack();
                    if (currentPos >= 0) currentPos--;
                    return new Token(TokenType.OPERATOR, "%", startPos, startPos >= 0 ? currentPos - 1 : -1, line, column);
                default:
                    break;
            }
        }
        throw new LexicalException("unexpected error");
    }

    /**
     * Factory method to create a number token (integer or float) from a character stream.
     *
     * @param it the character iterator.
     * @return the created {@link Token}.
     * @throws LexicalException if the number format is invalid.
     */
    public static Token makeNumber(PeekIterator<Character> it) throws LexicalException {
        return makeNumber(it, -1, -1, -1);
    }

    /**
     * Factory method to create a number token with position information.
     *
     * @param it       the character iterator.
     * @param startPos the starting position in the source.
     * @param line     the line number.
     * @param column   the column number.
     * @return the created {@link Token}.
     * @throws LexicalException if the number format is invalid.
     */
    public static Token makeNumber(PeekIterator<Character> it, int startPos, int line, int column) throws LexicalException {
        int state = 0;
        StringBuilder res = new StringBuilder();
        int currentPos = startPos;
        while (it.hasNext()) {
            Character lookahead = it.peek();
            switch (state) {
                case 0:
                    if (lookahead == '0') {
                        state = 1;
                    } else if (AlphabetHelper.isNumber(lookahead)) {
                        state = 2;
                    } else if (lookahead == '-' || lookahead == '+') {
                        state = 3;
                    } else if (lookahead == '.') {
                        res.append('.');
                        state = 5;
                    }
                    break;
                case 1:
                    if (lookahead == '0') {
                        state = 1;
                    } else if (AlphabetHelper.isNumber(lookahead)) {
                        if ("0".equals(res.toString())) {
                            res = new StringBuilder();
                        }
                        state = 2;
                    } else if (lookahead == '.') {
                        state = 4;
                    } else {
                        int endPos = startPos >= 0 ? currentPos - 1 : -1;
                        return new Token(TokenType.INTEGER, "0", startPos, endPos, line, column);
                    }
                    break;
                case 2:
                    if (AlphabetHelper.isNumber(lookahead)) {
                        state = 2;
                    } else if (lookahead == '.') {
                        state = 4;
                    } else {
                        int endPos = startPos >= 0 ? currentPos - 1 : -1;
                        return new Token(TokenType.INTEGER, res.toString(), startPos, endPos, line, column);
                    }
                    break;
                case 3:
                    if (AlphabetHelper.isNumber(lookahead)) {
                        state = 2;
                    } else if (lookahead == '.') {
                        state = 5;
                    } else {
                        throw new LexicalException("unexpected token:" + lookahead);
                    }
                    break;
                case 4:
                    if (lookahead == '.') {
                        throw new LexicalException("unexpected token:" + lookahead);
                    } else if (AlphabetHelper.isNumber(lookahead)) {
                        state = 6;
                    } else {
                        int endPos = startPos >= 0 ? currentPos - 1 : -1;
                        return new Token(TokenType.FLOAT, res.toString(), startPos, endPos, line, column);
                    }
                    break;
                case 5:
                    if (AlphabetHelper.isNumber(lookahead)) {
                        state = 6;
                    } else {
                        int endPos = startPos >= 0 ? currentPos - 1 : -1;
                        return new Token(TokenType.FLOAT, res.toString(), startPos, endPos, line, column);
                    }
                    break;
                case 6:
                    if (AlphabetHelper.isNumber(lookahead)) {
                        state = 6;
                    } else if (lookahead == '.') {
                        throw new LexicalException("unexpected token:" + lookahead);
                    } else {
                        int endPos = startPos >= 0 ? currentPos - 1 : -1;
                        return new Token(TokenType.FLOAT, res.toString(), startPos, endPos, line, column);
                    }
                    break;
                default:
                    break;
            }
            it.next();
            res.append(lookahead);
            if (currentPos >= 0) currentPos++;

        }
        throw new LexicalException("unexpected error");
    }

    /**
     * Checks if the token is a number (either integer or float).
     *
     * @return {@code true} if the token is a numeric type.
     */
    public boolean isNumber() {
        return this.getType() == TokenType.FLOAT || this.getType() == TokenType.INTEGER;
    }

    /**
     * Checks if the token is an integer.
     *
     * @return {@code true} if the token type is {@link TokenType#INTEGER}.
     */
    public boolean isInteger() {
        return this.getType()== TokenType.INTEGER;
    }

    /**
     * Checks if the token is a float.
     *
     * @return {@code true} if the token type is {@link TokenType#FLOAT}.
     */
    public boolean isFloat() {
        return this.getType()== TokenType.FLOAT;
    }

    /**
     * Checks if the token is a string.
     *
     * @return {@code true} if the token type is {@link TokenType#STRING}.
     */
    public boolean isString() {
        return this.getType() == TokenType.STRING;
    }

    /**
     * Checks if the token is an operator.
     *
     * @return {@code true} if the token type is {@link TokenType#OPERATOR}.
     */
    public boolean isOperator() {
        return this.getType() == TokenType.OPERATOR;
    }

    /**
     * Checks if the token is a boolean.
     *
     * @return {@code true} if the token type is {@link TokenType#BOOLEAN}.
     */
    public boolean isBoolean() {
        return this.getType() == TokenType.BOOLEAN;
    }
}
