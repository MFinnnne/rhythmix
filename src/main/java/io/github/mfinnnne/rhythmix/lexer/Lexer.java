package io.github.mfinnnne.rhythmix.lexer;


import io.github.mfinnnne.rhythmix.exception.LexicalException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * The Lexer for the Rhythmix language.
 * <p>
 * This class is responsible for tokenizing Rhythmix source code. It reads a stream of characters
 * and converts it into a sequence of {@link Token}s, which can then be processed by the parser.
 * It handles basic language constructs like keywords, variables, numbers, strings, operators,
 * and comments.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class Lexer {
    /**
     * Analyzes the source code from a {@link PeekIterator} and produces a list of tokens.
     *
     * @param source the character stream iterator
     * @return an {@link ArrayList} of {@link Token}s
     * @throws LexicalException if an unexpected character or lexical error is encountered
     */
    public ArrayList<Token> analyse(PeekIterator<Character> source) throws LexicalException {
        ArrayList<Token> tokens = new ArrayList<>();
        PeekIterator<Character> iterator = new PeekIterator<>(source, (char) 0);
        int position = 0;
        int line = 1;
        int column = 1;

        while (iterator.hasNext()) {
            Character next = iterator.next();
            if (next == (char) 0) {
                break;
            }
            Character lookahead = iterator.peek();

            if (next == ' ') {
                position++;
                column++;
                continue;
            }
            if (next.equals('\n')) {
                position++;
                line++;
                column = 1;
                continue;
            }
            // 删除注释
            if (next == '/') {
                if (lookahead == '/') {
                    position++;
                    column++;
                    while (iterator.hasNext()) {
                        Character c = iterator.next();
                        position++;
                        if (c == '\n') {
                            line++;
                            column = 1;
                            break;
                        } else {
                            column++;
                        }
                    }
                } else if (lookahead == '*') {
                    iterator.next();
                    position += 2;
                    column += 2;
                    boolean valid = false;
                    while (iterator.hasNext()) {
                        Character c = iterator.next();
                        position++;
                        if (c == '\n') {
                            line++;
                            column = 1;
                        } else {
                            column++;
                        }
                        if (c == '*' && iterator.peek() == '/') {
                            valid = true;
                            iterator.next();
                            position++;
                            column++;
                            break;
                        }
                    }
                    if (!valid) {
                        throw new LexicalException("comments not match");
                    }
                } else {
                    iterator.putBack();
                    tokens.add(Token.makeOp(iterator, position, line, column));
                    position++;
                    column++;
                }
                continue;
            }

            if (next == '{' || next == '}' || next == '(' || next == ')' || next == '[' || next == ']') {
                tokens.add(new Token(TokenType.BRACKET, next + "", position, position, line, column));
                position++;
                column++;
                continue;
            }

            if (next == '<' || next == '>') {
                iterator.putBack();
                Token token = Token.makeOp(iterator, position, line, column);
                tokens.add(token);
                position += token.getValue().length();
                column += token.getValue().length();
                continue;
            }
            if (next == '"' || next == '\'') {
                iterator.putBack();
                Token token = Token.makeString(iterator, position, line, column);
                tokens.add(token);
                position += token.getValue().length();
                column += token.getValue().length();
                continue;
            }

            if (AlphabetHelper.isLetter(next)) {
                iterator.putBack();
                Token token = Token.makeVarOrKeyword(iterator, position, line, column);
                tokens.add(token);
                position += token.getValue().length();
                column += token.getValue().length();
                continue;
            }

            if (AlphabetHelper.isNumber(next)) {
                iterator.putBack();
                Token token = Token.makeNumber(iterator, position, line, column);
                tokens.add(token);
                position += token.getValue().length();
                column += token.getValue().length();
                continue;
            }
            if (next == '.' && AlphabetHelper.isLiteral(lookahead)) {
                iterator.putBack();
                Token token = Token.makeOp(iterator, position, line, column);
                tokens.add(token);
                position += token.getValue().length();
                column += token.getValue().length();
                continue;
            }
            if ((next == '+' || next == '-' || next == '.') && AlphabetHelper.isNumber(lookahead)) {
                Token token = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
                if (token != null) {
                    if ("(".equals(token.getValue())) {
                        iterator.putBack();
                        Token numToken = Token.makeNumber(iterator, position, line, column);
                        tokens.add(numToken);
                        position += numToken.getValue().length();
                        column += numToken.getValue().length();
                        continue;
                    }
                    if (")".equals(token.getValue())) {
                        if (AlphabetHelper.isOperator(next)) {
                            iterator.putBack();
                            Token opToken = Token.makeOp(iterator, position, line, column);
                            tokens.add(opToken);
                            position += opToken.getValue().length();
                            column += opToken.getValue().length();
                            continue;
                        }
                    }
                }
                if (token == null || !token.isValue()) {
                    iterator.putBack();
                    Token numToken = Token.makeNumber(iterator, position, line, column);
                    tokens.add(numToken);
                    position += numToken.getValue().length();
                    column += numToken.getValue().length();
                    continue;
                }

            }
            if (AlphabetHelper.isOperator(next)) {
                iterator.putBack();
                Token opToken = Token.makeOp(iterator, position, line, column);
                tokens.add(opToken);
                position += opToken.getValue().length();
                column += opToken.getValue().length();
                continue;
            }
            throw new LexicalException(next);
        }
        return tokens;
    }

    /**
     * Analyzes the source code from a {@link Stream} of characters.
     *
     * @param source the character stream
     * @return an {@link ArrayList} of {@link Token}s
     * @throws LexicalException if a lexical error occurs
     */
    public ArrayList<Token> analyse(Stream<Character> source) throws LexicalException {
        PeekIterator<Character> it = new PeekIterator<Character>(source, (char) 0);
        return analyse(it);
    }

    /**
     * Loads and analyzes source code from a file.
     *
     * @param src the path to the source code file
     * @return an {@link ArrayList} of {@link Token}s
     * @throws FileNotFoundException if the file is not found
     * @throws LexicalException      if a lexical error occurs during analysis
     */
    public static ArrayList<Token> fromFile(String src) throws FileNotFoundException, LexicalException {
        File file = new File(src);
        FileInputStream fileStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);

        try (BufferedReader br = new BufferedReader(inputStreamReader)) {
            /** 利用BufferedReader每次读取一行 */
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
        } catch (IOException ioe) {
            throw new LexicalException("I/O error while reading source file: " + ioe.getMessage());
        }
    }
}
