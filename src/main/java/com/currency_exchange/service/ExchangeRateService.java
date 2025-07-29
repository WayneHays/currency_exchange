package com.currency_exchange.service;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.dto.currency.CurrencyPairDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateRequestDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponseDto;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.entity.ExchangeRateData;
import com.currency_exchange.exception.*;
import com.currency_exchange.util.Mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExchangeRateService {
    public static final String FAILED_TO_SAVE_MESSAGE = "Failed to save exchange rate: [%s -> %s]";
    public static final String FAILED_TO_FIND_ALL_MESSAGE = "Failed to find all exchange rates";
    public static final String FAILED_TO_UPDATE_MESSAGE = "Failed to update exchange rate: [%s -> %s]";
    public static final String FAILED_TO_FIND_BY_PAIR_MESSAGE = "Failed to find exchange rate by pair [%s -> %s]";

    private final CurrencyDao currencyDao;
    private final ExchangeRatesDao exchangeRatesDao;

    public ExchangeRateService(CurrencyDao currencyDao, ExchangeRatesDao exchangeRatesDao) {
        this.currencyDao = currencyDao;
        this.exchangeRatesDao = exchangeRatesDao;
    }

    public ExchangeRateResponseDto save(ExchangeRateRequestDto dto) {
        try {
            Currency base = currencyDao.findByCode(dto.baseCurrencyCode());
            Currency target = currencyDao.findByCode(dto.targetCurrencyCode());
            ExchangeRate exchangeRate = Mapper.toExchangeRate(dto.rate(), base, target);
            ExchangeRate saved = exchangeRatesDao.save(exchangeRate);
            return Mapper.toExchangeRateResponseDto(saved, base, target);
        } catch (CurrencyNotFoundException e) {
            throw e;
        } catch (ExchangeRateAlreadyExistsException e) {
            throw new ExchangeRateAlreadyExistsException(dto.baseCurrencyCode(), dto.targetCurrencyCode());
        } catch (DaoException e) {
            throw new ServiceException(FAILED_TO_SAVE_MESSAGE
                    .formatted(dto.baseCurrencyCode(), dto.targetCurrencyCode()), e);
        }
    }

    public List<ExchangeRateResponseDto> findAll() {
        try {
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
        } catch (DaoException e) {
            throw new ServiceException(FAILED_TO_FIND_ALL_MESSAGE, e);
        }
    }

    public ExchangeRateResponseDto update(CurrencyPairDto dto, BigDecimal rate) {
        try {
            ExchangeRate updated = exchangeRatesDao.update(
                    dto.baseCurrencyCode(),
                    dto.targetCurrencyCode(),
                    rate);

            ExchangeRateData data = exchangeRatesDao.findExchangeRateWithCurrencyCodes(
                    dto.baseCurrencyCode(),
                    dto.targetCurrencyCode()
            );

            return Mapper.toExchangeRateResponseDto(updated, data.baseCurrency(), data.targetCurrency());
        } catch (ExchangeRateNotFoundException e) {
            throw e;
        } catch (DaoException e) {
            throw new ServiceException(FAILED_TO_UPDATE_MESSAGE.formatted(
                    dto.baseCurrencyCode(), dto.targetCurrencyCode()), e);
        }
    }

    public ExchangeRateResponseDto findByPair(CurrencyPairDto dto) {
        try {
            ExchangeRateData data = exchangeRatesDao.findExchangeRateWithCurrencyCodes(
                    dto.baseCurrencyCode(), dto.targetCurrencyCode());
            return Mapper.toExchangeRateResponseDto(data.exchangeRate(), data.baseCurrency(), data.targetCurrency());
        } catch (ExchangeRateNotFoundException e) {
            throw e;
        } catch (DaoException e) {
            throw new ServiceException(FAILED_TO_FIND_BY_PAIR_MESSAGE
                    .formatted(dto.baseCurrencyCode(), dto.targetCurrencyCode()), e);
        }
    }
}
