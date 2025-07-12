package com.currency_exchange.service;

import com.currency_exchange.dto.currency.CurrencyResponse;
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
        String codeBase = calculationRequest.from();
        String codeTarget = calculationRequest.to();
        BigDecimal amount = calculationRequest.amount();

        CurrencyPair pair = exchangeRateService.findCurrencyPair(codeBase, codeTarget);

        CurrencyResponse baseResponse = Mapper.toCurrencyResponse(pair.base());
        CurrencyResponse targetResponse = Mapper.toCurrencyResponse(pair.target());

        return strategies.stream()
                .filter(strategy -> strategy.canHandle(pair))
                .findFirst()
                .map(strategy -> strategy.calculate(pair, amount, baseResponse, targetResponse))
                .orElseThrow(() -> new ExchangeRateNotFoundException(pair.base().getCode(), pair.target().getCode()));
    }
}
