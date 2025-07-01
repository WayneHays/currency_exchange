package com.currency_exchange.servlet.exchange_rates;

import com.currency_exchange.dto.response.ExchangeRateDtoResponse;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.ExchangeRateNotFoundException;
import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.service.ExchangeRateService;
import com.currency_exchange.servlet.BaseServlet;
import com.currency_exchange.util.RequestParser;
import com.google.gson.JsonIOException;
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
        setResponseConfig(resp);

        try {
            String[] currencyCodes = RequestParser.extractCurrencyPairCodes(req);
            ExchangeRateDtoResponse dtoResponse = exchangeRateService.findByCurrencyCodes(currencyCodes[0], currencyCodes[1]);
            sendSuccessGetJsonResponse(resp, dtoResponse);
        } catch (InvalidAttributeException e) {
            sendError(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            sendError(resp, SC_NOT_FOUND, e.getMessage());
        } catch (ServiceException | JsonIOException | IOException e) {
            sendError(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
