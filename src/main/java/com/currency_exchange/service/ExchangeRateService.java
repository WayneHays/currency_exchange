package com.currency_exchange.service;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.dto.currency.CurrencyCodesDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateCreateDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponseDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateDto;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.CurrencyPair;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.ExchangeRateAlreadyExistsException;
import com.currency_exchange.exception.dao_exception.ExchangeRateNotFoundException;
import com.currency_exchange.exception.service_exception.ServiceException;
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
            CurrencyPair pair = findCurrencyPair(dto.baseCurrencyCode(), dto.targetCurrencyCode());
            ExchangeRate exchangeRate = Mapper.toExchangeRate(dto, pair);
            ExchangeRate saved = exchangeRatesDao.save(exchangeRate);
            return Mapper.toExchangeRateResponseDto(saved, pair);
        } catch (ExchangeRateAlreadyExistsException e) {
            throw new ExchangeRateAlreadyExistsException(dto.baseCurrencyCode(), dto.targetCurrencyCode());
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public ExchangeRateResponseDto update(CurrencyCodesDto pairDto, ExchangeRateUpdateDto rateDto) {
        try {
            CurrencyPair pair = findCurrencyPair(pairDto.baseCurrencyCode(), pairDto.targetCurrencyCode());
            ExchangeRate updated = exchangeRatesDao.update(pair, rateDto.rate());
            return Mapper.toExchangeRateResponseDto(updated, pair);
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
                    CurrencyPair pair = new CurrencyPair(base, target);
                    return Mapper.toExchangeRateResponseDto(rate, pair);
                })
                .toList();
    }

    public ExchangeRateResponseDto findByPair(CurrencyCodesDto dto) {
        try {
            CurrencyPair pair = findCurrencyPair(dto.baseCurrencyCode(), dto.targetCurrencyCode());
            ExchangeRate exchangeRate = exchangeRatesDao.findByCurrencyIds(pair.base().getId(), pair.target().getId());
            return Mapper.toExchangeRateResponseDto(exchangeRate, pair);
        } catch (ExchangeRateNotFoundException e) {
            throw new ExchangeRateNotFoundException(dto.baseCurrencyCode(), dto.targetCurrencyCode());
        }
    }

    public ExchangeRate findEntityByPair(CurrencyPair pair) {
        return exchangeRatesDao.findByCurrencyIds(pair.base().getId(), pair.target().getId());
    }

    public CurrencyPair findCurrencyPair(String baseCode, String targetCode) {
        Currency base = currencyDao.findByCode(baseCode);
        Currency target = currencyDao.findByCode(targetCode);
        return new CurrencyPair(base, target);
    }
}
