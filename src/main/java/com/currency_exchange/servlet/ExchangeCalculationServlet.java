package com.currency_exchange.servlet;

import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationRequest;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationResponse;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.ExchangeRateNotFoundException;
import com.currency_exchange.exception.service_exception.InvalidParameterException;
import com.currency_exchange.service.ExchangeCalculationService;
import com.currency_exchange.util.data_extraction.RequestDataExtractor;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@WebServlet("/exchange")
public class ExchangeCalculationServlet extends BaseServlet {
    private final ExchangeCalculationService exchangeCalculationService = ExchangeCalculationService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);

        try {
            ExchangeCalculationRequest calculationRequest = RequestDataExtractor.extractValidCalculationData(req);
            ExchangeCalculationResponse calculatedResponse = exchangeCalculationService.calculate(calculationRequest);
            sendSuccessResponse(resp, calculatedResponse);
        } catch (InvalidParameterException e) {
            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        }
    }
}
