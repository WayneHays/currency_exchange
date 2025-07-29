package com.currency_exchange.servlet.exchange_rates;

import com.currency_exchange.config.ApplicationContext;
import com.currency_exchange.dto.currency.CurrencyPairDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponseDto;
import com.currency_exchange.service.ExchangeRateService;
import com.currency_exchange.util.http.ParameterExtractor;
import com.currency_exchange.util.http.PathExtractor;
import com.currency_exchange.util.http.ResponseHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        ApplicationContext context = (ApplicationContext) getServletContext().getAttribute("applicationContext");
        this.exchangeRateService = context.getExchangeRateService();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equals(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CurrencyPairDto dto = PathExtractor.extractCurrencyPair(req);
        ExchangeRateResponseDto dtoResponse = exchangeRateService.findByPair(dto);
        ResponseHelper.sendSuccessResponse(resp, dtoResponse);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CurrencyPairDto dto = PathExtractor.extractCurrencyPair(req);
        BigDecimal rate = ParameterExtractor.extractRate(req);
        ExchangeRateResponseDto updated = exchangeRateService.update(dto, rate);
        ResponseHelper.sendSuccessResponse(resp, updated);
    }
}
