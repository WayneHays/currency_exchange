package com.currency_exchange.dto.request;

public class CurrencyRequest {
    private String code;
    private String fullName;
    private String sign;

    public CurrencyRequest(String code, String fullName, String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public void setCode(String code) {
        this.code = code;
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
