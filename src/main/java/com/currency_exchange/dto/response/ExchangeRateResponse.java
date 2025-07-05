package com.currency_exchange.dto.response;

import java.math.BigDecimal;

public class ExchangeRateResponse {
    private final Long id;
    private final CurrencyResponse baseCurrency;
    private final CurrencyResponse targetCurrency;
    private final BigDecimal rate;

    public ExchangeRateResponse(Long id, CurrencyResponse baseCurrencyDto, CurrencyResponse targetCurrencyDto, BigDecimal rate) {
        this.id = id;
        this.baseCurrency = baseCurrencyDto;
        this.targetCurrency = targetCurrencyDto;
        this.rate = rate;
    }

    public CurrencyResponse getTargetCurrency() {
        return targetCurrency;
    }

    public Long getId() {
        return id;
    }

    public CurrencyResponse getBaseCurrency() {
        return baseCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
