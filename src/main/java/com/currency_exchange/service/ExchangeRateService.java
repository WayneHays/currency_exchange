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

import java.util.LinkedList;
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
        List<ExchangeRateDtoResponse> result = new LinkedList<>();
        try {
            List<ExchangeRate> exchangeRates = exchangeRatesDao.findAll();
            for (ExchangeRate exchangeRate : exchangeRates) {
                Currency[] currencies = findPairByIds(exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId());
                ExchangeRateDtoResponse dtoResponse = createDto(exchangeRate, currencies);
                result.add(dtoResponse);
            }
            return result;
        } catch (DatabaseAccessException e) {
            throw new ServiceUnavailableException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public ExchangeRateDtoResponse findByCurrencyCodes(String baseCode, String targetCode) {
        try {
            Currency[] currencies = findPairByCodes(baseCode, targetCode);
            CurrencyDtoResponse[] dtoResponses = convertToDtoResponse(currencies);
            ExchangeRate exchangeRate = findByCurrencyIds(currencies);
            return Mapper.mapToExchangeRateDtoResponse(exchangeRate, dtoResponses);
        } catch (DatabaseAccessException e) {
            throw new ServiceUnavailableException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private CurrencyDtoResponse[] convertToDtoResponse(Currency[] currencies) {
        CurrencyDtoResponse baseDto = Mapper.mapToCurrencyDtoResponse(currencies[0]);
        CurrencyDtoResponse targetDto = Mapper.mapToCurrencyDtoResponse(currencies[1]);
        return new CurrencyDtoResponse[]{baseDto, targetDto};
    }

    private Currency[] findPairByCodes(String baseCode, String targetCode) {
        Currency base = findCurrencyByCode(baseCode);
        Currency target = findCurrencyByCode(targetCode);
        return new Currency[]{base, target};
    }

    private Currency[] findPairByIds(Long baseId, Long targetId) {
        Currency base = findCurrencyById(baseId);
        Currency target = findCurrencyById(targetId);
        return new Currency[]{base, target};
    }

    public ExchangeRateDtoResponse save(ExchangeRateDtoRequest dto) throws CurrencyNotFoundException {
        try {
            String baseCode = dto.getBaseCurrencyCode();
            String targetCode = dto.getTargetCurrencyCode();
            Currency base = findCurrencyByCode(baseCode);
            Currency target = findCurrencyByCode(targetCode);
            CurrencyDtoResponse baseDto = Mapper.mapToCurrencyDtoResponse(base);
            CurrencyDtoResponse targetDto = Mapper.mapToCurrencyDtoResponse(target);
            ExchangeRate exchangeRate = Mapper.mapToExchangeRate(dto, base.getId(), target.getId());
            ExchangeRate saved = exchangeRatesDao.save(exchangeRate);
            return Mapper.mapToExchangeRateDtoResponse(saved, baseDto, targetDto);
        } catch (ExchangeRateAlreadyExistsException e) {
            String newMessage = getErrorMessage(dto, e);
            throw new ExchangeRateConflictException(newMessage);
        } catch (DatabaseAccessException e) {
            throw new ServiceUnavailableException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException("Currency service error");
        }
    }

    private String getErrorMessage(ExchangeRateDtoRequest dto, ExchangeRateAlreadyExistsException e) {
        String message = e.getMessage();
        return message.formatted(dto.getBaseCurrencyCode(), dto.getTargetCurrencyCode());
    }

    private ExchangeRate findByCurrencyIds(Currency[] currencies) {
        Currency base = currencies[0];
        Currency target = currencies[1];
        return exchangeRatesDao.findByCurrencyIds(base.getId(), target.getId())
                .orElseThrow(() -> new ExchangeRateNotFoundException(base.getCode(), target.getCode()));
    }

    private ExchangeRateDtoResponse createDto(ExchangeRate exchangeRate, Currency[] currencies) {
        Currency base = currencies[0];
        Currency target = currencies[1];
        return new ExchangeRateDtoResponse(exchangeRate.getId(),
                Mapper.mapToCurrencyDtoResponse(base),
                Mapper.mapToCurrencyDtoResponse(target),
                exchangeRate.getRate());
    }

    private Currency findCurrencyByCode(String code) {
        return currencyDao.findByCode(code)
                .orElseThrow(() -> new CurrencyNotFoundException("%s".formatted(code)));
    }

    private Currency findCurrencyById(Long id) {
        return currencyDao.findById(id)
                .orElseThrow(() -> new CurrencyNotFoundException("%d".formatted(id)));
    }

    private record CurrencyPair(Currency base, Currency target) {
    }
}
