package com.currency_exchange.service;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dto.currency.CurrencyRequestDto;
import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.CurrencyAlreadyExistsException;
import com.currency_exchange.exception.CurrencyNotFoundException;
import com.currency_exchange.exception.DaoException;
import com.currency_exchange.exception.ServiceException;
import com.currency_exchange.util.Mapper;

import java.util.List;

public class CurrencyService {
    public static final String FAILED_TO_SAVE_MESSAGE = "Failed to save currency with code [%s]";
    public static final String FAILED_TO_FIND_BY_CODE_MESSAGE = "Failed to find currency with code [%s]";
    public static final String FAILED_TO_FIND_ALL_MESSAGE = "Failed to find all currencies";

    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    public CurrencyResponseDto save(CurrencyRequestDto dto) {
        try {
            Currency currency = Mapper.toCurrency(dto);
            Currency saved = currencyDao.save(currency);
            return Mapper.toCurrencyResponseDto(saved);
        } catch (CurrencyAlreadyExistsException e) {
            throw e;
        } catch (DaoException e) {
            throw new ServiceException(FAILED_TO_SAVE_MESSAGE.formatted(dto.code()), e);
        }
    }

    public CurrencyResponseDto findByCode(String code) {
        try {
            Currency currency = currencyDao.findByCode(code);
            return Mapper.toCurrencyResponseDto(currency);
        } catch (CurrencyNotFoundException e) {
            throw e;
        } catch (DaoException e) {
            throw new ServiceException(FAILED_TO_FIND_BY_CODE_MESSAGE.formatted(code), e);
        }
    }

    public List<CurrencyResponseDto> findAll() {
        try {
            return currencyDao.findAll().stream()
                    .map(Mapper::toCurrencyResponseDto)
                    .toList();
        } catch (DaoException e) {
            throw new ServiceException(FAILED_TO_FIND_ALL_MESSAGE, e);
        }
    }
}