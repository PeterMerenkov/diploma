package ru.merenkov.diploma.domain;

public record ResultDataHolder(
        String paramName,
        String termSetName,
        Double importanceWeight
) {
}
