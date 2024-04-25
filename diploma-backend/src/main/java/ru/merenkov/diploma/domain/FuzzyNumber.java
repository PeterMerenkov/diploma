package ru.merenkov.diploma.domain;


public record FuzzyNumber(
        Double largestValue,
        Double value,
        Double smallestValue
) {
}
