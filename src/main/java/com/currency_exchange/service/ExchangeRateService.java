package com.currency_exchange.service;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.dto.currency.CurrencyCodesDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateCreateDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponseDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateDto;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.exception.DaoException;
import com.currency_exchange.exception.ExchangeRateAlreadyExistsException;
import com.currency_exchange.exception.ExchangeRateNotFoundException;
import com.currency_exchange.exception.ServiceException;
import com.currency_exchange.util.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();

    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private ExchangeRateService() {
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public ExchangeRateResponseDto save(ExchangeRateCreateDto dto) {
        try {
            Currency base = currencyDao.findByCode(dto.baseCurrencyCode());
            Currency target = currencyDao.findByCode(dto.targetCurrencyCode());
            ExchangeRate exchangeRate = Mapper.toExchangeRate(dto.rate(), base, target);
            ExchangeRate saved = exchangeRatesDao.save(exchangeRate);
            return Mapper.toExchangeRateResponseDto(saved, base, target);
        } catch (ExchangeRateAlreadyExistsException e) {
            throw new ExchangeRateAlreadyExistsException(dto.baseCurrencyCode(), dto.targetCurrencyCode());
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public ExchangeRateResponseDto update(ExchangeRateUpdateDto dto) {
        try {
            Currency base = currencyDao.findByCode(dto.codesDto().baseCurrencyCode());
            Currency target = currencyDao.findByCode(dto.codesDto().targetCurrencyCode());
            ExchangeRate toUpdate = exchangeRatesDao.findByCurrencyIds(base.getId(), target.getId());
            ExchangeRate updated = exchangeRatesDao.update();
            return Mapper.toExchangeRateResponseDto(updated, base, target);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public List<ExchangeRateResponseDto> findAll() {
        List<ExchangeRate> rates = exchangeRatesDao.findAll();
        List<Long> currencyIds = new ArrayList<>();

        rates.forEach(rate -> {
            currencyIds.add(rate.getBaseCurrencyId());
            currencyIds.add(rate.getTargetCurrencyId());
        });

        Map<Long, Currency> currencies = currencyDao.findByIds(currencyIds);
        return rates.stream()
                .map(rate -> {
                    Currency base = currencies.get(rate.getBaseCurrencyId());
                    Currency target = currencies.get(rate.getTargetCurrencyId());
                    return Mapper.toExchangeRateResponseDto(rate, base, target);
                })
                .toList();
    }

    public ExchangeRateResponseDto findByPair(CurrencyCodesDto dto) {
        try {
            Currency base = currencyDao.findByCode(dto.baseCurrencyCode());
            Currency target = currencyDao.findByCode(dto.targetCurrencyCode());
            ExchangeRate exchangeRate = exchangeRatesDao.findByCurrencyIds(base.getId(), target.getId());
            return Mapper.toExchangeRateResponseDto(exchangeRate, base, target);
        } catch (ExchangeRateNotFoundException e) {
            throw new ExchangeRateNotFoundException(dto.baseCurrencyCode(), dto.targetCurrencyCode());
        }
    }
}
