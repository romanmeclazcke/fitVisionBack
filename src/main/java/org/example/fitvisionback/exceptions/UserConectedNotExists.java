package org.example.fitvisionback.exceptions;

public class UserConectedNotExists extends RuntimeException {
    public UserConectedNotExists() {
        super("User connected does not exist");
    }
}
