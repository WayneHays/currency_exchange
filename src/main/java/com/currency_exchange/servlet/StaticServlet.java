package com.currency_exchange.servlet;


import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;

@WebServlet("/*")
public class StaticServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String path = req.getRequestURI();

        // API запросы - пропускаем
        if (path.startsWith("/currencies") || path.startsWith("/currency") ||
            path.startsWith("/exchangeRate") || path.startsWith("/exchange")) {
            resp.sendError(404);
            return;
        }

        // Корневой путь = index.html
        if ("/".equals(path)) path = "/index.html";

        // Получаем файл из webapp/
        InputStream is = getServletContext().getResourceAsStream(path);
        if (is == null) {
            resp.sendError(404);
            return;
        }

        // Отдаем файл
        resp.setContentType(getContentType(path));
        is.transferTo(resp.getOutputStream());
        is.close();
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        return "text/plain";
    }
}
