package com.currency_exchange.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebFilter(value = "/*", filterName = "EncodingFilter")
public class EncodingFilter implements Filter {
    private static final String ENCODING = StandardCharsets.UTF_8.toString();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        
        request.setCharacterEncoding(ENCODING);
        response.setCharacterEncoding(ENCODING);
        filterChain.doFilter(request, response);
    }
}
