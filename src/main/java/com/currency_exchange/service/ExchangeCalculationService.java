package com.currency_exchange.service;

import com.currency_exchange.dto.currency.CurrencyResponse;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationRequest;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationResponse;
import com.currency_exchange.entity.Currency;
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
        String codeBase = calculationRequest.from();
        String codeTarget = calculationRequest.to();
        BigDecimal amount = calculationRequest.amount();

        Currency base = currencyService.findCurrencyEntityByCode(codeBase);
        Currency target = currencyService.findCurrencyEntityByCode(codeTarget);

        CurrencyResponse baseResponse = Mapper.mapToCurrencyResponse(base);
        CurrencyResponse targetResponse = Mapper.mapToCurrencyResponse(target);

        return strategies.stream()
                .filter(strategy -> strategy.canHandle(base, target))
                .findFirst()
                .map(strategy -> strategy.calculate(base, target, amount, baseResponse, targetResponse))
                .orElseThrow(() -> new ExchangeRateNotFoundException(base.getCode(), target.getCode()));
    }
}
