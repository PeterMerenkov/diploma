package ru.merenkov.diploma.service;

import org.junit.jupiter.api.Test;
import ru.merenkov.diploma.domain.DeltaDataHolder;
import ru.merenkov.diploma.domain.FuzzNumTermSetHolder;
import ru.merenkov.diploma.domain.FuzzyNumbersHolder;
import ru.merenkov.diploma.domain.TermSetsDataHolder;
import ru.merenkov.diploma.domain.ValuesDataHolder;

import java.math.BigDecimal;
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
    void testGetFuzzNumTermSetPairs() {
        CalculateService calculateService = new CalculateService();

        FuzzyNumbersHolder fuzzyNumbersHolder = FuzzyNumbersHolder.builder()
                .paramIndex(0)
                .fuzzyNumbers(List.of(
                        FuzzyNumbersHolder.FuzzyNumber.builder()
                                .dateTime(LocalDateTime.now())
                                .smallestValue(BigDecimal.valueOf(4.0))
                                .value(BigDecimal.valueOf(5.0))
                                .largestValue(BigDecimal.valueOf(7.0))
                                .build()
                ))
                .build();

        TermSetsDataHolder termSetsDataHolder = TermSetsDataHolder.builder()
                .paramIndex(0)
                .termSets(List.of(TermSetsDataHolder.TermSetData.builder()
                                .index(0)
                                .name("testTermSet1")
                                .smallestValue(0.0)
                                .largestValue(3.0)
                                .importanceWeight(1.0)
                                .build(),
                        TermSetsDataHolder.TermSetData.builder()
                                .index(1)
                                .name("testTermSet2")
                                .smallestValue(3.0)
                                .largestValue(6.0)
                                .importanceWeight(3.0)
                                .build(),
                        TermSetsDataHolder.TermSetData.builder()
                                .index(2)
                                .name("testTermSet3")
                                .smallestValue(6.0)
                                .largestValue(9.0)
                                .importanceWeight(6.0)
                                .build()
                ))
                .build();

        List<FuzzyNumbersHolder> fuzzyNumbersHolderList = List.of(fuzzyNumbersHolder);
        List<TermSetsDataHolder> termSetsDataHolderList = List.of(termSetsDataHolder);

        // Act
        List<FuzzNumTermSetHolder> result = calculateService.getFuzzNumTermSetPairs(fuzzyNumbersHolderList, termSetsDataHolderList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        FuzzNumTermSetHolder fuzzNumTermSetHolder = result.get(0);
        assertEquals(0, fuzzNumTermSetHolder.paramIndex());
        assertEquals(1, fuzzNumTermSetHolder.results().size());
        FuzzNumTermSetHolder.ResultData resultData = fuzzNumTermSetHolder.results().get(0);
        assertEquals(fuzzyNumbersHolder.fuzzyNumbers().get(0), resultData.fuzzyNumber());
        assertEquals(termSetsDataHolder.termSets().get(2), resultData.termSet());
    }

    @Test
    void calculateResult() {
    }
}