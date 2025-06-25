package com.currency_exchange;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.entity.Currency;

import java.util.Optional;

public class CurrencyDaoTest {
    public static void main(String[] args) {
      findByCode("EUR");
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
        var optionalCurrency = currencyDao.findByCode("EUR");
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

    private static void findByCode(String code) {
        Optional<Currency> eur = CurrencyDao.getInstance().findByCode(code);
        if(eur.isPresent()) {
            System.out.println(eur.get());
        }
    }
}
