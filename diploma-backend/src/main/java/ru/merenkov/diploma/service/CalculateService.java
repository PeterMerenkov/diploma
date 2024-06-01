package ru.merenkov.diploma.service;

import javafx.util.Pair;
import org.springframework.stereotype.Service;
import ru.merenkov.diploma.domain.ConditionDataHolder;
import ru.merenkov.diploma.domain.DeltaDataHolder;
import ru.merenkov.diploma.domain.FuzzNumTermSetHolder;
import ru.merenkov.diploma.domain.FuzzyNumbersHolder;
import ru.merenkov.diploma.domain.ResultDataHolder;
import ru.merenkov.diploma.domain.TermSetsDataHolder;
import ru.merenkov.diploma.domain.ValuesDataHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CalculateService {

    public List<FuzzyNumbersHolder> calculateFuzzyNumbers(
            List<ValuesDataHolder> valuesDataHolderList,
            List<DeltaDataHolder> deltaDataHoldersList
    ) {
        List<FuzzyNumbersHolder> result = new ArrayList<>();
        for (int i = 0; i < valuesDataHolderList.size(); i++) {
            ValuesDataHolder valuesDataHolder = valuesDataHolderList.get(i);
            DeltaDataHolder deltaDataHolder = deltaDataHoldersList.get(i);
            FuzzyNumbersHolder fuzzyNumbersHolder = FuzzyNumbersHolder.builder()
                    .paramIndex(i)
                    .fuzzyNumbers(new ArrayList<>())
                    .build();
            for (ValuesDataHolder.ValueData valueData : valuesDataHolder.values()) {
                fuzzyNumbersHolder.fuzzyNumbers().add(
                        FuzzyNumbersHolder.FuzzyNumber.builder()
                                .dateTime(valueData.dateTime())
                                .smallestValue(BigDecimal.valueOf(valueData.value())
                                        .subtract(BigDecimal.valueOf(deltaDataHolder.delta1())))
                                .value(BigDecimal.valueOf(valueData.value()))
                                .largestValue(BigDecimal.valueOf(valueData.value())
                                        .add(BigDecimal.valueOf(deltaDataHolder.delta2())))
                                .build()
                );
            }
            result.add(fuzzyNumbersHolder);
        }

        return result;
    }

    public List<FuzzNumTermSetHolder> getFuzzNumTermSetPairs(
            List<FuzzyNumbersHolder> fuzzyNumbersHolders,
            List<TermSetsDataHolder> termSetsDataHolders
    ) {
        List<FuzzNumTermSetHolder> fuzzNumTermSetHolders = new ArrayList<>();
        for (int i = 0; i < fuzzyNumbersHolders.size(); i++) {
            FuzzyNumbersHolder fuzzyNumbersHolder = fuzzyNumbersHolders.get(i);
            TermSetsDataHolder termSetsDataHolder = termSetsDataHolders.get(i);
            FuzzNumTermSetHolder fuzzNumTermSetHolder = FuzzNumTermSetHolder.builder()
                    .paramIndex(i)
                    .results(new ArrayList<>())
                    .build();
            for (FuzzyNumbersHolder.FuzzyNumber fuzzyNumber : fuzzyNumbersHolder.fuzzyNumbers()) {
                List<TermSetsDataHolder.TermSetData> crossedTermSets = termSetsDataHolder.termSets().stream()
                        .filter(
                                termSet -> (termSet.smallestValue() <= fuzzyNumber.value().doubleValue()
                                        && termSet.largestValue() >= fuzzyNumber.value().doubleValue())
                                        || (termSet.smallestValue() <= fuzzyNumber.largestValue().doubleValue()
                                        && termSet.largestValue() >= fuzzyNumber.largestValue().doubleValue())
                        )
                        .toList();
                TermSetsDataHolder.TermSetData suitableTermSet = crossedTermSets.stream()
                        .reduce(
                                (termSet1, termSet2) ->
                                        termSet1.importanceWeight() > termSet2.importanceWeight()
                                                ? termSet1
                                                : termSet2
                        ).orElseGet(
                                () -> TermSetsDataHolder.TermSetData.builder()
                                        .name("вне терм-множества")
                                        .index(10)
                                        .importanceWeight(0.0)
                                        .smallestValue(0.0)
                                        .largestValue(0.0)
                                        .build()
                        );

                fuzzNumTermSetHolder.results().add(
                        FuzzNumTermSetHolder.ResultData.builder()
                                .fuzzyNumber(fuzzyNumber)
                                .termSet(suitableTermSet)
                                .build()
                );
            }
            fuzzNumTermSetHolders.add(fuzzNumTermSetHolder);
        }
        
        return fuzzNumTermSetHolders;
    }

    public ResultDataHolder calculateResult(
            List<FuzzNumTermSetHolder> fuzzNumTermSetHolders,
            List<ConditionDataHolder> conditions
    ) {

        Map<LocalDateTime, List<Pair<Integer, FuzzNumTermSetHolder.ResultData>>> result = new HashMap<>();
        for (FuzzNumTermSetHolder fuzzNumTermSetHolder : fuzzNumTermSetHolders) {
            for (FuzzNumTermSetHolder.ResultData resultData : fuzzNumTermSetHolder.results()) {
                result.computeIfAbsent(resultData.fuzzyNumber().dateTime(),
                                k -> new ArrayList<>())
                        .add(new Pair<>(fuzzNumTermSetHolder.paramIndex(), resultData));
            }
        }

        result = result.entrySet().stream()
                .filter(entry -> {
                    return conditions.stream()
                            .map(ConditionDataHolder::paramTermSetIndexPairs)
                            .allMatch(indexPairs -> {
                                List<Pair<Integer, FuzzNumTermSetHolder.ResultData>> collect = new ArrayList<>();
                                for (Pair<Integer, FuzzNumTermSetHolder.ResultData> indexToDataPair : entry.getValue()) {
                                    if (indexPairs.contains(new ConditionDataHolder.ParamTermSetIndexPair(indexToDataPair.getKey(), indexToDataPair.getValue().termSet().index()))) {
                                        collect.add(indexToDataPair);
                                    }
                                }

                                List<Integer> conditionTermSetIndexListSorted = indexPairs.stream().map(x -> x.termSetIndex()).sorted().toList();
                                List<Integer> valueTermSetIndexListSorted = collect.stream().map(x -> x.getValue().termSet().index()).sorted().toList();

                                return conditionTermSetIndexListSorted.equals(valueTermSetIndexListSorted);
                            });
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new ResultDataHolder(conditions, result);
    }
}
