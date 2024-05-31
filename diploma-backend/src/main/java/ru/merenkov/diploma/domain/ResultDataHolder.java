package ru.merenkov.diploma.domain;

import javafx.util.Pair;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
public record ResultDataHolder(
        List<ConditionDataHolder> conditionDataHolder,
        Map<LocalDateTime, List<Pair<Integer, FuzzNumTermSetHolder.ResultData>>> timeListMap
) {
}
