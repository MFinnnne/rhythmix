package com.df.rhythmix.exception;

public class ComputeException extends RhythmixException {

    public ComputeException(String message) {
        super(message);
    }

    @Override
    public String getExceptionType() {
        return "compute";
    }
}
