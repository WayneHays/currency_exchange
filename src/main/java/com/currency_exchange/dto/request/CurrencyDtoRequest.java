package com.currency_exchange.dto.request;

public class CurrencyDtoRequest {
    private String code;
    private String fullName;
    private String sign;

    public CurrencyDtoRequest(String code, String fullName, String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public CurrencyDtoRequest() {
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setSign(String sign) {
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
