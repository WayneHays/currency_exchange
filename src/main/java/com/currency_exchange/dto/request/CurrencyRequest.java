package com.currency_exchange.dto.request;

public class CurrencyRequest {
    private final String code;
    private final String fullName;
    private final String sign;

    public CurrencyRequest(String code, String fullName, String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public String getCode() {
        return code;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSign() {
        return sign;
    }
}
