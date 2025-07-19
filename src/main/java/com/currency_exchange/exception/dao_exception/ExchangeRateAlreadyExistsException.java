package com.currency_exchange.exception.dao_exception;

public class ExchangeRateAlreadyExistsException extends RuntimeException {
    private final String baseCode;
    private final String targetCode;

    public ExchangeRateAlreadyExistsException(String baseCode, String targetCode) {
        super("Exchange rate already exists for currencies [%s -> %s]".formatted(baseCode, targetCode));
        this.baseCode = baseCode;
        this.targetCode = targetCode;
    }

    public ExchangeRateAlreadyExistsException(Long baseId, Long targetId) {
        super("Exchange rate already exists for currencies ids: [%d -> %d]".formatted(baseId, targetId));
        this.baseCode = null;
        this.targetCode = null;
    }
}
