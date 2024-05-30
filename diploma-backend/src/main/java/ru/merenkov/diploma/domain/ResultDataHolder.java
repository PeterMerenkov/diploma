package ru.merenkov.diploma.domain;

import lombok.Builder;

import java.util.List;

@Builder
public record ResultDataHolder(
        List<ConditionDataHolder> conditionDataHolder,
        List<FuzzNumTermSetHolder> fuzzNumTermSetHolders
) {
}
