package com.example.capstone_4.Exceptions;

public class TaskIdDoesNotExistException extends RuntimeException {
    public TaskIdDoesNotExistException(String message) {
        super(message);
    }
}
