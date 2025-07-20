package com.currency_exchange.exception;

public class ExchangeRateAlreadyExistsException extends RuntimeException {
    private final String baseCode;
    private final String targetCode;

    public ExchangeRateAlreadyExistsException(String baseCode, String targetCode) {
        super("Exchange rate already exists [%s -> %s]".formatted(baseCode.toUpperCase(), targetCode.toUpperCase()));
        this.baseCode = baseCode;
        this.targetCode = targetCode;
    }

    public ExchangeRateAlreadyExistsException(Long baseId, Long targetId) {
        super("Exchange rate already exists for currencies ids: [%d -> %d]".formatted(baseId, targetId));
        this.baseCode = null;
        this.targetCode = null;
    }
}
