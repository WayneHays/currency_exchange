package com.currency_exchange.servlet;

import com.currency_exchange.service.CalculationService;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/exchange")
public class CalculationServlet extends BaseServlet {
    private final CalculationService calculationService = CalculationService.getInstance();

//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        prepareJsonResponse(resp);
//
//        try {
//            CalculationRequestDto dto = ParameterExtractor.extractCalculationRequest(req);
//            CalculationResponseDto calculatedResponse = calculationService.calculate(dto);
//            sendSuccessResponse(resp, calculatedResponse);
//        } catch (InvalidParameterException e) {
//            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
//        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
//            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
//        }
//    }
}
