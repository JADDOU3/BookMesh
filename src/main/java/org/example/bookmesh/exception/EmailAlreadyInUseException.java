package org.example.bookmesh.exception;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String email) {
        super("An account with email '" + email + "' already exists");
    }
}