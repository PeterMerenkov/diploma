package ru.merenkov.diploma.service;

import org.springframework.stereotype.Service;
import ru.merenkov.diploma.domain.FuzzyNumber;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalculateService {


    public static final int DELTA_SMALLEST = 1;
    public static final int DELTA_LARGEST = 2;

    public List<List<FuzzyNumber>> convertToFuzzyNumberList(List<List<Double>> rawData) {
        List<List<FuzzyNumber>> result = new ArrayList<>();
        for (List<Double> row : rawData) {
            List<FuzzyNumber> fuzzyNumbers = new ArrayList<>();
            for (Double value : row) {
                fuzzyNumbers.add(
                        new FuzzyNumber(
                                value - DELTA_SMALLEST,
                                value,
                                value + DELTA_LARGEST
                        )
                );
            }
            result.add(fuzzyNumbers);
        }
        return result;
    }
}
