package com.truongmg.di.exceptions;

public class BeanInstantiationException extends RuntimeException {

    public BeanInstantiationException(String message) {
        super(message);
    }

    public BeanInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
