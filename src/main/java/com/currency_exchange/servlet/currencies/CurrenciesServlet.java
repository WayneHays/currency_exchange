package com.currency_exchange.servlet.currencies;

import com.currency_exchange.dto.response.CurrencyDtoResponse;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.exception.servlet_exception.BadRequestException;
import com.currency_exchange.service.CurrencyService;
import com.currency_exchange.util.Validator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    private static final CurrencyService currencyService = CurrencyService.getInstance();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            Validator.validateNoParameters(req);
            List<CurrencyDtoResponse> currencies = currencyService.findAll();
            resp.setStatus(SC_OK);
            gson.toJson(currencies, resp.getWriter());
        } catch (BadRequestException e) {
            resp.setStatus(SC_BAD_REQUEST);
            resp.getWriter().write(("{\"message\":\"%s\"}".formatted(e.getMessage())));
        } catch (ServiceException e) {
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(("{\"message\":\"%s\"}".formatted(e.getMessage())));
        }
    }

//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//
//        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
//        String name = req.getParameter("name");
//        String code = req.getParameter("code");
//        String sign = req.getParameter("sign");
//
//        try {
//            Validator.validate(List.of(name, code, sign));
//            CurrencyDtoRequest dtoRequest = Mapper.mapToDtoRequest(name, code, sign);
//            currencyService.save(dtoRequest);
//
//        } catch (InvalidAttributeException e) {
//            resp.setStatus(SC_BAD_REQUEST);
//            resp.getWriter().write(("{\"message\":\"%s\"}".formatted(e.getMessage())));
//        } catch (ServiceException e) {
//            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
//            resp.getWriter().write(("{\"message\":\"%s\"}".formatted(e.getMessage())));
//        }
//    }
}
