package com.currency_exchange.util.data_extraction;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class StringFormatter {

    private StringFormatter() {
    }

    public static String capitalizeRequiredLetters(String input) {
        if (input.contains(" ")) {
            return capitalizeFirstLetterOfEachWord(input);
        }
        return capitalizeFirstLetter(input);
    }

    private static String capitalizeFirstLetterOfEachWord(String input) {
        return Arrays.stream(input.split("\\s+"))
                .filter(word -> !word.isEmpty())
                .map(StringFormatter::capitalizeFirstLetter)
                .collect(Collectors.joining(" "));
    }

    private static String capitalizeFirstLetter(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
