package com.currency_exchange.servlet.currencies;

import com.currency_exchange.dto.response.CurrencyDtoResponse;
import com.currency_exchange.exception.ServiceException;
import com.currency_exchange.service.CurrencyService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/currency")
public class CurrencyServlet extends HttpServlet {
    private static final CurrencyService currencyService = CurrencyService.getInstance();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            List<CurrencyDtoResponse> currencies = currencyService.findAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            gson.toJson(currencies, resp.getWriter());
        } catch (ServiceException e) {
            resp.setStatus(e.getHttpStatus());
            resp.getWriter().write("sosihue");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
       resp.setContentType("application/json");
       resp.setCharacterEncoding(StandardCharsets.UTF_8);
    }
}
