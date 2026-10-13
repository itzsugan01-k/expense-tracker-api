package com.expensetracker.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleMethodNotValidException(MethodArgumentNotValidException ex,HttpServletRequest request){
        Map<String,String> errors=new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));

        return ResponseEntity.badRequest().body(new ApiError(400,"Request validation failed",errors, LocalDateTime.now(),request.getRequestURI()));
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleExpenseNotFoundException(ExpenseNotFoundException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404,ex.getMessage(), Collections.emptyMap(),LocalDateTime.now(),request.getRequestURI()));
    }

    @ExceptionHandler(BudgetNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleBudgetNotFoundException(BudgetNotFoundException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404,ex.getMessage(), Collections.emptyMap(),LocalDateTime.now(),request.getRequestURI()));
    }

    @ExceptionHandler(ExportException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleExportException(ExportException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(500,ex.getMessage(), Collections.emptyMap(),LocalDateTime.now(),request.getRequestURI()));
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<?> handleUserAlreadyExistException(UserAlreadyExistException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(409,ex.getMessage(), Collections.emptyMap(),LocalDateTime.now(),request.getRequestURI()));
    }

    @ExceptionHandler(UserNameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleUserNameNotFoundException(UserNameNotFoundException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404,ex.getMessage(), Collections.emptyMap(),LocalDateTime.now(),request.getRequestURI()));
    }
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(401,"Invalid email or password", Collections.emptyMap(),LocalDateTime.now(),request.getRequestURI()));
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleUserNameNotFoundException(Exception ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(500,"Something went wrong", Collections.emptyMap(),LocalDateTime.now(),request.getRequestURI()));
    }
    @ExceptionHandler(RefreshTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404,ex.getMessage(), Collections.emptyMap(),LocalDateTime.now(),request.getRequestURI()));
    }

}
