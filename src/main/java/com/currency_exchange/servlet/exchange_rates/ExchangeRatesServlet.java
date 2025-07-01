package com.currency_exchange.servlet.exchange_rates;

import com.currency_exchange.dto.request.ExchangeRateDtoRequest;
import com.currency_exchange.dto.response.ExchangeRateDtoResponse;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.ExchangeRateConflictException;
import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.service.ExchangeRateService;
import com.currency_exchange.servlet.BaseServlet;
import com.currency_exchange.util.Mapper;
import com.currency_exchange.util.RequestParser;
import com.currency_exchange.util.validator.ExchangeRateValidator;
import com.google.gson.JsonIOException;
import jakarta.servlet.ServletException;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setResponseConfig(resp);

        try {
            ExchangeRateValidator.validateEmptyRequest(req);
            List<ExchangeRateDtoResponse> result = exchangeRateService.findAll();
            sendSuccessGetJsonResponse(resp, result);
        } catch (ServiceException | JsonIOException | IOException e) {
            sendError(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ExchangeRateValidator.validateRequestParameters(req);
            String[] exchangeRateData = RequestParser.extractExchangeRateData(req);
            ExchangeRateDtoRequest dtoRequest = Mapper.mapToExchangeRateDtoRequest(
                    exchangeRateData[0],
                    exchangeRateData[1],
                    exchangeRateData[2]);
            ExchangeRateDtoResponse saved = exchangeRateService.save(dtoRequest);
            sendSuccessCreatedJsonResponse(resp, saved);
        } catch (NumberFormatException | InvalidAttributeException e) {
            sendError(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (ExchangeRateConflictException e) {
            sendError(resp, SC_CONFLICT, e.getMessage());
        } catch (CurrencyNotFoundException e) {
            sendError(resp, SC_NOT_FOUND, e.getMessage());
        } catch (ServiceException | JsonIOException | IOException e) {
            sendError(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

