package com.currency_exchange.service;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dto.currency.CurrencyCreateDto;
import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.util.Mapper;

import java.util.List;

public class CurrencyService extends BaseService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public CurrencyResponseDto save(CurrencyCreateDto dto) {
        return executeDaoOperation(() -> {
            Currency currency = Mapper.toCurrency(dto);
            Currency saved = currencyDao.saveAndSetId(currency);

            return Mapper.toCurrencyResponseDto(saved);
        });
    }

    public List<CurrencyResponseDto> findAll() {
        return executeDaoOperation(() ->
                currencyDao.findAll().stream()
                        .map(Mapper::toCurrencyResponseDto)
                        .toList()
        );
    }

    public CurrencyResponseDto getByCode(String code) {
        return executeDaoOperation(() ->
                currencyDao.findByCode(code)
                        .map(Mapper::toCurrencyResponseDto)
                        .orElseThrow(() -> new CurrencyNotFoundException(code.toUpperCase()))
        );
    }

    public Currency getEntityByCode(String code) {
        return executeDaoOperation(() ->
                currencyDao.findByCode(code)
                        .orElseThrow(() -> new CurrencyNotFoundException(code.toUpperCase()))
        );
    }

    public Currency getEntityById(Long id) {
        return executeDaoOperation(() ->
                currencyDao.findById(id))
                .orElseThrow(() -> new CurrencyNotFoundException(String.valueOf(id)));
    }
}