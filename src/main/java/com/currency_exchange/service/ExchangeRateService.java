package com.currency_exchange.service;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.dto.request.ExchangeRateDtoRequest;
import com.currency_exchange.dto.response.CurrencyDtoResponse;
import com.currency_exchange.dto.response.ExchangeRateDtoResponse;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.DatabaseAccessException;
import com.currency_exchange.exception.dao_exception.ExchangeRateAlreadyExistsException;
import com.currency_exchange.exception.service_exception.*;
import com.currency_exchange.util.Mapper;

import java.util.ArrayList;
import java.util.List;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private ExchangeRateService() {
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRateDtoResponse> findAll() {
        List<ExchangeRateDtoResponse> result = new ArrayList<>();
        try {
            List<ExchangeRate> exchangeRates = exchangeRatesDao.findAll();
            for (ExchangeRate exchangeRate : exchangeRates) {
                Long baseCurrencyId = exchangeRate.getBaseCurrencyId();
                Long targetCurrencyId = exchangeRate.getTargetCurrencyId();
                Currency baseCurrency = currencyDao.findById(baseCurrencyId).orElseThrow(() -> new CurrencyNotFoundException("%d".formatted(baseCurrencyId)));
                Currency targetCurrency = currencyDao.findById(targetCurrencyId).orElseThrow(() -> new CurrencyNotFoundException("%d".formatted(targetCurrencyId)));
                CurrencyDtoResponse baseCurrencyDtoResponse = Mapper.mapToCurrencyDtoResponse(baseCurrency);
                CurrencyDtoResponse targetCurrencyDtoResponse = Mapper.mapToCurrencyDtoResponse(targetCurrency);
                result.add(new ExchangeRateDtoResponse(exchangeRate.getId(), baseCurrencyDtoResponse, targetCurrencyDtoResponse, exchangeRate.getRate()));
            }
            return result;
        } catch (DatabaseAccessException e) {
            throw new ServiceUnavailableException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException("ExchangeRate service error");
        }
    }

    public ExchangeRateDtoResponse findByPair(String pair) {
        String baseCode = pair.substring(0, 3);
        String targetCode = pair.substring(3, 6);

        Currency base = currencyDao.findByCode(baseCode).orElseThrow(() -> (new CurrencyNotFoundException(baseCode)));
        Currency target = currencyDao.findByCode(targetCode).orElseThrow(() -> new CurrencyNotFoundException(targetCode));

        CurrencyDtoResponse baseCurrencyDtoResponse = Mapper.mapToCurrencyDtoResponse(base);
        CurrencyDtoResponse targetCurrencyDtoResponse = Mapper.mapToCurrencyDtoResponse(target);

        ExchangeRate exchangeRate = exchangeRatesDao.findByPair(base.getId(), target.getId()).orElseThrow(() -> new ExchangeRateNotFoundException(pair));

        return Mapper.mapToExchangeRateDtoResponse(exchangeRate, baseCurrencyDtoResponse, targetCurrencyDtoResponse);
    }

    public ExchangeRateDtoResponse save(ExchangeRateDtoRequest dtoRequest) throws CurrencyNotFoundException {
        try {
            String baseCurrencyCode = dtoRequest.getBaseCurrencyCode();
            String targetCurrencyCode = dtoRequest.getTargetCurrencyCode();

            Currency base = currencyDao.findByCode(baseCurrencyCode).orElseThrow(() -> new CurrencyNotFoundException(baseCurrencyCode));
            Currency target = currencyDao.findByCode(targetCurrencyCode).orElseThrow(() -> new CurrencyNotFoundException(targetCurrencyCode));

            CurrencyDtoResponse baseCurrencyResponse = Mapper.mapToCurrencyDtoResponse(base);
            CurrencyDtoResponse targetCurrencyResponse = Mapper.mapToCurrencyDtoResponse(target);

            ExchangeRate exchangeRate = Mapper.mapToExchangeRate(dtoRequest, base.getId(), target.getId());
            ExchangeRate saved = exchangeRatesDao.save(exchangeRate);

            return Mapper.mapToExchangeRateDtoResponse(saved, baseCurrencyResponse, targetCurrencyResponse);
        } catch (ExchangeRateAlreadyExistsException e) {
            String message = e.getMessage();
            String newMessage = message.formatted(dtoRequest.getBaseCurrencyCode(), dtoRequest.getTargetCurrencyCode());
            throw new ExchangeRateConflictException(newMessage);
        } catch (DatabaseAccessException e) {
            throw new ServiceUnavailableException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException("Currency service error");
        }
    }
}
