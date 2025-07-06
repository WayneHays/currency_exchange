package com.currency_exchange.servlet.currency_exchange;

import com.currency_exchange.dto.currency_exchange.ExchangeCalculationRequest;
import com.currency_exchange.service.CurrencyExchangeService;
import com.currency_exchange.servlet.BaseServlet;
import com.currency_exchange.util.RequestDataExtractor;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchange")
public class CurrencyExchangeServlet extends BaseServlet {
    private final CurrencyExchangeService currencyExchangeService = CurrencyExchangeService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ExchangeCalculationRequest currencyExchange = RequestDataExtractor.extractValidExchangeData(req);
        currencyExchangeService.calculate(currencyExchange);
}
