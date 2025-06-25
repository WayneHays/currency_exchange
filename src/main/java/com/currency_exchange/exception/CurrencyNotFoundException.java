package com.currency_exchange.exception;

import jakarta.servlet.http.HttpServletResponse;

public class CurrencyNotFoundException extends BusinessException {

    public CurrencyNotFoundException(String message) {
        super(message, HttpServletResponse.SC_NOT_FOUND);
    }
}
