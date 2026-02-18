package com.example.capstone_4.Exceptions;

public class TaskIdExistsException extends RuntimeException{
    public TaskIdExistsException(String message){
        super(message);
    }
}
