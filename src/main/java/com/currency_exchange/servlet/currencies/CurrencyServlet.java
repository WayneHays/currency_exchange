package com.currency_exchange.servlet.currencies;

import com.currency_exchange.config.ApplicationContext;
import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.service.CurrencyService;
import com.currency_exchange.util.http.PathExtractor;
import com.currency_exchange.util.http.ResponseHelper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyService currencyService;
    
    @Override
    public void init() {
        ApplicationContext context = (ApplicationContext) getServletContext().getAttribute("applicationContext");
        this.currencyService = context.getCurrencyService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = PathExtractor.extractCurrencyCode(req);
        CurrencyResponseDto dto = currencyService.findByCode(code);
        ResponseHelper.sendSuccessResponse(resp, dto);
    }
}
