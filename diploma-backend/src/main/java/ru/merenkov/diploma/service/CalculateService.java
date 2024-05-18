package ru.merenkov.diploma.service;

import org.springframework.stereotype.Service;
import ru.merenkov.diploma.domain.DeltaHolder;
import ru.merenkov.diploma.domain.FuzzyNumber;
import ru.merenkov.diploma.domain.ResultDataHolder;
import ru.merenkov.diploma.domain.TermsDataHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CalculateService {

    public List<List<FuzzyNumber>> convertToFuzzyNumberList(
            Map<Integer, List<Double>> paramIndexToValueListMap, 
            List<DeltaHolder> deltasList
    ) {
        List<List<FuzzyNumber>> result = new ArrayList<>();
        for (int i = 0; i < paramIndexToValueListMap.size(); i++) {
            List<Double> row = paramIndexToValueListMap.get(i);
            List<FuzzyNumber> fuzzyNumbers = new ArrayList<>();
            for (Double value : row) {
                fuzzyNumbers.add(
                        new FuzzyNumber(
                                new BigDecimal(value).subtract(BigDecimal.valueOf(deltasList.get(i).delta1())),
                                new BigDecimal(value),
                                new BigDecimal(value).add(BigDecimal.valueOf(deltasList.get(i).delta2()))
                        )
                );
            }
            result.add(fuzzyNumbers);
        }
        return result;
    }

    public List<List<ResultDataHolder>> convertToResultData(
            List<List<FuzzyNumber>> fuzzyNumberListList, 
            List<List<TermsDataHolder>> termsDataListList
    ) {
        List<List<ResultDataHolder>> result = new ArrayList<>();
        for (int i = 0; i < fuzzyNumberListList.size(); i++) {
            List<FuzzyNumber> fuzzyNumberList = fuzzyNumberListList.get(i);
            List<TermsDataHolder> termsDataList = termsDataListList.get(i);
            List<ResultDataHolder> resultDataList = new ArrayList<>();
            for (FuzzyNumber fuzzyNumber : fuzzyNumberList) {
                Optional<TermsDataHolder> suitableTermData = termsDataList.stream()
                        .filter(termsData -> termsData.smallest() <= fuzzyNumber.smallestValue().doubleValue() &&
                                termsData.largest() >= fuzzyNumber.largestValue().doubleValue())
                        .findFirst();

                resultDataList.add(new ResultDataHolder(
                        suitableTermData.map(TermsDataHolder::paramName).orElse(null),
                        suitableTermData.map(TermsDataHolder::termSetName).orElse(null),
                        suitableTermData.map(TermsDataHolder::importanceWeight).orElse(null)
                ));
            }
            result.add(resultDataList);
        }
        
        return result;
    }
}
