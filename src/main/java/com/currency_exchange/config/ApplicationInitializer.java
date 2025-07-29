package com.currency_exchange.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ApplicationInitializer implements ServletContextListener {
    public static final String KEY = "applicationContext";

    private ApplicationContext applicationContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        applicationContext = new ApplicationContext();
        sce.getServletContext().setAttribute(KEY, applicationContext);
    }
}
