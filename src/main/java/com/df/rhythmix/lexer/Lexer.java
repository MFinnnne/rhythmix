package com.df.rhythmix.lexer;


import com.df.rhythmix.exception.LexicalException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

public class Lexer {
    public ArrayList<Token> analyse(PeekIterator<Character> source) throws LexicalException {
        ArrayList<Token> tokens = new ArrayList<>();
        PeekIterator<Character> iterator = new PeekIterator<>(source, (char) 0);
        while (iterator.hasNext()) {
            Character next = iterator.next();
            if (next == (char) 0) {
                break;
            }
            Character lookahead = iterator.peek();
            if (next == ' ' || next.equals('\n')) {
                continue;
            }
            //删除注释
            if (next == '/') {
                if (lookahead == '/') {
                    while (iterator.hasNext() && iterator.next() != '\n') {
                        ;
                    }
                } else if (lookahead == '*') {
                    iterator.next();
                    boolean valid = false;
                    while (iterator.hasNext()) {
                        Character c = iterator.next();
                        if (c == '*' && iterator.peek() == '/') {
                            valid = true;
                            iterator.next();
                            break;
                        }
                    }
                    if (!valid) {
                        throw new LexicalException("comments not match");
                    }
                } else {
                    iterator.putBack();
                    tokens.add(Token.makeOp(iterator));
                }
                continue;
            }

            if (next == '{' || next == '}' || next == '(' || next == ')' || next == '[' || next == ']') {
                tokens.add(new Token(TokenType.BRACKET, next + ""));
                continue;
            }

            if (next == '<' || next == '>') {
                iterator.putBack();
                tokens.add(Token.makeOp(iterator));
                continue;
            }
            if (next == '"' || next == '\'') {
                iterator.putBack();
                tokens.add(Token.makeString(iterator));
                continue;
            }

            if (AlphabetHelper.isLetter(next)) {
                iterator.putBack();
                tokens.add(Token.makeVarOrKeyword(iterator));
                continue;
            }

            if (AlphabetHelper.isNumber(next)) {
                iterator.putBack();
                tokens.add(Token.makeNumber(iterator));
                continue;
            }
            if (next == '.' && AlphabetHelper.isLiteral(lookahead)) {
                iterator.putBack();
                tokens.add(Token.makeOp(iterator));
                continue;
            }
            if ((next == '+' || next == '-' || next == '.') && AlphabetHelper.isNumber(lookahead)) {
                Token token = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
                if (token != null) {
                    if ("(".equals(token.getValue())) {
                        iterator.putBack();
                        tokens.add(Token.makeNumber(iterator));
                        continue;
                    }
                    if (")".equals(token.getValue())) {
                        if (AlphabetHelper.isOperator(next)) {
                            iterator.putBack();
                            tokens.add(Token.makeOp(iterator));
                            continue;
                        }
                    }
                }
                if (token == null || !token.isValue()) {
                    iterator.putBack();
                    tokens.add(Token.makeNumber(iterator));
                    continue;
                }

            }
            if (AlphabetHelper.isOperator(next)) {
                iterator.putBack();
                tokens.add(Token.makeOp(iterator));
                continue;
            }
            throw new LexicalException(next);
        }
        return tokens;
    }

    public ArrayList<Token> analyse(Stream source) throws LexicalException {
        PeekIterator<Character> it = new PeekIterator<Character>(source, (char) 0);
        return analyse(it);
    }

    /**
     * 从源代码文件加载并解析
     */
    public static ArrayList<Token> fromFile(String src) throws FileNotFoundException, LexicalException {
        File file = new File(src);
        FileInputStream fileStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);

        BufferedReader br = new BufferedReader(inputStreamReader);


        /**
         * 利用BufferedReader每次读取一行
         */
        Iterator<Character> it = new Iterator<Character>() {
            private String line = null;
            private int cursor = 0;

            private void readLine() throws IOException {
                if (line == null || cursor == line.length()) {
                    line = br.readLine();
                    cursor = 0;
                }
            }

            @Override
            public boolean hasNext() {
                try {
                    readLine();
                    return line != null;
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            public Character next() {
                try {
                    readLine();
                    return line != null ? line.charAt(cursor++) : null;
                } catch (IOException e) {
                    return null;
                }
            }

        };

        PeekIterator<Character> peekIt = new PeekIterator<>(it, '\0');
        Lexer lexer = new Lexer();
        return lexer.analyse(peekIt);
    }
}
