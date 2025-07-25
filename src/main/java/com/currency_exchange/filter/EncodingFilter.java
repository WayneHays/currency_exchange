package com.currency_exchange.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebFilter(value = "/*", filterName = "EncodingFilter")
public class EncodingFilter implements Filter {
    private static final String ENCODING = StandardCharsets.UTF_8.toString();
    private static final String CONTENT_TYPE = "application/json";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        request.setCharacterEncoding(ENCODING);
        response.setCharacterEncoding(ENCODING);
        response.setContentType(CONTENT_TYPE);

        filterChain.doFilter(request, response);
    }
}
