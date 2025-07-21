package com.currency_exchange.service;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dto.currency.CurrencyRequestDto;
import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.DaoException;
import com.currency_exchange.exception.ServiceException;
import com.currency_exchange.util.Mapper;

import java.util.List;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public CurrencyResponseDto save(CurrencyRequestDto dto) {
        try {
            Currency currency = Mapper.toCurrency(dto);
            Currency saved = currencyDao.save(currency);
            return Mapper.toCurrencyResponseDto(saved);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public CurrencyResponseDto findByCode(String code) {
        try {
            Currency currency = currencyDao.findByCode(code);
            return Mapper.toCurrencyResponseDto(currency);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public List<CurrencyResponseDto> findAll() {
        try {
            return currencyDao.findAll().stream()
                    .map(Mapper::toCurrencyResponseDto)
                    .toList();
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }
}