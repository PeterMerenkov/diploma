package ru.merenkov.diploma.domain;

import lombok.Builder;

import java.util.List;

@Builder
public record ResultDataHolder(
        int paramIndex,
        List<ResultData> results
) {
    @Builder
    public record ResultData(
            FuzzyNumbersHolder.FuzzyNumber fuzzyNumber,
            TermSetsDataHolder.TermSetData termSet
    ) {
    }
}
