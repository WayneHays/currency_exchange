package com.currency_exchange.util.constant;

public final class ValidationErrorMessages {
    public static final String NAME_ERROR_MESSAGE = "Currency name must be 2-30 latin letters";
    public static final String CODE_ERROR_MESSAGE = "Currency code must be 3 latin letters";
    public static final String SIGN_ERROR_MESSAGE = "Currency sign must be a valid symbol (e.g. $, €...) or 1 latin letter";
    public static final String RATE_ERROR_MESSAGE = "Rate must be a positive number with up to 6 digits before and after the point";
    public static final String AMOUNT_ERROR_MESSAGE = "Amount must be a positive number with up to 6 digits before and after the point";
    public static final String WRONG_PAIR_MESSAGE = "Base and target currencies must be different";
    public static final String PAIR_ERROR_MESSAGE = "Path must be 6 latin letters like USDRUB";
    public static final String MISSING_CURRENCY_CODE_MESSAGE = "Missing currency code";
    public static final String MISSING_PAIR_MESSAGE = "Missing currency pair codes";

    private ValidationErrorMessages() {
    }
}
