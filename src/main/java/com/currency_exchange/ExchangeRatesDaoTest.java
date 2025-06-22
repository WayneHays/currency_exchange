package com.currency_exchange;

import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.entity.ExchangeRates;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeRatesDaoTest {
    public static void main(String[] args) {
        updateTest();
    }

    private static ExchangeRates createTest() {
        var exchangeRates = new ExchangeRates();
        exchangeRates.setBaseCurrencyId(10L);
        exchangeRates.setTargetCurrencyId(11L);
        exchangeRates.setRate(BigDecimal.valueOf(0.00001));
        var exchangeRatesDao = ExchangeRatesDao.getInstance();
        return exchangeRatesDao.save(exchangeRates);
    }

    private static List<ExchangeRates> findAllTest() {
        return ExchangeRatesDao.getInstance().findAll();
    }

    private static void updateTest() {
        var exchangeRatesDao = ExchangeRatesDao.getInstance();
        var optionalExchangeRates = exchangeRatesDao.findById(2L);
        System.out.println(optionalExchangeRates);

        optionalExchangeRates.ifPresent(exchangeRates -> {
            exchangeRates.setRate(BigDecimal.valueOf(1.1111111));
            exchangeRatesDao.update(exchangeRates);
        });
    }
}
