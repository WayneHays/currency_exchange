package com.currency_exchange.servlet.exchange_rates;

import com.currency_exchange.dto.exchange_rate.ExchangeRateRequestDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponseDto;
import com.currency_exchange.service.ExchangeRateService;
import com.currency_exchange.util.ResponseHelper;
import com.currency_exchange.util.data_extraction.ParameterExtractor;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRateResponseDto> exchangeRates = exchangeRateService.findAll();
        ResponseHelper.sendSuccessResponse(resp, exchangeRates);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRateRequestDto dto = ParameterExtractor.extractExchangeRateRequest(req);
        ExchangeRateResponseDto saved = exchangeRateService.save(dto);
        ResponseHelper.sendCreatedResponse(resp, saved);
    }
}

