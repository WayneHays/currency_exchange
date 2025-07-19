package com.currency_exchange.exception.dao_exception;

public class ExchangeRateNotFoundException extends RuntimeException {
    private final String baseCode;
    private final String targetCode;

    public ExchangeRateNotFoundException(String baseCode, String targetCode) {
        super("Exchange rate not found for currency pair: %s/%s".formatted(baseCode, targetCode));
        this.baseCode = baseCode;
        this.targetCode = targetCode;
    }

    public ExchangeRateNotFoundException(Long baseId, Long targetId) {
        super("Exchange rate not found for currency IDs: %d/%d".formatted(baseId, targetId));
        this.baseCode = null;
        this.targetCode = null;
    }
}