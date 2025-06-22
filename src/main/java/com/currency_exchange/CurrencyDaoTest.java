package com.currency_exchange;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.entity.Currency;

public class CurrencyDaoTest {
    public static void main(String[] args) {
      updateTest();
    }

    private static Currency createTest() {
        var currency = new Currency();
        currency.setCode("AAA");
        currency.setFullName("SSS");
        currency.setSign(")))");
        var currencyDao = CurrencyDao.getInstance();
        return currencyDao.save(currency);
    }

    private static void updateTest() {
        var currencyDao = CurrencyDao.getInstance();
        var optionalCurrency = currencyDao.findById(2L);
        System.out.println(optionalCurrency);

        optionalCurrency.ifPresent(currency -> {
            currency.setFullName("sosiHue");
            currencyDao.update(currency);
        });
    }

    private static void selectTest() {
        var currencies = CurrencyDao.getInstance().findAll();
        System.out.println(currencies);
    }
}
