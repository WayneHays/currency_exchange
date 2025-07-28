package com.currency_exchange.servlet;

import com.currency_exchange.dto.calculation.CalculationRequestDto;
import com.currency_exchange.dto.calculation.CalculationResponseDto;
import com.currency_exchange.service.CalculationService;
import com.currency_exchange.util.http.ParameterExtractor;
import com.currency_exchange.util.http.ResponseHelper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchange")
public class CalculationServlet extends HttpServlet {
    private final CalculationService calculationService = CalculationService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CalculationRequestDto dto = ParameterExtractor.extractCalculationRequest(req);
        CalculationResponseDto calculatedResponse = calculationService.calculate(dto);
        ResponseHelper.sendSuccessResponse(resp, calculatedResponse);
    }
}
