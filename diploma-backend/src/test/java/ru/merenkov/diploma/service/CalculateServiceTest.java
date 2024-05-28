package ru.merenkov.diploma.service;

import org.junit.jupiter.api.Test;
import ru.merenkov.diploma.domain.DeltaDataHolder;
import ru.merenkov.diploma.domain.FuzzyNumbersHolder;
import ru.merenkov.diploma.domain.ValuesDataHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CalculateServiceTest {

    @Test
    void testCalculateFuzzyNumbers() {
        CalculateService calculateService = new CalculateService();

        ValuesDataHolder valuesDataHolder = ValuesDataHolder.builder()
                .paramIndex(0)
                .paramName("testParam")
                .values(List.of(
                        ValuesDataHolder.ValueData.builder()
                                .dateTime(LocalDateTime.now())
                                .value(5.0)
                                .build(),
                        ValuesDataHolder.ValueData.builder()
                                .dateTime(LocalDateTime.now())
                                .value(10.0)
                                .build(),
                        ValuesDataHolder.ValueData.builder()
                                .dateTime(LocalDateTime.now())
                                .value(20.0)
                                .build()
                ))
                .build();
        DeltaDataHolder deltaDataHolder = DeltaDataHolder.builder()
                .paramIndex(0)
                .delta1(1.0)
                .delta2(2.0)
                .build();

        List<ValuesDataHolder> valuesDataHolderList = List.of(valuesDataHolder);
        List<DeltaDataHolder> deltaDataHolderList = List.of(deltaDataHolder);

        List<FuzzyNumbersHolder> result = calculateService.calculateFuzzyNumbers(valuesDataHolderList, deltaDataHolderList);

        assertNotNull(result);
        assertEquals(1, result.size());
        FuzzyNumbersHolder fuzzyNumbersHolder = result.get(0);
        assertEquals(0, fuzzyNumbersHolder.paramIndex());
        assertEquals(3, fuzzyNumbersHolder.fuzzyNumbers().size());
        assertEquals(4.0, fuzzyNumbersHolder.fuzzyNumbers().get(0).smallestValue().doubleValue());
        assertEquals(5.0, fuzzyNumbersHolder.fuzzyNumbers().get(0).value().doubleValue());
        assertEquals(7.0, fuzzyNumbersHolder.fuzzyNumbers().get(0).largestValue().doubleValue());
    }

    @Test
    void getFuzzNumTermSetPairs() {
    }

    @Test
    void calculateResult() {
    }
}