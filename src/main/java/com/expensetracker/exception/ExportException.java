package com.expensetracker.exception;

public class ExportException extends RuntimeException{
    public ExportException(String message, Exception e){
        super(message);
    }
}
