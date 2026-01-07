package com.msa.auth.exception;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String message) {
        super(message);
    }
}
