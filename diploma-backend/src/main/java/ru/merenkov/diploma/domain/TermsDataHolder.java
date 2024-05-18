package ru.merenkov.diploma.domain;

public record TermsDataHolder(
        String paramName,
        Double smallest,
        Double largest,
        String termSetName,
        Double importanceWeight
) {
}