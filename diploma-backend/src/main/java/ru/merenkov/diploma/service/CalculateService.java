package ru.merenkov.diploma.service;

import org.springframework.stereotype.Service;
import ru.merenkov.diploma.domain.*;

import java.math.BigDecimal;
import java.util.*;
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

    public List<ResultDataHolder> calculateResult(
            List<FuzzNumTermSetHolder> fuzzNumTermSetHolders,
            ConditionDataHolder condition
    ) {
        ArrayList<ResultDataHolder> result = new ArrayList<>();
        condition.paramTermSetIndexPairs().forEach(pair -> {
            List<FuzzNumTermSetHolder> holders = new ArrayList<>();
            fuzzNumTermSetHolders.stream()
                    .filter(ft -> Objects.equals(ft.paramIndex(), pair.paramIndex()))
                    .forEach(ft -> {
                        List<FuzzNumTermSetHolder.ResultData> resultDataList = ft.results().stream()
                                .filter(rd -> Objects.equals(rd.termSet().index(), pair.termSetIndex()))
                                .collect(Collectors.toList());
                        holders.add(FuzzNumTermSetHolder.builder()
                                .paramIndex(ft.paramIndex())
                                .results(resultDataList)
                                .build());

                    });

            ResultDataHolder rd = ResultDataHolder.builder()
                    .conditionDataHolder(condition)
                    .fuzzNumTermSetHolders(holders)
                    .build();
            result.add(rd);
        });

        return result;
    }
}
