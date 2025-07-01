package com.currency_exchange.servlet.currencies;

import com.currency_exchange.dto.request.CurrencyDtoRequest;
import com.currency_exchange.dto.response.CurrencyDtoResponse;
import com.currency_exchange.exception.service_exception.CurrencyConflictException;
import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.service.CurrencyService;
import com.currency_exchange.servlet.BaseServlet;
import com.currency_exchange.util.CurrencyRequest;
import com.currency_exchange.util.Mapper;
import com.currency_exchange.util.validator.CurrencyValidator;
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
        setResponseConfig(resp);

        try {
            CurrencyValidator.validateGet(req);
            List<CurrencyDtoResponse> currencies = currencyService.findAll();
            sendSuccessJsonResponse(resp, currencies);
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
}
