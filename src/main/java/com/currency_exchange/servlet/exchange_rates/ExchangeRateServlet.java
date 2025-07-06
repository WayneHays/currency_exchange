package com.currency_exchange.servlet.exchange_rates;

import com.currency_exchange.dto.currency.CurrencyPairDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponse;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateRequest;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.ExchangeRateNotFoundException;
import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.service.ExchangeRateService;
import com.currency_exchange.servlet.BaseServlet;
import com.currency_exchange.util.RequestDataExtractor;
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
            CurrencyPairDto currencyPairDto = RequestDataExtractor.extractValidCurrencyPairData(req);
            ExchangeRateResponse dtoResponse = exchangeRateService.findByPair(currencyPairDto);
            sendSuccessResponse(resp, dtoResponse);
        } catch (InvalidAttributeException e) {
            sendError(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            sendError(resp, SC_NOT_FOUND, e.getMessage());
        } catch (ServiceException | JsonIOException | IOException e) {
            sendError(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);
        try {
            CurrencyPairDto currencyPairDto = RequestDataExtractor.extractValidCurrencyPairData(req);
            ExchangeRateUpdateRequest rateDto = RequestDataExtractor.extractValidPatchData(req);
            ExchangeRateResponse updated = exchangeRateService.update(currencyPairDto, rateDto);
            sendSuccessResponse(resp, updated);
        } catch (InvalidAttributeException e) {
            sendError(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (ExchangeRateNotFoundException e) {
            sendError(resp, SC_NOT_FOUND, e.getMessage());
        } catch (ServiceException | JsonIOException | IOException e) {
            sendError(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
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
