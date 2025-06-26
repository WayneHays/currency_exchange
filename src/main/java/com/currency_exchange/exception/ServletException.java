package com.currency_exchange.exception;

public class ServletException extends RuntimeException{
    private final int httpStatus;
    private final String message;

    public ServletException(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
