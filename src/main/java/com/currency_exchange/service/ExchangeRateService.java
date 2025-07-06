package com.currency_exchange.service;

import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.dto.currency.CurrencyPairDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateCreateRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponse;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateRequest;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.DatabaseAccessException;
import com.currency_exchange.exception.dao_exception.ExchangeRateAlreadyExistsException;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.ExchangeRateConflictException;
import com.currency_exchange.exception.service_exception.ExchangeRateNotFoundException;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.util.Mapper;

import java.util.ArrayList;
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

    public ExchangeRateResponse save(ExchangeRateCreateRequest dto) throws CurrencyNotFoundException {
        try {
            Currency base = currencyService.findCurrencyEntityByCode(dto.baseCurrencyCode());
            Currency target = currencyService.findCurrencyEntityByCode(dto.targetCurrencyCode());

            checkIfExchangeRateAlreadyExists(base, target);

            ExchangeRate exchangeRate = Mapper.mapToExchangeRate(dto, base, target);
            ExchangeRate saved = exchangeRatesDao.saveAndSetId(exchangeRate);

            return Mapper.mapToExchangeRateResponse(saved, base, target);
        } catch (ExchangeRateAlreadyExistsException e) {
            throw new ExchangeRateConflictException(buildErrorMessage(dto));
        } catch (DatabaseAccessException e) {
            throw new ServiceException(EXCHANGE_RATE_SERVICE_ERROR, e);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private void checkIfExchangeRateAlreadyExists(Currency baseCurrency, Currency targetCurrency) {
        if (exchangeRatesDao.existsByPair(baseCurrency.getId(), targetCurrency.getId())) {
            throw new ExchangeRateConflictException(EXCHANGE_RATE_ALREADY_EXISTS.formatted(baseCurrency.getCode(), targetCurrency.getCode()));
        }
    }

    public List<ExchangeRateResponse> findAll() {
        try {
            List<ExchangeRateResponse> result = new ArrayList<>();
            List<ExchangeRate> exchangeRates = exchangeRatesDao.findAll();

            for (ExchangeRate exchangeRate : exchangeRates) {
                Currency base = currencyService.findById(exchangeRate.getBaseCurrencyId());
                Currency target = currencyService.findById(exchangeRate.getTargetCurrencyId());
                result.add(Mapper.mapToExchangeRateResponse(exchangeRate, base, target));
            }
            return result;
        } catch (DatabaseAccessException e) {
            throw new ServiceException(EXCHANGE_RATE_SERVICE_ERROR, e);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public ExchangeRateResponse update(CurrencyPairDto pairDto, ExchangeRateUpdateRequest rateDto) throws CurrencyNotFoundException, ExchangeRateNotFoundException {
        try {
            Currency base = currencyService.findCurrencyEntityByCode(pairDto.baseCurrencyCode());
            Currency target = currencyService.findCurrencyEntityByCode(pairDto.targetCurrencyCode());

            ExchangeRate toUpdate = exchangeRatesDao.findByCurrencyIds(base.getId(), target.getId())
                    .orElseThrow(() -> new ExchangeRateNotFoundException(pairDto.baseCurrencyCode(), pairDto.targetCurrencyCode()));
            ExchangeRate updated = exchangeRatesDao.update(toUpdate, rateDto.rate())
                    .orElseThrow(() -> new DaoException("Failed to update exchange rate"));

            return Mapper.mapToExchangeRateResponse(updated, base, target);
        } catch (DatabaseAccessException e) {
            throw new ServiceException(EXCHANGE_RATE_SERVICE_ERROR, e);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public ExchangeRateResponse findByPair(CurrencyPairDto dto) {
        try {
            Currency base = currencyService.findCurrencyEntityByCode(dto.baseCurrencyCode());
            Currency target = currencyService.findCurrencyEntityByCode(dto.targetCurrencyCode());

            ExchangeRate exchangeRate = exchangeRatesDao.findByCurrencyIds(base.getId(), target.getId())
                    .orElseThrow(() -> new ExchangeRateNotFoundException(base.getCode(), target.getCode()));

            return Mapper.mapToExchangeRateResponse(exchangeRate, base, target);
        } catch (DatabaseAccessException e) {
            throw new ServiceException(EXCHANGE_RATE_SERVICE_ERROR, e);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private String buildErrorMessage(ExchangeRateCreateRequest dto) {
        return EXCHANGE_RATE_ALREADY_EXISTS
                .formatted(dto.baseCurrencyCode(), dto.targetCurrencyCode());
    }
}
