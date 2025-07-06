package com.currency_exchange.servlet.currencies;

import com.currency_exchange.dto.currency.CurrencyCreateRequest;
import com.currency_exchange.dto.currency.CurrencyResponse;
import com.currency_exchange.exception.dao_exception.CurrencyAlreadyExistsException;
import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.service.CurrencyService;
import com.currency_exchange.servlet.BaseServlet;
import com.currency_exchange.util.RequestDataExtractor;
import com.currency_exchange.util.ValidationUtils;
import com.google.gson.JsonIOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/currencies")
public class CurrenciesServlet extends BaseServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);

        try {
            ValidationUtils.validateGetRequest(req);
            List<CurrencyResponse> currencies = currencyService.findAll();
            sendSuccessResponse(resp, currencies);
        } catch (ServiceException | JsonIOException | IOException e) {
            sendError(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);
        try {
            CurrencyCreateRequest dto = RequestDataExtractor.extractCurrencyData(req);
            CurrencyResponse savedCurrency = currencyService.save(dto);
            sendCreatedResponse(resp, savedCurrency);
        } catch (InvalidAttributeException e) {
            sendError(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyAlreadyExistsException e) {
            sendError(resp, SC_CONFLICT, e.getMessage());
        } catch (ServiceException | JsonIOException | IOException e) {
            sendError(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
