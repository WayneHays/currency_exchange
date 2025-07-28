package com.currency_exchange.filter;

import com.currency_exchange.util.ResponseHelper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter(value = "/*", filterName = "ExceptionFilter")
public class ExceptionFilter implements Filter {
    public static final String INVALID_PARAMETER_EXCEPTION = "InvalidParameterException";
    public static final String CURRENCY_NOT_FOUND_EXCEPTION = "CurrencyNotFoundException";
    public static final String EXCHANGE_RATE_NOT_FOUND_EXCEPTION = "ExchangeRateNotFoundException";
    public static final String CURRENCY_ALREADY_EXISTS_EXCEPTION = "CurrencyAlreadyExistsException";
    public static final String EXCHANGE_RATE_ALREADY_EXISTS_EXCEPTION = "ExchangeRateAlreadyExistsException";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred";
    public static final String SERVICE_EXCEPTION = "ServiceException";
    public static final String INPUT_OUTPUT_ERROR_OCCURRED = "Input/Output error occurred";
    public static final String SERVER_PROCESSING_ERROR = "Server processing error";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            handleRuntimeException(httpResponse, e);
        } catch (IOException e) {
            handleIOException(httpResponse, e);
        } catch (ServletException e) {
            handleServletException(httpResponse, e);
        }
    }

    private void handleRuntimeException(HttpServletResponse response, RuntimeException e) throws IOException {
        int statusCode;
        String message = e.getMessage();

        switch (e.getClass().getSimpleName()) {
            case INVALID_PARAMETER_EXCEPTION -> statusCode = SC_BAD_REQUEST;
            case CURRENCY_NOT_FOUND_EXCEPTION, EXCHANGE_RATE_NOT_FOUND_EXCEPTION -> statusCode = SC_NOT_FOUND;
            case CURRENCY_ALREADY_EXISTS_EXCEPTION, EXCHANGE_RATE_ALREADY_EXISTS_EXCEPTION -> statusCode = SC_CONFLICT;
            case SERVICE_EXCEPTION -> statusCode = SC_INTERNAL_SERVER_ERROR;
            default -> {
                statusCode = SC_INTERNAL_SERVER_ERROR;
                message = UNEXPECTED_ERROR;
            }
        }
        sendErrorIfPossible(response, statusCode, message);
    }

    private void handleIOException(HttpServletResponse response, IOException e) throws IOException {
        sendErrorIfPossible(response, SC_INTERNAL_SERVER_ERROR,
                INPUT_OUTPUT_ERROR_OCCURRED);
        throw e;
    }

    private void handleServletException(HttpServletResponse response, ServletException e) throws IOException {
        sendErrorIfPossible(response, SC_INTERNAL_SERVER_ERROR,
                SERVER_PROCESSING_ERROR);
    }

    private void sendErrorIfPossible(HttpServletResponse response, int statusCode, String message) throws IOException {
        if (!response.isCommitted()) {
            ResponseHelper.sendErrorResponse(response, statusCode, message);
        }
    }
}
