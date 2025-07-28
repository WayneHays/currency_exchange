package com.currency_exchange.constant;

public class ValidationPatterns {
    public static final String NAME_PATTERN = "^[\\p{L}\\s\\-]{2,30}$";
    public static final String CODE_PATTERN = "^[A-Za-z]{3}$";
    public static final String SIGN_PATTERN = "^[A-Za-z\\p{Sc}]";
    public static final String RATE_PATTERN = "^(?!0+([.,]0+)?$)([1-9]\\d{0,5}([.,]\\d{1,6})?|0[.,]\\d{1,6}|[.,]\\d{1,6})$";
    public static final String PAIR_PATTERN = "^([A-Za-z]{3})([A-Za-z]{3})$";

    private ValidationPatterns() {
    }
}
