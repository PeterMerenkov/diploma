package ru.merenkov.diploma.domain;

import java.util.List;

public record ConditionDataHolder(
        List<ParamTermSetIndexPair> paramTermSetIndexPairs,
//        List<Integer> paramIndexes,
//        List<Integer> termSetIndexes,
        String result
) {

    public record ParamTermSetIndexPair (
        Integer paramIndex,
        Integer termSetIndex
    ) {

    }
}
