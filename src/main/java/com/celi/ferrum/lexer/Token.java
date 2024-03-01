package com.celi.ferrum.lexer;

import com.celi.ferrum.exception.LexicalException;

public class Token {
    private final TokenType type;
    private  String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }


    public TokenType getType() {
        return type;
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

        String s = "";
        while (it.hasNext()) {
            Character lookahead = it.peek();
            if (AlphabetHelper.isLiteral(lookahead)) {
                s += lookahead;
            } else {
                break;
            }
            it.next();
        }
        if (KeyWords.isKeyword(s)) {
            return new Token(TokenType.KEYWORD, s);
        }
        if (s.equals("true") || s.equals("false")) {
            return new Token(TokenType.BOOLEAN, s);
        }
        return new Token(TokenType.VARIABLE, s);
    }

    public static Token makeString(PeekIterator<Character> it) throws LexicalException {
        StringBuilder s = new StringBuilder();
        int state = 0;
        while (it.hasNext()) {
            char c = it.next();
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
                        return new Token(TokenType.STRING, s.toString());
                    } else {
                        s.append(c);
                    }
                    break;
                case 2:
                    if (c == '"') {
                        s.append(c);
                        return new Token(TokenType.STRING, s.toString());
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
        int state = 0;
        while (it.hasNext()) {
            Character lookahead = it.next();
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
                            return new Token(TokenType.OPERATOR, ",");
                        case ';':
                            return new Token(TokenType.OPERATOR, ";");
                        case '.':
                            return new Token(TokenType.OPERATOR,".");
                        default:
                            break;
                    }
                    break;
                case 1:
                    switch (lookahead) {
                        case '+':
                            return new Token(TokenType.OPERATOR, "++");
                        case '=':
                            return new Token(TokenType.OPERATOR, "+=");
                        default:
                            it.putBack();
                            return new Token(TokenType.OPERATOR, "+");
                    }
                case 2:
                    switch (lookahead) {
                        case '-':
                            return new Token(TokenType.OPERATOR, "--");
                        case '=':
                            return new Token(TokenType.OPERATOR, "-=");
                        case '>':
                            return new Token(TokenType.OPERATOR, "->");
                        default:
                            it.putBack();
                            return new Token(TokenType.OPERATOR, "-");
                    }
                case 3:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "*=");
                    }
                    it.putBack();
                    return new Token(TokenType.OPERATOR, "*");
                case 4:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "/=");
                    }
                    it.putBack();
                    return new Token(TokenType.OPERATOR, "/");
                case 5:
                    switch (lookahead) {
                        case '>':
                            return new Token(TokenType.OPERATOR, ">>");
                        case '=':
                            return new Token(TokenType.OPERATOR, ">=");
                        default:
                            it.putBack();
                            return new Token(TokenType.OPERATOR, ">");
                    }
                case 6:
                    switch (lookahead) {
                        case '>':
                            return new Token(TokenType.OPERATOR, "<<");
                        case '=':
                            return new Token(TokenType.OPERATOR, "<=");
                        default:
                            it.putBack();
                            return new Token(TokenType.OPERATOR, "<");
                    }
                case 7:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "==");
                    }
                    if (lookahead == '>') {
                        return new Token(TokenType.OPERATOR, "=>");
                    }
                    it.putBack();
                    return new Token(TokenType.OPERATOR, "=");
                case 8:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "!=");
                    }
                    it.putBack();
                    return new Token(TokenType.OPERATOR, "!");
                case 9:
                    switch (lookahead) {
                        case '=':
                            return new Token(TokenType.OPERATOR, "&=");
                        case '&':
                            return new Token(TokenType.OPERATOR, "&&");
                        default:
                            it.putBack();
                            return new Token(TokenType.OPERATOR, "&");
                    }
                case 10:
                    switch (lookahead) {
                        case '=':
                            return new Token(TokenType.OPERATOR, "|=");
                        case '|':
                            return new Token(TokenType.OPERATOR, "||");
                        default:
                            it.putBack();
                            return new Token(TokenType.OPERATOR, "|");
                    }
                case 11:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "^=");
                    }
                    it.putBack();
                    return new Token(TokenType.OPERATOR, "^");
                case 12:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "%=");
                    }
                    it.putBack();
                    return new Token(TokenType.OPERATOR, "%");
                default:
                    break;
            }
        }
        throw new LexicalException("unexpected error");
    }

    public static Token makeNumber(PeekIterator<Character> it) throws LexicalException {
        int state = 0;
        StringBuilder res = new StringBuilder();
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
                        return new Token(TokenType.INTEGER, "0");
                    }
                    break;
                case 2:
                    if (AlphabetHelper.isNumber(lookahead)) {
                        state = 2;
                    } else if (lookahead == '.') {
                        state = 4;
                    } else {
                        return new Token(TokenType.INTEGER, res.toString());
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
                        return new Token(TokenType.FLOAT, res.toString());
                    }
                    break;
                case 5:
                    if (AlphabetHelper.isNumber(lookahead)) {
                        state = 6;
                    } else {
                        return new Token(TokenType.FLOAT, res.toString());
                    }
                    break;
                case 6:
                    if (AlphabetHelper.isNumber(lookahead)) {
                        state = 6;
                    } else if (lookahead == '.') {
                        throw new LexicalException("unexpected token:" + lookahead);
                    } else {
                        return new Token(TokenType.FLOAT, res.toString());
                    }
                    break;
                default:
                    break;
            }
            it.next();
            res.append(lookahead);

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
