package com.currency_exchange.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;

public class BaseServlet extends HttpServlet {
    protected static final String TEMPLATE = "{\"message\":\"%s\"}";
    protected final Gson gsonFormatter = new GsonBuilder().setPrettyPrinting().create();

    protected void prepareJsonResponse(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }

    protected void sendErrorResponse(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(TEMPLATE.formatted(message));
        }
    }

    protected void sendSuccessResponse(HttpServletResponse resp, Object responseData) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        try (PrintWriter writer = resp.getWriter()) {
            gsonFormatter.toJson(responseData, writer);
        }
    }

    protected void sendCreatedResponse(HttpServletResponse resp, Object responseData) throws IOException {
        resp.setStatus(SC_CREATED);
        try (PrintWriter writer = resp.getWriter()) {
            gsonFormatter.toJson(responseData, writer);
        }
    }
}
