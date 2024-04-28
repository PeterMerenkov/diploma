package ru.merenkov.diploma.service;

import org.springframework.stereotype.Service;
import ru.merenkov.diploma.domain.DeltaHolder;
import ru.merenkov.diploma.domain.FuzzyNumber;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CalculateService {

    public List<List<FuzzyNumber>> convertToFuzzyNumberList(Map<Integer, List<Double>> paramIndexToValueListMap, List<DeltaHolder> deltasList) {
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
}
