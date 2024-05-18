package ru.merenkov.diploma.domain;

import lombok.Builder;

@Builder
public record DeltaDataHolder(
        int paramIndex,
        Double delta1,
        Double delta2
) {
}