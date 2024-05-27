package ru.merenkov.diploma.domain;


import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record FuzzyNumbersHolder(
        int paramIndex,
        List<FuzzyNumber> fuzzyNumbers
) {
    @Builder
    public record FuzzyNumber(
            LocalDateTime dateTime,
            BigDecimal largestValue,
            BigDecimal value,
            BigDecimal smallestValue
    ) {
    }
}
