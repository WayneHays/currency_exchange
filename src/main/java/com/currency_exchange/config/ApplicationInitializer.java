package com.currency_exchange.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ApplicationInitializer implements ServletContextListener {
    public static final String APPLICATION_CONTEXT_ATTRIBUTE = "applicationContext";

    private ApplicationContext applicationContext;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        applicationContext = new ApplicationContext();
        event.getServletContext().setAttribute(APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
    }
}
