package com.currency_exchange.util.data_extraction;

public class DataFormatter {

    private DataFormatter() {
    }

    public static String capitalizeRequiredLetters(String input) {
        if (input.contains(" ")) {
            return capitalizeFirstLetterOfEachWord(input);
        }
        return capitalizeFirstLetter(input);
    }

    private static String capitalizeFirstLetterOfEachWord(String input) {
        String[] parts = input.split(" ");
        String firstName = capitalizeFirstLetter(parts[0]);
        String secondName = capitalizeFirstLetter(parts[1]);
        return "%s %s".formatted(firstName, secondName);
    }

    private static String capitalizeFirstLetter(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
