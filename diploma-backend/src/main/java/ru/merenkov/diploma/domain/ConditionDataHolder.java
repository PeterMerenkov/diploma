package ru.merenkov.diploma.domain;

import lombok.Builder;

import java.util.List;

@Builder
public record ConditionDataHolder(
        List<ParamTermSetIndexPair> paramTermSetIndexPairs,
        String result
) {
    @Builder
    public record ParamTermSetIndexPair (
        Integer paramIndex,
        Integer termSetIndex
    ) {
    }
}
