package ru.merenkov.diploma.domain;


import java.math.BigDecimal;

public record FuzzyNumber(
        BigDecimal largestValue,
        BigDecimal value,
        BigDecimal smallestValue
) {
}
