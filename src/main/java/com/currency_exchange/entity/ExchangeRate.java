package com.currency_exchange.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeRate {
    private Long id;
    private Long baseCurrencyId;
    private Long targetCurrencyId;
    private BigDecimal rate;

    public ExchangeRate() {
    }

    public ExchangeRate(Long id, Long baseCurrencyId, Long targetCurrencyId, BigDecimal rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBaseCurrencyId(Long baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public void setTargetCurrencyId(Long targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Long getId() {
        return id;
    }

    public Long getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public Long getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate that = (ExchangeRate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
