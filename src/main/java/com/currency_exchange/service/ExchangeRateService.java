package com.currency_exchange.service;

import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.dto.currency.CurrencyPairRequest;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateCreateRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponse;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateRequest;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.CurrencyPair;
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

    public ExchangeRateResponse save(ExchangeRateCreateRequest dto) throws CurrencyNotFoundException {
        try {
            CurrencyPair pair = findCurrencyPairByCodes(dto.baseCurrencyCode(), dto.targetCurrencyCode());

            checkIfExchangeRateExists(pair.base(), pair.target());

            ExchangeRate exchangeRate = Mapper.toExchangeRate(dto, pair.base(), pair.target());
            ExchangeRate saved = exchangeRatesDao.saveAndSetId(exchangeRate);

            return Mapper.toExchangeRateResponse(saved, pair.base(), pair.target());
        } catch (ExchangeRateAlreadyExistsException e) {
            throw new ExchangeRateConflictException(buildErrorMessage(dto));
        } catch (DatabaseAccessException e) {
            throw new ServiceException(SERVICE_ERROR.formatted(e.getMessage()));
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private void checkIfExchangeRateExists(Currency baseCurrency, Currency targetCurrency) {
        if (exchangeRatesDao.isExchangeRateExists(baseCurrency.getId(), targetCurrency.getId())) {
            throw new ExchangeRateConflictException(EXCHANGE_RATE_ALREADY_EXISTS.formatted(baseCurrency.getCode(), targetCurrency.getCode()));
        }
    }

    public List<ExchangeRateResponse> findAll() {
        try {
            List<ExchangeRateResponse> result = new ArrayList<>();
            List<ExchangeRate> exchangeRates = exchangeRatesDao.findAll();

            for (ExchangeRate exchangeRate : exchangeRates) {
                CurrencyPair pair = findCurrencyPairByIds(exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId());
                result.add(Mapper.toExchangeRateResponse(exchangeRate, pair.base(), pair.target()));
            }
            return result;
        } catch (DatabaseAccessException e) {
            throw new ServiceException(SERVICE_ERROR.formatted(e.getMessage()));
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public ExchangeRateResponse update(CurrencyPairRequest pairDto, ExchangeRateUpdateRequest rateDto) throws CurrencyNotFoundException, ExchangeRateNotFoundException {
        try {
            CurrencyPair pair = findCurrencyPairByCodes(pairDto.baseCurrencyCode(), pairDto.targetCurrencyCode());

            ExchangeRate toUpdate = exchangeRatesDao.findByCurrencyIds(pair.base().getId(), pair.target().getId())
                    .orElseThrow(() -> new ExchangeRateNotFoundException(pairDto.baseCurrencyCode(), pairDto.targetCurrencyCode()));
            ExchangeRate updated = exchangeRatesDao.update(toUpdate, rateDto.rate())
                    .orElseThrow(() -> new DaoException("Failed to update exchange rate"));

            return Mapper.toExchangeRateResponse(updated, pair.base(), pair.target());
        } catch (DatabaseAccessException e) {
            throw new ServiceException(SERVICE_ERROR.formatted(e.getMessage()));
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public boolean isExchangeRateExists(Currency base, Currency target) {
        return exchangeRatesDao.isExchangeRateExists(base.getId(), target.getId());
    }

    public boolean isReversedExchangeRateExists(Currency base, Currency target) {
        return exchangeRatesDao.isReversedExchangeRateExists(base, target);
    }

    public boolean isCrossCourseExists(Currency base, Currency target) {
        return exchangeRatesDao.isCrossCourseExists(base, target);
    }

    public ExchangeRateResponse findByPair(CurrencyPairRequest dto) {
        try {
            CurrencyPair pair = findCurrencyPairByCodes(dto.baseCurrencyCode(), dto.targetCurrencyCode());
            ExchangeRate exchangeRate = exchangeRatesDao.findByCurrencyIds(pair.target().getId(), pair.target().getId())
                    .orElseThrow(() -> new ExchangeRateNotFoundException(pair.target().getCode(), pair.target().getCode()));

            return Mapper.toExchangeRateResponse(exchangeRate, pair.base(), pair.target());
        } catch (DatabaseAccessException e) {
            throw new ServiceException(SERVICE_ERROR.formatted(e.getMessage()));
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public ExchangeRate findByUsd(Currency currency) {
        return exchangeRatesDao.findByUsd(currency)
                .orElseThrow(() -> new CurrencyNotFoundException(currency.getCode()));
    }

    public ExchangeRate findEntityByPair(Currency base, Currency target) {
        return exchangeRatesDao.findByCurrencyIds(base.getId(), target.getId())
                .orElseThrow(() -> new ExchangeRateNotFoundException(base.getCode(), target.getCode()));
    }

    public CurrencyPair findCurrencyPair(ExchangeCalculationRequest request) {
        Currency base = currencyService.findCurrencyEntityByCode(request.from());
        Currency target = currencyService.findCurrencyEntityByCode(request.to());
        return new CurrencyPair(base, target);
    }

    public CurrencyPair findCurrencyPairByCodes(String baseCode, String targetCode) {
        Currency base = currencyService.findCurrencyEntityByCode(baseCode);
        Currency target = currencyService.findCurrencyEntityByCode(targetCode);
        return new CurrencyPair(base, target);
    }

    public CurrencyPair findCurrencyPairByIds(Long baseId, Long targetId) {
        Currency base = currencyService.findCurrencyEntityById(baseId);
        Currency target = currencyService.findCurrencyEntityById(targetId);
        return new CurrencyPair(base, target);
    }

    private String buildErrorMessage(ExchangeRateCreateRequest dto) {
        return EXCHANGE_RATE_ALREADY_EXISTS
                .formatted(dto.baseCurrencyCode(), dto.targetCurrencyCode());
    }
}
