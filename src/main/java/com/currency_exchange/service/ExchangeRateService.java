package com.currency_exchange.service;

import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.dto.currency.CurrencyCodesRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateCreateRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponse;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateRequest;
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

    public ExchangeRateResponse save(ExchangeRateCreateRequest dto) throws CurrencyNotFoundException {
        return executeDaoOperation(() -> {
            CurrencyPair pair = findCurrencyPair(dto.baseCurrencyCode(), dto.targetCurrencyCode());

            checkIfExchangeRateExists(pair);

            ExchangeRate exchangeRate = Mapper.toExchangeRate(dto, pair);
            ExchangeRate saved = exchangeRatesDao.saveAndSetId(exchangeRate);

            return Mapper.toExchangeRateResponse(saved, pair);
        });
    }

    public List<ExchangeRateResponse> findAll() {
        return executeDaoOperation(() ->
                exchangeRatesDao.findAll().stream()
                        .map(exchangeRate -> {
                            Currency base = currencyService.getEntityById(exchangeRate.getBaseCurrencyId());
                            Currency target = currencyService.getEntityById(exchangeRate.getTargetCurrencyId());
                            CurrencyPair pair = new CurrencyPair(base, target);
                            return Mapper.toExchangeRateResponse(exchangeRate, pair);
                        })
                        .toList());
    }

    public ExchangeRateResponse update(CurrencyCodesRequest pairDto, ExchangeRateUpdateRequest rateDto) throws CurrencyNotFoundException, ExchangeRateNotFoundException {
        return executeDaoOperation(() -> {
            CurrencyPair pair = findCurrencyPair(pairDto.baseCurrencyCode(), pairDto.targetCurrencyCode());

            ExchangeRate toUpdate = exchangeRatesDao.findByCurrencyIds(pair.base().getId(), pair.target().getId())
                    .orElseThrow(() -> new ExchangeRateNotFoundException(pairDto.baseCurrencyCode(), pairDto.targetCurrencyCode()));
            ExchangeRate updated = exchangeRatesDao.update(toUpdate, rateDto.rate())
                    .orElseThrow(() -> new DaoException("Failed to update exchange rate"));

            return Mapper.toExchangeRateResponse(updated, pair);
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

    public ExchangeRateResponse findByPair(CurrencyCodesRequest dto) {
        return executeDaoOperation(() -> {
            CurrencyPair pair = findCurrencyPair(dto.baseCurrencyCode(), dto.targetCurrencyCode());
            ExchangeRate exchangeRate = exchangeRatesDao.findByCurrencyIds(pair.base().getId(), pair.target().getId())
                    .orElseThrow(() -> new ExchangeRateNotFoundException(pair.base().getCode(), pair.target().getCode()));

            return Mapper.toExchangeRateResponse(exchangeRate, pair);
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
