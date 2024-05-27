package ru.merenkov.diploma.domain;

import lombok.Builder;

import java.util.List;

@Builder
public record TermSetsDataHolder(
        int paramIndex,
        List<TermSetData> termSets
) {
    @Builder
    public record TermSetData(
            Integer index,
            String name,
            Double smallestValue,
            Double largestValue,
            Double importanceWeight
    ) {
    }
}