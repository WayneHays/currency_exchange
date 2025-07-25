package com.currency_exchange.servlet.currencies;

import com.currency_exchange.dto.currency.CurrencyRequestDto;
import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.service.CurrencyService;
import com.currency_exchange.util.ResponseHelper;
import com.currency_exchange.util.data_extraction.ParameterExtractor;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final transient CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<CurrencyResponseDto> currencies = currencyService.findAll();
        ResponseHelper.sendSuccessResponse(resp, currencies);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CurrencyRequestDto dto = ParameterExtractor.extractCurrencyRequest(req);
        CurrencyResponseDto savedCurrency = currencyService.save(dto);
        ResponseHelper.sendCreatedResponse(resp, savedCurrency);
    }
}
