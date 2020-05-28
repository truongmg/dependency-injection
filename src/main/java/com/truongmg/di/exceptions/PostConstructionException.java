package com.truongmg.di.exceptions;

public class PostConstructionException extends RuntimeException {

    public PostConstructionException(String message) {
        super(message);
    }

    public PostConstructionException(String message, Throwable cause) {
        super(message, cause);
    }
}
