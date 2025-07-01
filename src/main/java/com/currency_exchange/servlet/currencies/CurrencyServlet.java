package com.currency_exchange.servlet.currencies;

import com.currency_exchange.dto.response.CurrencyDtoResponse;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.service.CurrencyService;
import com.currency_exchange.servlet.BaseServlet;
import com.currency_exchange.util.RequestParser;
import com.google.gson.JsonIOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/currency/*")
public class CurrencyServlet extends BaseServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setResponseConfig(resp);

        try {
            String code = RequestParser.extractCurrencyCode(req);
            CurrencyDtoResponse dtoResponse = currencyService.findByCode(code);
            sendSuccessGetJsonResponse(resp, dtoResponse);
        } catch (InvalidAttributeException e) {
            sendError(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyNotFoundException e) {
            sendError(resp, SC_NOT_FOUND, e.getMessage());
        } catch (ServiceException | JsonIOException | IOException e) {
            sendError(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
