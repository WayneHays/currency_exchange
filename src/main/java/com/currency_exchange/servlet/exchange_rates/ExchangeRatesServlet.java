package com.currency_exchange.servlet.exchange_rates;

import com.currency_exchange.dto.request.ExchangeRateDtoRequest;
import com.currency_exchange.dto.response.ExchangeRateDtoResponse;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.ExchangeRateConflictException;
import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.service.ExchangeRateService;
import com.currency_exchange.util.ExchangeRateRequest;
import com.currency_exchange.util.Mapper;
import com.currency_exchange.util.validator.ExchangeRateValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            ExchangeRateValidator.validateEmptyRequest(req);
            List<ExchangeRateDtoResponse> exchangeRateDtoResponses = exchangeRateService.findAll();
            resp.setStatus(SC_OK);
            gson.toJson(exchangeRateDtoResponses, resp.getWriter());
        } catch (ServiceException | JsonIOException | IOException e) {
            sendError(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ExchangeRateValidator.validateRequestParameters(req);

            String baseCurrencyCode = req.getParameter(ExchangeRateRequest.BASE_CURRENCY_CODE.getName());
            String targetCurrencyCode = req.getParameter(ExchangeRateRequest.TARGET_CURRENCY_CODE.getName());
            String rate = req.getParameter(ExchangeRateRequest.RATE.getName());

            ExchangeRateDtoRequest dtoRequest = Mapper.mapToExchangeRateDtoRequest(baseCurrencyCode, targetCurrencyCode, rate);
            ExchangeRateDtoResponse saved = exchangeRateService.save(dtoRequest);
            resp.setStatus(SC_CREATED);
            gson.toJson(saved, resp.getWriter());
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

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(("{\"message\":\"%s\"}".formatted(message)));
    }
}

