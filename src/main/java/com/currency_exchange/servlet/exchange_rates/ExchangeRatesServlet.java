package com.currency_exchange.servlet.exchange_rates;

import com.currency_exchange.dto.exchange_rate.ExchangeRateRequestDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponseDto;
import com.currency_exchange.exception.CurrencyNotFoundException;
import com.currency_exchange.exception.ExchangeRateAlreadyExistsException;
import com.currency_exchange.exception.InvalidParameterException;
import com.currency_exchange.exception.ServiceException;
import com.currency_exchange.service.ExchangeRateService;
import com.currency_exchange.servlet.BaseServlet;
import com.currency_exchange.util.data_extraction.ParameterExtractor;
import com.google.gson.JsonIOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends BaseServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);
        try {
            List<ExchangeRateResponseDto> exchangeRates = exchangeRateService.findAll();
            sendSuccessResponse(resp, exchangeRates);
        } catch (ServiceException | JsonIOException | IOException e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);
        try {
            ExchangeRateRequestDto dto = ParameterExtractor.extractExchangeRateRequest(req);
            ExchangeRateResponseDto saved = exchangeRateService.save(dto);
            sendCreatedResponse(resp, saved);
        } catch (InvalidParameterException e) {
            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (ExchangeRateAlreadyExistsException e) {
            sendErrorResponse(resp, SC_CONFLICT, e.getMessage());
        } catch (CurrencyNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (ServiceException | JsonIOException | IOException e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

