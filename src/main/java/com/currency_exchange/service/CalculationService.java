package com.currency_exchange.service;

import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.dto.exchange_calculation.CalculationRequestDto;
import com.currency_exchange.dto.exchange_calculation.CalculationResponseDto;
import com.currency_exchange.entity.CurrencyPair;
import com.currency_exchange.exception.dao_exception.ExchangeRateNotFoundException;
import com.currency_exchange.service.calculation_strategy.CalculationStrategy;
import com.currency_exchange.service.calculation_strategy.CrossRateStrategy;
import com.currency_exchange.service.calculation_strategy.DirectRateStrategy;
import com.currency_exchange.service.calculation_strategy.ReverseRateStrategy;
import com.currency_exchange.util.Mapper;

import java.math.BigDecimal;
import java.util.List;

public class CalculationService {
    private static final CalculationService INSTANCE = new CalculationService();
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final List<CalculationStrategy> strategies = List.of(
            new DirectRateStrategy(exchangeRateService),
            new ReverseRateStrategy(exchangeRateService),
            new CrossRateStrategy(exchangeRateService));

    private CalculationService() {
    }

    public static CalculationService getInstance() {
        return INSTANCE;
    }

    public CalculationResponseDto calculate(CalculationRequestDto calculationRequest) {
        String codeBase = calculationRequest.from();
        String codeTarget = calculationRequest.to();
        BigDecimal amount = calculationRequest.amount();

        CurrencyPair pair = exchangeRateService.findCurrencyPair(codeBase, codeTarget);

        CurrencyResponseDto baseResponse = Mapper.toCurrencyResponseDto(pair.base());
        CurrencyResponseDto targetResponse = Mapper.toCurrencyResponseDto(pair.target());

        return strategies.stream()
                .filter(strategy -> strategy.canHandle(pair))
                .findFirst()
                .map(strategy -> strategy.calculate(pair, amount, baseResponse, targetResponse))
                .orElseThrow(() -> new ExchangeRateNotFoundException(pair.base().getCode(), pair.target().getCode()));
    }
}
