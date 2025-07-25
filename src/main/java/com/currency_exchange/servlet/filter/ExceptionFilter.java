package com.currency_exchange.servlet.filter;

import com.currency_exchange.util.ResponseHelper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter(value = "/*", filterName = "ExceptionFilter")
public class ExceptionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            handleException(httpResponse, e);
        }
    }

    private void handleException(HttpServletResponse response, RuntimeException e) throws IOException {
        int statusCode;
        String message = e.getMessage();

        switch (e.getClass().getSimpleName()) {
            case "InvalidParameterException" -> {
                statusCode = SC_BAD_REQUEST;
            }
            case "CurrencyNotFoundException", "ExchangeRateNotFoundException" -> {
                statusCode = SC_CONFLICT;
            }
            case "ServiceException" -> {
                statusCode = SC_INTERNAL_SERVER_ERROR;
            }
            default -> {
                statusCode = SC_INTERNAL_SERVER_ERROR;
                message = "An unexpected error occurred";
            }
        }
        ResponseHelper.sendErrorResponse(response, statusCode, message);
    }
}
