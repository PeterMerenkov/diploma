package ru.merenkov.diploma.domain;

import java.util.List;

public record ConditionDataHolder(
        List<ParamTermSetIndexPair> paramTermSetIndexPairs,
        String result
) {

    public record ParamTermSetIndexPair (
        Integer paramIndex,
        Integer termSetIndex
    ) {
    }
}
