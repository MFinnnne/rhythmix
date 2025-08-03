package com.df.rhythmix.lexer;

import com.df.rhythmix.exception.LexicalException;

public class Token {
    private final TokenType type;
    private  String value;
    private final int startPosition;
    private final int endPosition;
    private final int line;
    private final int column;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
        this.startPosition = -1;
        this.endPosition = -1;
        this.line = -1;
        this.column = -1;
    }

    public Token(TokenType type, String value, int startPosition, int endPosition, int line, int column) {
        this.type = type;
        this.value = value;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return type;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public boolean hasPositionInfo() {
        return startPosition >= 0 && endPosition >= 0 && line >= 0 && column >= 0;
    }

    @Override
    public String toString() {
        return String.format("type %s,value %s", type, value);
    }

    public boolean isVariable() {
        return type == TokenType.VARIABLE;
    }

    public boolean isValue() {
        return this.isVariable() || this.isScalar();
    }

    public boolean isScalar() {
        return type == TokenType.FLOAT || type == TokenType.INTEGER
                || type == TokenType.STRING || type == TokenType.BOOLEAN;
    }

    public boolean isType() {
        return this.value.equals("bool") || this.value.equals("int") || this.value.equals("float") || this.value.equals("void") || this.value.equals("string");
    }

    public boolean isBracket() {
        return this.type == TokenType.BRACKET;
    }

    public static Token makeVarOrKeyword(PeekIterator<Character> it) {
        return makeVarOrKeyword(it, -1, -1, -1);
    }

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

    public static Token makeString(PeekIterator<Character> it) throws LexicalException {
        return makeString(it, -1, -1, -1);
    }

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


    public static Token makeOp(PeekIterator<Character> it) throws LexicalException {
        return makeOp(it, -1, -1, -1);
    }

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

    public static Token makeNumber(PeekIterator<Character> it) throws LexicalException {
        return makeNumber(it, -1, -1, -1);
    }

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

    public boolean isNumber() {
        return this.getType() == TokenType.FLOAT || this.getType() == TokenType.INTEGER;
    }

    public boolean isInteger() {
        return this.getType()== TokenType.INTEGER;
    }

    public boolean isFloat() {
        return this.getType()== TokenType.FLOAT;
    }

    public boolean isString() {
        return this.getType() == TokenType.STRING;
    }

    public boolean isOperator() {
        return this.getType() == TokenType.OPERATOR;
    }

    public boolean isBoolean() {
        return this.getType() == TokenType.BOOLEAN;
    }
}
