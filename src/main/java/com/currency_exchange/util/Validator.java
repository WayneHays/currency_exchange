package com.currency_exchange.util;

import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import com.currency_exchange.exception.servlet_exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public final class Validator {

    private Validator() {
    }

    public static void validate(List<String> parameters) {
        for (String parameter : parameters) {
            if (parameter == null || parameter.isBlank()) {
                throw new InvalidAttributeException(parameter);
            }
        }
    }

    public static void validateNoParameters(HttpServletRequest request) throws BadRequestException {
        if (!request.getParameterMap().isEmpty()) {
            throw new BadRequestException("Parameters are not allowed");
        }
    }

    public static void validateRequiredParameters(HttpServletRequest request, String parameter) throws BadRequestException {
        if (request.getParameter(parameter) == null) {
            throw new BadRequestException("Parameter %s is required".formatted(parameter));
        }
    }
}
