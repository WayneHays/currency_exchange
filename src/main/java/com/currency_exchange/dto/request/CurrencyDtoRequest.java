package com.currency_exchange.dto.request;

import com.currency_exchange.util.CurrencyRequest;
import jakarta.servlet.http.HttpServletRequest;

public class CurrencyDtoRequest {
    private String code;
    private String fullName;
    private String sign;

    public CurrencyDtoRequest(String code, String fullName, String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public static CurrencyDtoRequest from(HttpServletRequest req) {
        return new CurrencyDtoRequest(
                req.getParameter(CurrencyRequest.NAME.getParamName()),
                req.getParameter(CurrencyRequest.CODE.getParamName()),
                req.getParameter(CurrencyRequest.SIGN.getParamName())
        );
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
