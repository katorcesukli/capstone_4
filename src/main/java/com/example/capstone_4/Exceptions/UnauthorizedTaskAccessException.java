package com.example.capstone_4.Exceptions;

public class UnauthorizedTaskAccessException extends RuntimeException {
    public UnauthorizedTaskAccessException(String message) {
        super(message);
    }
}
