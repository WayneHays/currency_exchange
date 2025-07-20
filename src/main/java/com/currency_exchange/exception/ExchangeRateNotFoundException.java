package com.currency_exchange.exception;

public class ExchangeRateNotFoundException extends RuntimeException {
    private final String baseCode;
    private final String targetCode;

    public ExchangeRateNotFoundException(String baseCode, String targetCode) {
        super("Exchange rate not found: [%s -> %s]".formatted(baseCode.toUpperCase(), targetCode.toUpperCase()));
        this.baseCode = baseCode;
        this.targetCode = targetCode;
    }

    public ExchangeRateNotFoundException(Long baseId, Long targetId) {
        super("Exchange rate not found for IDs: [%d -> %d]".formatted(baseId, targetId));
        this.baseCode = null;
        this.targetCode = null;
    }

    public ExchangeRateNotFoundException(Long id) {
        super("Exchange rate with id [%s] not found".formatted(id));
        this.baseCode = null;
        this.targetCode = null;
    }
}