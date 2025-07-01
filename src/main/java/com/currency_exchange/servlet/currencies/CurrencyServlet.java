package com.currency_exchange.servlet.currencies;

import com.currency_exchange.dto.response.CurrencyDtoResponse;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.service.CurrencyService;
import com.currency_exchange.servlet.BaseServlet;
import com.currency_exchange.util.validator.CurrencyValidator;
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
            String code = extractCode(req);
            CurrencyValidator.validate(code);
            CurrencyDtoResponse dtoResponse = currencyService.findByCode(code);
            sendSuccessJsonResponse(resp, dtoResponse);
        } catch (InvalidAttributeException e) {
            sendError(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyNotFoundException e) {
            sendError(resp, SC_NOT_FOUND, e.getMessage());
        } catch (ServiceException | JsonIOException | IOException e) {
            sendError(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private String extractCode(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        CurrencyValidator.validatePathInfo(pathInfo);
        return pathInfo.substring(1);
    }
}
