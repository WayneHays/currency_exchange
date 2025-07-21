package com.currency_exchange.service;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.service.calculation_strategy.CalculationStrategy;
import com.currency_exchange.service.calculation_strategy.CrossRateStrategy;
import com.currency_exchange.service.calculation_strategy.DirectRateStrategy;
import com.currency_exchange.service.calculation_strategy.ReverseRateStrategy;

import java.util.List;

public class CalculationService {
    private static final CalculationService INSTANCE = new CalculationService();
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final List<CalculationStrategy> strategies = List.of(
            new DirectRateStrategy(exchangeRatesDao),
            new ReverseRateStrategy(exchangeRatesDao),
            new CrossRateStrategy(exchangeRatesDao));

    private CalculationService() {
    }

    public static CalculationService getInstance() {
        return INSTANCE;
    }

//    public CalculationResponseDto calculate(CalculationRequestDto calculationRequest) {
//
//        Currency base = currencyDao.findByCode(calculationRequest.from());
//        Currency target = currencyDao.findByCode(calculationRequest.to());
//
//        CurrencyResponseDto baseResponse = Mapper.toCurrencyResponseDto(base);
//        CurrencyResponseDto targetResponse = Mapper.toCurrencyResponseDto(target);
//
//        return strategies.stream()
//                .filter(strategy -> strategy.canHandle(base, target))
//                .findFirst()
//                .map(strategy -> strategy.calculate(base, target, calculationRequest.amount(), baseResponse, targetResponse))
//                .orElseThrow(() -> new ExchangeRateNotFoundException(base.getCode(), target.getCode()));
//    }
}