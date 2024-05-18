package ru.merenkov.diploma.domain;


import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record FuzzyNumbersHolder(
        int paramIndex,
        List<FuzzyNumber> fuzzyNumbers
) {
    @Builder
    public record FuzzyNumber(
            BigDecimal largestValue,
            BigDecimal value,
            BigDecimal smallestValue
    ) {
    }
}
