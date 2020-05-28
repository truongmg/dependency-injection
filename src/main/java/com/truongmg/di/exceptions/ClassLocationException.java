package com.truongmg.di.exceptions;

public class ClassLocationException extends RuntimeException {

    public ClassLocationException(String message, Exception exception) {
        super(message, exception);
    }

    public ClassLocationException(String message) {
        super(message);
    }
}
