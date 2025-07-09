package com.currency_exchange.service;

import com.currency_exchange.dto.currency.CurrencyResponse;
import com.currency_exchange.dto.currency.CurrencyResponsePair;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationRequest;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationResponse;
import com.currency_exchange.entity.CurrencyPair;
import com.currency_exchange.exception.service_exception.ExchangeRateNotFoundException;
import com.currency_exchange.service.calculation_strategy.CalculationStrategy;
import com.currency_exchange.service.calculation_strategy.CrossRateStrategy;
import com.currency_exchange.service.calculation_strategy.DirectRateStrategy;
import com.currency_exchange.service.calculation_strategy.ReverseRateStrategy;
import com.currency_exchange.util.Mapper;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeCalculationService {
    private static final ExchangeCalculationService INSTANCE = new ExchangeCalculationService();

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final List<CalculationStrategy> strategies = List.of(
            new DirectRateStrategy(exchangeRateService),
            new ReverseRateStrategy(exchangeRateService),
            new CrossRateStrategy(exchangeRateService));

    private ExchangeCalculationService() {
    }

    public static ExchangeCalculationService getInstance() {
        return INSTANCE;
    }

    public ExchangeCalculationResponse calculate(ExchangeCalculationRequest calculationRequest) {
        CurrencyPair currencyPair = exchangeRateService.findCurrencyPair(calculationRequest);
        CurrencyResponsePair responsePair = mapToResponse(currencyPair);
        return executeCalculationStrategy(currencyPair, calculationRequest.amount(), responsePair);
    }

    private CurrencyResponsePair mapToResponse(CurrencyPair currencyPair) {
        CurrencyResponse baseResponse = Mapper.toCurrencyResponse(currencyPair.base());
        CurrencyResponse targetResponse = Mapper.toCurrencyResponse(currencyPair.target());
        return new CurrencyResponsePair(baseResponse, targetResponse);
    }

    private ExchangeCalculationResponse executeCalculationStrategy(CurrencyPair currencyPair,
                                                                   BigDecimal amount,
                                                                   CurrencyResponsePair responsePair) {
        return strategies.stream()
                .filter(strategy -> strategy.canHandle(currencyPair.base(), currencyPair.target()))
                .findFirst()
                .map(strategy -> strategy.calculate(
                        currencyPair.base(),
                        currencyPair.target(),
                        amount,
                        responsePair.baseResponse(),
                        responsePair.targetResponse()))
                .orElseThrow(() -> new ExchangeRateNotFoundException(
                        currencyPair.base().getCode(),
                        currencyPair.target().getCode()));
    }
}
