package com.example.financetracker.exception;

public class QueryLoadingException extends RuntimeException {
    public QueryLoadingException(String message) {
        super(message);
    }
    public QueryLoadingException(String message, Throwable throwable) { super(message, throwable); }
}
