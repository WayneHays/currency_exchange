package com.currency_exchange.util;

public final class ValidationConstants {
    public static final String CURRENCY_NAME_PATTERN = "^[\\p{L}\\s\\-]{2,30}$";
    public static final String CURRENCY_CODE_PATTERN = "^[A-Za-z]{3}$";
    public static final String CURRENCY_SIGN_PATTERN = "^\\p{Sc}";
    public static final String EXCHANGE_RATE_RATE_PATTERN = "^(?!0+([.,]0+)?$)([1-9]\\d{0,5}([.,]\\d{1,6})?|0[.,]\\d{1,6}|[.,]\\d{1,6})$";
    public static final String CURRENCY_PAIR_PATTERN = "^/([A-Za-z]{3})([A-Za-z]{3})$";

    public static final String CURRENCY_NAME_ERROR_MESSAGE = "Currency name must be 2-30 latin letters";
    public static final String CURRENCY_CODE_ERROR_MESSAGE = "Currency code must be 3 latin letters";
    public static final String CURRENCY_SIGN_ERROR_MESSAGE = "Currency sign must be a valid symbol (e.g. $, €...";
    public static final String EXCHANGE_RATE_RATE_ERROR_MESSAGE = "Rate must be a positive number with up to 6 digits before and after the point";
    public static final String CALCULATION_AMOUNT_ERROR_MESSAGE = "Amount must be a positive number with up to 6 digits before and after the point";
    public static final String WRONG_PAIR_MESSAGE = "Base and target currencies must be different";
    public static final String CURRENCY_PAIR_ERROR_MESSAGE = "Path must be 6 latin letters like USDRUB";
    public static final String MISSING_CURRENCY_CODE = "Missing currency code";
    public static final String MISSING_CURRENCY_PAIR_CODE = "Missing currency pair code";
    public static final String MISSING_RATE_MESSAGE = "Missing rate parameter";
    public static final String UNKNOWN_CURRENCY_PARAM = "Unknown currency param";

    private ValidationConstants() {
    }
}
