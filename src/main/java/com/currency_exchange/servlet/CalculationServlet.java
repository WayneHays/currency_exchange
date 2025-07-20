package com.currency_exchange.servlet;

import com.currency_exchange.dto.exchange_calculation.CalculationRequestDto;
import com.currency_exchange.dto.exchange_calculation.CalculationResponseDto;
import com.currency_exchange.exception.CurrencyNotFoundException;
import com.currency_exchange.exception.ExchangeRateNotFoundException;
import com.currency_exchange.exception.InvalidParameterException;
import com.currency_exchange.service.CalculationService;
import com.currency_exchange.util.data_extraction.DataExtractor;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@WebServlet("/exchange")
public class CalculationServlet extends BaseServlet {
    private final CalculationService calculationService = CalculationService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);

        try {
            CalculationRequestDto dto = DataExtractor.extractCalculationRequestDto(req);
            CalculationResponseDto calculatedResponse = calculationService.calculate(dto);
            sendSuccessResponse(resp, calculatedResponse);
        } catch (InvalidParameterException e) {
            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        }
    }
}
