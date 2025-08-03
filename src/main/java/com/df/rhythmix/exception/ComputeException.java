package com.df.rhythmix.exception;

import com.df.rhythmix.lexer.Token;

public class ComputeException extends Exception{

    private String msg;
    private int characterPosition = -1;
    private int line = -1;
    private int column = -1;

    public ComputeException() {
        super();
        this.msg = "Computation error";
    }

    public ComputeException(String message) {
        super(message);
        this.msg = message;
    }

    public ComputeException(String msg, Token token) {
        this.msg = msg;
        if (token != null && token.hasPositionInfo()) {
            this.characterPosition = token.getStartPosition();
            this.line = token.getLine();
            this.column = token.getColumn();
        }
    }

    public ComputeException(String msg, int characterPosition, int line, int column) {
        this.msg = msg;
        this.characterPosition = characterPosition;
        this.line = line;
        this.column = column;
    }

    public int getCharacterPosition() {
        return characterPosition;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public boolean hasPositionInfo() {
        return characterPosition >= 0 && line >= 0 && column >= 0;
    }

    @Override
    public String getMessage() {
        if (hasPositionInfo()) {
            return String.format("Compute error at position %d (line %d, column %d): %s",
                characterPosition + 1, line, column, this.msg);
        }
        return this.msg;
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
