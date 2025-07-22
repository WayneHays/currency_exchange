package com.currency_exchange.service;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.dto.calculation.CalculationRequestDto;
import com.currency_exchange.dto.calculation.CalculationResponseDto;
import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.ExchangeRateNotFoundException;
import com.currency_exchange.service.calculation_strategy.CalculationStrategy;
import com.currency_exchange.service.calculation_strategy.CrossRateStrategy;
import com.currency_exchange.service.calculation_strategy.DirectRateStrategy;
import com.currency_exchange.service.calculation_strategy.ReverseRateStrategy;
import com.currency_exchange.util.Mapper;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

public class CalculationService {
    private static final CalculationService INSTANCE = new CalculationService();
    private static final Long CROSS_CURRENCY_ID = 2L;

    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final DirectRateStrategy directRateStrategy = new DirectRateStrategy(exchangeRatesDao);
    private final ReverseRateStrategy reverseRateStrategy = new ReverseRateStrategy(exchangeRatesDao);
    private final CrossRateStrategy crossRateStrategy = new CrossRateStrategy(exchangeRatesDao, CROSS_CURRENCY_ID);

    private CalculationService() {
    }

    public static CalculationService getInstance() {
        return INSTANCE;
    }


    public CalculationResponseDto calculate(CalculationRequestDto calculationRequest) {
        Currency base = currencyDao.findByCode(calculationRequest.from());
        Currency target = currencyDao.findByCode(calculationRequest.to());

        CurrencyResponseDto baseResponse = Mapper.toCurrencyResponseDto(base);
        CurrencyResponseDto targetResponse = Mapper.toCurrencyResponseDto(target);

        return Stream.of(directRateStrategy, reverseRateStrategy, crossRateStrategy)
                .map(strategy -> tryCalculate(
                        strategy,
                        base,
                        target,
                        calculationRequest.amount(),
                        baseResponse,
                        targetResponse))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new ExchangeRateNotFoundException(base.getCode(), target.getCode()));
    }

    private Optional<CalculationResponseDto> tryCalculate(CalculationStrategy strategy, Currency base, Currency target,
                                                          BigDecimal amount, CurrencyResponseDto baseResponse,
                                                          CurrencyResponseDto targetResponse) {
        try {
            return Optional.of(strategy.calculate(base, target, amount, baseResponse, targetResponse));
        } catch (ExchangeRateNotFoundException e) {
            return Optional.empty();
        }
    }
}