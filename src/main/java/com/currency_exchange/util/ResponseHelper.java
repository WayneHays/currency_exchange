package com.currency_exchange.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public final class ResponseHelper {
    private static final String CONTENT_TYPE = "application/json; charset=UTF-8";
    private static final String ERROR_TEMPLATE = "{\"message\":\"%s\"}";
    private static final Gson GSON_FORMATTER = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private ResponseHelper() {
    }

    public static void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setStatus(status);
        try (PrintWriter writer = response.getWriter()) {
            writer.write(ERROR_TEMPLATE.formatted(message));
        }
    }

    public static void sendSuccessResponse(HttpServletResponse response, Object responseData) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_OK);
        try (PrintWriter writer = response.getWriter()) {
            GSON_FORMATTER.toJson(responseData, writer);
        }
    }

    public static void sendCreatedResponse(HttpServletResponse response, Object responseData) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_CREATED);
        try (PrintWriter writer = response.getWriter()) {
            GSON_FORMATTER.toJson(responseData, writer);
        }
    }
}
