package ru.merenkov.diploma.domain;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ValuesDataHolder(
        int paramIndex,
        String paramName,
        List<ValueData> values
) {
    @Builder
    public record ValueData(
            LocalDateTime dateTime,
            Double value
    ) {}
}
