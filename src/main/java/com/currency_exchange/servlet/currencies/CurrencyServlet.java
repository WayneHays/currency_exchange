package com.currency_exchange.servlet.currencies;

import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.exception.CurrencyNotFoundException;
import com.currency_exchange.exception.InvalidParameterException;
import com.currency_exchange.exception.ServiceException;
import com.currency_exchange.service.CurrencyService;
import com.currency_exchange.servlet.BaseServlet;
import com.currency_exchange.util.data_extraction.PathExtractor;
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
        prepareJsonResponse(resp);

        try {
            String code = PathExtractor.extractCurrencyCode(req);
            CurrencyResponseDto dto = currencyService.findByCode(code);
            sendSuccessResponse(resp, dto);
        } catch (InvalidParameterException e) {
            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (ServiceException | JsonIOException | IOException e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
