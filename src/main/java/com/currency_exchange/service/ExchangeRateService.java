package com.currency_exchange.service;

import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.dto.request.ExchangeRateRequest;
import com.currency_exchange.dto.response.CurrencyResponse;
import com.currency_exchange.dto.response.ExchangeRateResponse;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.DatabaseAccessException;
import com.currency_exchange.exception.dao_exception.ExchangeRateAlreadyExistsException;
import com.currency_exchange.exception.service_exception.*;
import com.currency_exchange.util.Mapper;

import java.util.List;

public class ExchangeRateService {
    private static final String SERVICE_ERROR_MSG = "ExchangeRate service error";
    public static final String EXCHANGE_RATE_ALREADY_EXISTS = "Exchange rate already exists for pair: %s/%s";
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();

    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    private ExchangeRateService() {
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRateResponse> findAll() {
        try {
            return exchangeRatesDao.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (DatabaseAccessException e) {
            throw new ServiceUnavailableException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public ExchangeRateResponse findByCurrencyCodes(String baseCode, String targetCode) {
        try {
            CurrencyDtoPair pair = findCurrencyPair(baseCode, targetCode);
            ExchangeRate exchangeRate = findExchangeRate(pair);
            return Mapper.mapToExchangeRateDtoResponse(exchangeRate, pair.base, pair.target);
        } catch (DatabaseAccessException e) {
            throw new ServiceUnavailableException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public ExchangeRateResponse save(ExchangeRateRequest dto) throws CurrencyNotFoundException {
        try {
            CurrencyDtoPair pair = findCurrencyPair(dto.getBaseCurrencyCode(), dto.getTargetCurrencyCode());
            ExchangeRate exchangeRate = Mapper.mapToExchangeRate(dto, pair.base.getId(), pair.target.getId());
            ExchangeRate saved = exchangeRatesDao.save(exchangeRate);
            return Mapper.mapToExchangeRateDtoResponse(saved, pair.base, pair.target);
        } catch (ExchangeRateAlreadyExistsException e) {
            throw new ExchangeRateConflictException(buildErrorMessage(dto, e));
        } catch (DatabaseAccessException e) {
            throw new ServiceUnavailableException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException(SERVICE_ERROR_MSG);
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

    private String buildErrorMessage(ExchangeRateRequest dto, ExchangeRateAlreadyExistsException e) {
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
