package com.currency_exchange.servlet.exchange_rates;

import com.currency_exchange.dto.currency.CurrencyCodesDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponseDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateDto;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.ExchangeRateNotFoundException;
import com.currency_exchange.exception.service_exception.InvalidParameterException;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.service.ExchangeRateService;
import com.currency_exchange.servlet.BaseServlet;
import com.currency_exchange.util.data_extraction.DataExtractor;
import com.google.gson.JsonIOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends BaseServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);

        try {
            String path = req.getPathInfo();
            CurrencyCodesDto dto = DataExtractor.extractCurrencyPair(path);
            ExchangeRateResponseDto dtoResponse = exchangeRateService.findByPair(dto);
            sendSuccessResponse(resp, dtoResponse);
        } catch (InvalidParameterException e) {
            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (ServiceException | JsonIOException | IOException e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);
        try {
            String path = req.getPathInfo();
            CurrencyCodesDto dto = DataExtractor.extractCurrencyPair(path);
            ExchangeRateUpdateDto updateDto = DataExtractor.extractExchangeRateUpdateRequest(req);
            ExchangeRateResponseDto updated = exchangeRateService.update(dto, updateDto);
            sendSuccessResponse(resp, updated);
        } catch (InvalidParameterException e) {
            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (ServiceException | JsonIOException | IOException e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equals(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }
}
