package org.example.fitvisionback.exceptions;

public class InsufficientCreditsException extends RuntimeException {
    public InsufficientCreditsException() {
        super("User has insufficient credits.");
    }
}
