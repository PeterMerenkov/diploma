package ru.merenkov.diploma.domain;

import lombok.Builder;

import java.util.List;

@Builder
public record ResultDataHolder(
        ConditionDataHolder conditionDataHolder,
        List<FuzzNumTermSetHolder> fuzzNumTermSetHolders
) {
}
