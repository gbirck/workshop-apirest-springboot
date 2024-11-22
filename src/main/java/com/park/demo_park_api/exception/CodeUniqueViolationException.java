package com.park.demo_park_api.exception;

public class CodeUniqueViolationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CodeUniqueViolationException(String message) {
        super(message);
    }
}
