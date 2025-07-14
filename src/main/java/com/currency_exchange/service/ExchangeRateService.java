package com.currency_exchange.service;

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
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.ExchangeRateNotFoundException;
import com.currency_exchange.util.Mapper;

import java.util.List;

public class ExchangeRateService extends BaseService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();

    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    private ExchangeRateService() {
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public ExchangeRateResponseDto save(ExchangeRateCreateDto dto) throws CurrencyNotFoundException {
        return executeDaoOperation(() -> {
            CurrencyPair pair = findCurrencyPair(dto.baseCurrencyCode(), dto.targetCurrencyCode());

            checkIfExchangeRateExists(pair);

            ExchangeRate exchangeRate = Mapper.toExchangeRate(dto, pair);
            ExchangeRate saved = exchangeRatesDao.saveAndSetId(exchangeRate);

            return Mapper.toExchangeRateResponseDto(saved, pair);
        });
    }

    public List<ExchangeRateResponseDto> findAll() {
        return executeDaoOperation(() ->
                exchangeRatesDao.findAll().stream()
                        .map(exchangeRate -> {
                            Currency base = currencyService.getEntityById(exchangeRate.getBaseCurrencyId());
                            Currency target = currencyService.getEntityById(exchangeRate.getTargetCurrencyId());
                            CurrencyPair pair = new CurrencyPair(base, target);
                            return Mapper.toExchangeRateResponseDto(exchangeRate, pair);
                        })
                        .toList());
    }

    public ExchangeRateResponseDto update(
            CurrencyCodesDto pairDto,
            ExchangeRateUpdateDto rateDto) throws CurrencyNotFoundException, ExchangeRateNotFoundException {

        return executeDaoOperation(() -> {
            CurrencyPair pair = findCurrencyPair(pairDto.baseCurrencyCode(), pairDto.targetCurrencyCode());

            ExchangeRate toUpdate = exchangeRatesDao.findByCurrencyIds(pair.base().getId(), pair.target().getId())
                    .orElseThrow(() -> new ExchangeRateNotFoundException(pairDto.baseCurrencyCode(), pairDto.targetCurrencyCode()));
            ExchangeRate updated = exchangeRatesDao.update(toUpdate, rateDto.rate())
                    .orElseThrow(() -> new DaoException("Failed to update exchange rate"));

            return Mapper.toExchangeRateResponseDto(updated, pair);
        });
    }

    public boolean isExchangeRateExists(CurrencyPair pair) {
        return exchangeRatesDao.isExchangeRateExists(pair);
    }

    public boolean isReversedExchangeRateExists(CurrencyPair pair) {
        return exchangeRatesDao.isReversedExchangeRateExists(pair);
    }

    public boolean isCrossCourseExists(CurrencyPair pair) {
        return exchangeRatesDao.isCrossCourseExists(pair);
    }

    public ExchangeRateResponseDto findByPair(CurrencyCodesDto dto) {
        return executeDaoOperation(() -> {
            CurrencyPair pair = findCurrencyPair(dto.baseCurrencyCode(), dto.targetCurrencyCode());
            ExchangeRate exchangeRate = exchangeRatesDao.findByCurrencyIds(pair.base().getId(), pair.target().getId())
                    .orElseThrow(() -> new ExchangeRateNotFoundException(pair.base().getCode(), pair.target().getCode()));

            return Mapper.toExchangeRateResponseDto(exchangeRate, pair);
        });
    }

    public ExchangeRate findByUsd(Currency currency) {
        return exchangeRatesDao.findByUsd(currency)
                .orElseThrow(() -> new CurrencyNotFoundException(currency.getCode()));
    }

    public ExchangeRate findEntityByPair(CurrencyPair pair) {
        return exchangeRatesDao.findByCurrencyIds(pair.base().getId(), pair.target().getId())
                .orElseThrow(() -> new ExchangeRateNotFoundException(pair.base().getCode(), pair.target().getCode()));
    }

    public CurrencyPair findCurrencyPair(String baseCode, String targetCode) {
        Currency base = currencyService.getEntityByCode(baseCode);
        Currency target = currencyService.getEntityByCode(targetCode);

        return new CurrencyPair(base, target);
    }

    private void checkIfExchangeRateExists(CurrencyPair pair) {
        if (exchangeRatesDao.isExchangeRateExists(pair)) {
            throw new ExchangeRateAlreadyExistsException("%s -> %s".formatted(pair.base().getCode(), pair.target().getCode()));
        }
    }
}
