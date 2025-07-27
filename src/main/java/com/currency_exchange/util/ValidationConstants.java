package com.currency_exchange.util;

public final class ValidationConstants {

    public static final String CODE = "code";
    public static final String NAME = "name";
    public static final String SIGN = "sign";
    public static final String BASE_CURRENCY_CODE = "baseCurrencyCode";
    public static final String TARGET_CURRENCY_CODE = "targetCurrencyCode";
    public static final String RATE = "rate";
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String AMOUNT = "amount";

    public static final String NAME_PATTERN = "^[\\p{L}\\s\\-]{2,30}$";
    public static final String CODE_PATTERN = "^[A-Za-z]{3}$";
    public static final String SIGN_PATTERN = "^[A-Za-z\\p{Sc}]";
    public static final String RATE_PATTERN = "^(?!0+([.,]0+)?$)([1-9]\\d{0,5}([.,]\\d{1,6})?|0[.,]\\d{1,6}|[.,]\\d{1,6})$";
    public static final String PAIR_PATTERN = "^([A-Za-z]{3})([A-Za-z]{3})$";

    public static final String NAME_ERROR_MESSAGE = "Currency name must be 2-30 latin letters";
    public static final String CODE_ERROR_MESSAGE = "Currency code must be 3 latin letters";
    public static final String SIGN_ERROR_MESSAGE = "Currency sign must be a valid symbol (e.g. $, €...) or 1 latin letter";
    public static final String RATE_ERROR_MESSAGE = "Rate must be a positive number with up to 6 digits before and after the point";
    public static final String AMOUNT_ERROR_MESSAGE = "Amount must be a positive number with up to 6 digits before and after the point";
    public static final String WRONG_PAIR_MESSAGE = "Base and target currencies must be different";
    public static final String PAIR_ERROR_MESSAGE = "Path must be 6 latin letters like USDRUB";
    public static final String MISSING_CURRENCY_CODE_MESSAGE = "Missing currency code";
    public static final String MISSING_PAIR_MESSAGE = "Missing currency pair codes";

    private ValidationConstants() {
    }
}
