package com.currency_exchange.service;

import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.dto.request.ExchangeRateRequest;
import com.currency_exchange.dto.response.CurrencyResponse;
import com.currency_exchange.dto.response.ExchangeRateResponse;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.DatabaseAccessException;
import com.currency_exchange.exception.dao_exception.ExchangeRateAlreadyExistsException;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.ExchangeRateConflictException;
import com.currency_exchange.exception.service_exception.ExchangeRateNotFoundException;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.util.Mapper;

import java.util.List;

public class ExchangeRateService {
    public static final String EXCHANGE_RATE_ALREADY_EXISTS = "Exchange rate already exists for pair: %s/%s";
    public static final String EXCHANGE_RATE_SERVICE_ERROR = "ExchangeRate service temporarily unavailable";

    private static final ExchangeRateService INSTANCE = new ExchangeRateService();

    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    private ExchangeRateService() {
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public ExchangeRateResponse save(ExchangeRateRequest dto) throws CurrencyNotFoundException {
        try {
            CurrencyDtoPair pair = findCurrencyPair(dto.getBaseCurrencyCode(), dto.getTargetCurrencyCode());
            ExchangeRate exchangeRate = Mapper.mapToExchangeRate(dto, pair.base.getId(), pair.target.getId());
            ExchangeRate saved = exchangeRatesDao.saveAndSetId(exchangeRate);
            return Mapper.mapToExchangeRateDtoResponse(saved, pair.base, pair.target);
        } catch (ExchangeRateAlreadyExistsException e) {
            throw new ExchangeRateConflictException(buildErrorMessage(dto));
        } catch (DatabaseAccessException e) {
            throw new ServiceException(EXCHANGE_RATE_SERVICE_ERROR, e);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public ExchangeRateResponse update(ExchangeRateRequest dto) throws ExchangeRateNotFoundException {
        try {
            CurrencyDtoPair pair = findCurrencyPair(dto.getBaseCurrencyCode(), dto.getTargetCurrencyCode());
            ExchangeRate exchangeRate = findExchangeRate(pair);
            ExchangeRate updated = exchangeRatesDao.update(exchangeRate, dto.getRate())
                    .orElseThrow(() -> new ExchangeRateNotFoundException(pair.base.getCode(), pair.target.getCode()));
            return Mapper.mapToExchangeRateDtoResponse(updated, pair.base, pair.target);
        } catch (DatabaseAccessException e) {
            throw new ServiceException(EXCHANGE_RATE_SERVICE_ERROR, e);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public List<ExchangeRateResponse> findAll() {
        try {
            return exchangeRatesDao.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (DatabaseAccessException e) {
            throw new ServiceException(EXCHANGE_RATE_SERVICE_ERROR, e);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public ExchangeRateResponse findByCurrencyCodes(String[] currencyCodes) {
        try {
            String baseCurrencyCode = currencyCodes[0];
            String targetCurrencyCode = currencyCodes[1];
            CurrencyDtoPair pair = findCurrencyPair(baseCurrencyCode, targetCurrencyCode);
            ExchangeRate exchangeRate = findExchangeRate(pair);
            return Mapper.mapToExchangeRateDtoResponse(exchangeRate, pair.base, pair.target);
        } catch (DatabaseAccessException e) {
            throw new ServiceException(EXCHANGE_RATE_SERVICE_ERROR, e);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private ExchangeRateResponse convertToDto(ExchangeRate exchangeRate) {
        CurrencyDtoPair pair = findCurrencyPair(
                exchangeRate.getBaseCurrencyId(),
                exchangeRate.getTargetCurrencyId()
        );
        return Mapper.mapToExchangeRateDtoResponse(exchangeRate, pair.base, pair.target);
    }

    private CurrencyDtoPair findCurrencyPair(String baseCode, String targetCode) {
        return new CurrencyDtoPair(
                currencyService.findByCode(baseCode),
                currencyService.findByCode(targetCode)
        );
    }

    private CurrencyDtoPair findCurrencyPair(Long baseId, Long targetId) {
        return new CurrencyDtoPair(
                currencyService.findById(baseId),
                currencyService.findById(targetId)
        );
    }

    private String buildErrorMessage(ExchangeRateRequest dto) {
        return EXCHANGE_RATE_ALREADY_EXISTS
                .formatted(dto.getBaseCurrencyCode(), dto.getTargetCurrencyCode());
    }

    private ExchangeRate findExchangeRate(CurrencyDtoPair pair) {
        return exchangeRatesDao.findByCurrencyIds(
                pair.base.getId(),
                pair.target.getId()
        ).orElseThrow(() -> new ExchangeRateNotFoundException(
                pair.base.getCode(),
                pair.target.getCode())
        );
    }

    private record CurrencyDtoPair(CurrencyResponse base, CurrencyResponse target) {
    }
}
