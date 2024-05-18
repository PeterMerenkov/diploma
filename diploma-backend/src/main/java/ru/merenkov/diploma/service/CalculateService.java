package ru.merenkov.diploma.service;

import org.springframework.stereotype.Service;
import ru.merenkov.diploma.domain.DeltaDataHolder;
import ru.merenkov.diploma.domain.FuzzyNumbersHolder;
import ru.merenkov.diploma.domain.ResultDataHolder;
import ru.merenkov.diploma.domain.TermSetsDataHolder;
import ru.merenkov.diploma.domain.ValuesDataHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
            for (Double value : valuesDataHolder.values()) {
                fuzzyNumbersHolder.fuzzyNumbers().add(
                        FuzzyNumbersHolder.FuzzyNumber.builder()
                                .largestValue(new BigDecimal(value)
                                        .subtract(BigDecimal.valueOf(deltaDataHolder.delta1())))
                                .value(new BigDecimal(value))
                                .largestValue(new BigDecimal(value)
                                        .add(BigDecimal.valueOf(deltaDataHolder.delta2())))
                                .build()
                );
            }
            result.add(fuzzyNumbersHolder);
        }

        return result;
    }

    public List<ResultDataHolder> calculateResultData(
            List<FuzzyNumbersHolder> fuzzyNumbersHolders,
            List<TermSetsDataHolder> termSetsDataHolders
    ) {
        List<ResultDataHolder> resultDataHolders = new ArrayList<>();
        for (int i = 0; i < fuzzyNumbersHolders.size(); i++) {
            FuzzyNumbersHolder fuzzyNumbersHolder = fuzzyNumbersHolders.get(i);
            TermSetsDataHolder termSetsDataHolder = termSetsDataHolders.get(i);
            ResultDataHolder resultDataHolder = ResultDataHolder.builder()
                    .paramIndex(i)
                    .results(new ArrayList<>())
                    .build();
            for (FuzzyNumbersHolder.FuzzyNumber fuzzyNumber : fuzzyNumbersHolder.fuzzyNumbers()) {
                List<TermSetsDataHolder.TermSetData> crossedTermSets = termSetsDataHolder.termSets().stream()
                        .filter(
                                termSet -> termSet.smallestValue() <= fuzzyNumber.largestValue().doubleValue()
                                        || termSet.largestValue() <= fuzzyNumber.smallestValue().doubleValue()
                        )
                        .toList();
                TermSetsDataHolder.TermSetData suitableTermSet = crossedTermSets.stream()
                        .reduce((termSet1, termSet2) ->
                                termSet1.importanceWeight() > termSet2.importanceWeight()
                                        ? termSet1
                                        : termSet2
                        ).orElseThrow(() -> new RuntimeException("No term set found"));

                resultDataHolder.results().add(
                        ResultDataHolder.ResultData.builder()
                                .fuzzyNumber(fuzzyNumber)
                                .termSet(suitableTermSet)
                                .build()
                );
            }
            resultDataHolders.add(resultDataHolder);
        }
        
        return resultDataHolders;
    }
}
