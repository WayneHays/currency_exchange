package com.currency_exchange.servlet.currencies;

import com.currency_exchange.dto.request.CurrencyDtoRequest;
import com.currency_exchange.dto.response.CurrencyDtoResponse;
import com.currency_exchange.exception.service_exception.CurrencyConflictException;
import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.service.CurrencyService;
import com.currency_exchange.util.CurrencyRequest;
import com.currency_exchange.util.Mapper;
import com.currency_exchange.util.validator.CurrencyValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            CurrencyValidator.validateGet(req);
            List<CurrencyDtoResponse> currencies = currencyService.findAll();
            resp.setStatus(SC_OK);
            gson.toJson(currencies, resp.getWriter());
        } catch (ServiceException | JsonIOException | IOException e) {
            sendError(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            CurrencyValidator.validatePost(req);

            String name = req.getParameter(CurrencyRequest.NAME.getParamName());
            String code = req.getParameter(CurrencyRequest.CODE.getParamName());
            String sign = req.getParameter(CurrencyRequest.SIGN.getParamName());

            CurrencyDtoRequest dtoRequest = Mapper.mapToCurrencyDtoRequest(name, code, sign);
            CurrencyDtoResponse saved = currencyService.save(dtoRequest);
            resp.setStatus(SC_CREATED);
            gson.toJson(saved, resp.getWriter());
        } catch (InvalidAttributeException e) {
            sendError(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyConflictException e) {
            sendError(resp, SC_CONFLICT, e.getMessage());
        } catch (ServiceException | JsonIOException | IOException e) {
            sendError(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(("{\"message\":\"%s\"}".formatted(message)));
    }
}
