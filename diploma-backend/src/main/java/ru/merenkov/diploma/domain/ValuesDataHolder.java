package ru.merenkov.diploma.domain;

import lombok.Builder;

import java.util.List;

@Builder
public record ValuesDataHolder(
        int paramIndex,
        List<Double> values
) {
}
