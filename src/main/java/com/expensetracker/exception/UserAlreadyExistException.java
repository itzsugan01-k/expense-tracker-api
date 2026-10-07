package com.expensetracker.exception;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException(String email){
        super("user is already registered in "+email);
    }
}
