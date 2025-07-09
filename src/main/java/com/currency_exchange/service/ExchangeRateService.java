package com.currency_exchange.service;

import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.dto.currency.CurrencyPairRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateCreateRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponse;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateRequest;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.CurrencyPair;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.ExchangeRateConflictException;
import com.currency_exchange.exception.service_exception.ExchangeRateNotFoundException;
import com.currency_exchange.util.Mapper;

import java.util.List;

public class ExchangeRateService extends BaseService {
    public static final String EXCHANGE_RATE_ALREADY_EXISTS = "Exchange rate already exists for pair: %s/%s";
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    private ExchangeRateService() {
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public ExchangeRateResponse save(ExchangeRateCreateRequest dto) {
        return executeDaoOperation(() -> {
            CurrencyPair pair = getCurrencyPair(dto.baseCurrencyCode(), dto.targetCurrencyCode());
            validateRateNotExists(pair);

            return toResponse(
                    exchangeRatesDao.saveAndSetId(Mapper.toExchangeRate(dto, pair.base(), pair.target())),
                    pair
            );
        });
    }

    public List<ExchangeRateResponse> findAll() {
        return executeDaoOperation(() ->
                exchangeRatesDao.findAll().stream()
                        .map(this::toResponseWithCurrencies)
                        .toList()
        );
    }

    public ExchangeRateResponse update(CurrencyPairRequest pairDto, ExchangeRateUpdateRequest rateDto) {
        return executeDaoOperation(() -> {
            CurrencyPair pair = getCurrencyPair(pairDto.baseCurrencyCode(), pairDto.targetCurrencyCode());
            ExchangeRate rate = findRateByPair(pair);

            return toResponse(
                    exchangeRatesDao.update(rate, rateDto.rate())
                            .orElseThrow(() -> new DaoException("Failed to update exchange rate")),
                    pair
            );
        });
    }

    public ExchangeRateResponse findByPair(CurrencyPairRequest dto) {
        return executeDaoOperation(() ->
                toResponse(findRateByPair(getCurrencyPair(dto)), getCurrencyPair(dto))
        );
    }

    public boolean isExchangeRateExists(CurrencyPair pair) {
        return executeDaoOperation(() ->
                exchangeRatesDao.isExchangeRateExists(pair.target().getId(), pair.target().getId())
        );
    }

    public boolean isReversedExchangeRateExists(CurrencyPair pair) {
        return executeDaoOperation(() ->
                exchangeRatesDao.isReversedExchangeRateExists(pair.base(), pair.target())
        );
    }

    public boolean isCrossCourseExists(CurrencyPair pair) {
        return executeDaoOperation(() ->
                exchangeRatesDao.isCrossCourseExists(pair.base(), pair.target())
        );
    }

    private CurrencyPair getCurrencyPair(CurrencyPairRequest dto) {
        return getCurrencyPair(dto.baseCurrencyCode(), dto.targetCurrencyCode());
    }

    private CurrencyPair getCurrencyPair(String baseCode, String targetCode) {
        return new CurrencyPair(
                currencyService.findCurrencyEntityByCode(baseCode),
                currencyService.findCurrencyEntityByCode(targetCode)
        );
    }

    private CurrencyPair getCurrencyPair(Long baseId, Long targetId) {
        return new CurrencyPair(
                currencyService.findCurrencyEntityById(baseId),
                currencyService.findCurrencyEntityById(targetId)
        );
    }

    private ExchangeRate findRateByPair(CurrencyPair pair) {
        return exchangeRatesDao.findByCurrencyIds(pair.base().getId(), pair.target().getId())
                .orElseThrow(() -> new ExchangeRateNotFoundException(pair.base().getCode(), pair.target().getCode()));
    }

    private void validateRateNotExists(CurrencyPair pair) {
        if (isExchangeRateExists(pair)) {
            throw new ExchangeRateConflictException(
                    String.format(EXCHANGE_RATE_ALREADY_EXISTS, pair.base().getCode(), pair.target().getCode())
            );
        }
    }

    private ExchangeRateResponse toResponse(ExchangeRate rate, CurrencyPair pair) {
        return Mapper.toExchangeRateResponse(rate, pair.base(), pair.target());
    }

    private ExchangeRateResponse toResponseWithCurrencies(ExchangeRate rate) {
        return toResponse(rate, getCurrencyPair(rate.getBaseCurrencyId(), rate.getTargetCurrencyId()));
    }

    public ExchangeRate findByUsd(Currency currency) {
        return executeDaoOperation(() ->
                exchangeRatesDao.findByUsd(currency)
                        .orElseThrow(() -> new CurrencyNotFoundException(currency.getCode()))
        );
    }

    public ExchangeRate findEntityByPair(Currency base, Currency target) {
        return executeDaoOperation(() -> findRateByPair(new CurrencyPair(base, target)));
    }
}
