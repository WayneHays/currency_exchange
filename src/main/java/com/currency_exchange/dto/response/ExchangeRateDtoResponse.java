package com.currency_exchange.dto.response;

import java.math.BigDecimal;

public class ExchangeRateDtoResponse {
    private Long id;
    private CurrencyDtoResponse baseCurrency;
    private CurrencyDtoResponse targetCurrency;
    private BigDecimal rate;

    public ExchangeRateDtoResponse(Long id, CurrencyDtoResponse baseCurrency, CurrencyDtoResponse targetCurrency, BigDecimal rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public ExchangeRateDtoResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CurrencyDtoResponse getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(CurrencyDtoResponse baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public CurrencyDtoResponse getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(CurrencyDtoResponse targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
