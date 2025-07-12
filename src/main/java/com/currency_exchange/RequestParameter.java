package com.currency_exchange;

import com.currency_exchange.exception.service_exception.InvalidParameterException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public interface RequestParameter {
    String getParamName();

    String getRegex();

    String getErrorMessage();

    static <T extends Enum<T> & RequestParameter> T fromParamName(Class<T> enumClass, String paramName) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(param -> param.getParamName().equals(paramName))
                .findFirst()
                .orElseThrow(() -> new InvalidParameterException("Unknown parameter: %s".formatted(paramName)));
    }

    static <T extends Enum<T> & RequestParameter> Set<String> getRequiredParamNames(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(RequestParameter::getParamName)
                .collect(Collectors.toUnmodifiableSet());
    }
}
