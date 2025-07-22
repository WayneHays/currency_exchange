package com.currency_exchange.exception;

public class UnsupportedRateTypeException extends RuntimeException {

    public UnsupportedRateTypeException(String baseCode, String targetCode) {
        super("Unsupported rate type for currencies [%s->%s]".formatted(baseCode, targetCode));
    }
}
