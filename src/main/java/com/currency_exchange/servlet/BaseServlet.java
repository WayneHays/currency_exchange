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
    protected final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected void setResponseConfig(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }

    protected void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write("{\"message\":\"%s\"}".formatted(message));
        }
    }

    protected void sendSuccessGetJsonResponse(HttpServletResponse resp, Object responseData) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        try (PrintWriter writer = resp.getWriter()) {
            gson.toJson(responseData, writer);
        }
    }

    protected void sendSuccessCreatedJsonResponse(HttpServletResponse resp, Object responseData) throws IOException {
        resp.setStatus(SC_CREATED);
        try (PrintWriter writer = resp.getWriter()) {
            gson.toJson(responseData, writer);
        }
    }
}
