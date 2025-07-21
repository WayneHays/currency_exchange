package com.currency_exchange.servlet.currencies;

import com.currency_exchange.dto.currency.CurrencyRequestDto;
import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.exception.CurrencyAlreadyExistsException;
import com.currency_exchange.exception.InvalidParameterException;
import com.currency_exchange.exception.ServiceException;
import com.currency_exchange.service.CurrencyService;
import com.currency_exchange.servlet.BaseServlet;
import com.currency_exchange.util.data_extraction.ParameterExtractor;
import com.google.gson.JsonIOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/currencies")
public class CurrenciesServlet extends BaseServlet {
    private final transient CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);
        try {
            List<CurrencyResponseDto> currencies = currencyService.findAll();
            sendSuccessResponse(resp, currencies);
        } catch (ServiceException | JsonIOException | IOException e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);
        try {
            Set.of()
            CurrencyRequestDto dto = ParameterExtractor.extractCurrencyRequest(req);
            CurrencyResponseDto savedCurrency = currencyService.save(dto);
            sendCreatedResponse(resp, savedCurrency);
        } catch (InvalidParameterException e) {
            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyAlreadyExistsException e) {
            sendErrorResponse(resp, SC_CONFLICT, e.getMessage());
        } catch (ServiceException | JsonIOException | IOException e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
