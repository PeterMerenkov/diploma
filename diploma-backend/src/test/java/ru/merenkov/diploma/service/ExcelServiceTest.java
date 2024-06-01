package ru.merenkov.diploma.service;

import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import ru.merenkov.diploma.domain.ConditionDataHolder;
import ru.merenkov.diploma.domain.DeltaDataHolder;
import ru.merenkov.diploma.domain.FuzzNumTermSetHolder;
import ru.merenkov.diploma.domain.FuzzyNumbersHolder;
import ru.merenkov.diploma.domain.ResultDataHolder;
import ru.merenkov.diploma.domain.TermSetsDataHolder;
import ru.merenkov.diploma.domain.ValuesDataHolder;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcelServiceTest {

    @Mock
    private ResourceLoader resourceLoaderMocked;

    @InjectMocks
    private ExcelService excelService;

    @Test
    void extractValuesDataFromFile() throws IOException {
        Resource mockResource = Mockito.mock(Resource.class);
        when(resourceLoaderMocked.getResource(anyString())).thenReturn(mockResource);

        Path path = Paths.get("src/test/resources/file/raw_values.xlsx");
        try (InputStream fileInputStream = Files.newInputStream(path)) {
            when(mockResource.getInputStream()).thenReturn(fileInputStream);

            List<ValuesDataHolder> result = excelService.extractValuesDataFromFile();

            for (ValuesDataHolder valuesDataHolder : result) {
                assertNotNull(valuesDataHolder.paramName());
                for (ValuesDataHolder.ValueData valueData : valuesDataHolder.values()) {
                    assertNotNull(valueData.dateTime());
                    assertNotNull(valueData.value());
                }
            }
        }
    }

    @Test
    void extractDeltasDataFromFile() throws IOException {
        Resource mockResource = Mockito.mock(Resource.class);
        when(resourceLoaderMocked.getResource(anyString())).thenReturn(mockResource);

        Path path = Paths.get("src/test/resources/file/deltas.xlsx");
        try (InputStream fileInputStream = Files.newInputStream(path)) {
            when(mockResource.getInputStream()).thenReturn(fileInputStream);

            List<DeltaDataHolder> result = excelService.extractDeltasDataFromFile();

            for (DeltaDataHolder valuesDataHolder : result) {
                assertNotNull(valuesDataHolder.delta1());
                assertNotNull(valuesDataHolder.delta2());
            }
        }
    }

    @Test
    void extractTermSetsDataFromFile() throws IOException {
        Resource mockResource = Mockito.mock(Resource.class);
        when(resourceLoaderMocked.getResource(anyString())).thenReturn(mockResource);

        Path path = Paths.get("src/test/resources/file/term_sets.xlsx");
        try (InputStream fileInputStream = Files.newInputStream(path)) {
            when(mockResource.getInputStream()).thenReturn(fileInputStream);

            List<TermSetsDataHolder> result = excelService.extractTermSetsDataFromFile();

            for (TermSetsDataHolder termSetsDataHolder : result) {
                for (TermSetsDataHolder.TermSetData termSetData : termSetsDataHolder.termSets()) {
                    assertNotNull(termSetData.index());
                    assertNotNull(termSetData.name());
                    assertNotNull(termSetData.importanceWeight());
                    assertNotNull(termSetData.smallestValue());
                    assertNotNull(termSetData.largestValue());
                }
            }
        }
    }

    @Test
    void extractConditionDataFromFile() throws IOException {
        Resource mockResource = Mockito.mock(Resource.class);
        when(resourceLoaderMocked.getResource(anyString())).thenReturn(mockResource);

        Path path = Paths.get("src/test/resources/file/conditions-test.xlsx");
        try (InputStream fileInputStream = Files.newInputStream(path)) {
            when(mockResource.getInputStream()).thenReturn(fileInputStream);

            List<ConditionDataHolder> result = excelService.extractConditionDataFromFile();

            for (ConditionDataHolder conditionDataHolder : result) {
                for (ConditionDataHolder.ParamTermSetIndexPair paramTermSetIndexPair : conditionDataHolder.paramTermSetIndexPairs()) {
                    assertNotNull(paramTermSetIndexPair.paramIndex());
                    assertNotNull(paramTermSetIndexPair.termSetIndex());
                }
                assertNotNull(conditionDataHolder.result());
            }
        }
    }

    @Test
    void convertToResultExcelFile() throws IOException {

        List<FuzzNumTermSetHolder> fuzzNumTermSetHolderList = getTestFuzzNumTermSetHolders();
        List<ConditionDataHolder> conditionDataHolderList = getTestConditionDataHolders();

        CalculateService calculateService = new CalculateService();
        ResultDataHolder resultDataHolder = calculateService.calculateResult(fuzzNumTermSetHolderList, conditionDataHolderList);

        ByteArrayResource result = excelService.convertToResultExcelFile(resultDataHolder);
        try (InputStream inputStream = result.getInputStream()) {
            Path outputPath = Paths.get("src/test/resources/file/conditions-results.xlsx");
            Files.write(outputPath, IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Assert
        assertNotNull(result);
    }

    private static List<FuzzNumTermSetHolder> getTestFuzzNumTermSetHolders() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        FuzzNumTermSetHolder fuzzNumTermSetHolder1 = FuzzNumTermSetHolder.builder()
                .paramIndex(0)
                .results(List.of(
                        FuzzNumTermSetHolder.ResultData.builder()
                                .fuzzyNumber(FuzzyNumbersHolder.FuzzyNumber.builder()
                                        .dateTime(nowDateTime)
                                        .smallestValue(BigDecimal.valueOf(4.0))
                                        .value(BigDecimal.valueOf(5.0))
                                        .largestValue(BigDecimal.valueOf(7.0))
                                        .build())
                                .termSet(TermSetsDataHolder.TermSetData.builder()
                                        .index(0)
                                        .name("testTermSet1")
                                        .smallestValue(3.0)
                                        .largestValue(6.0)
                                        .importanceWeight(1.0)
                                        .build())
                                .build(),
                        FuzzNumTermSetHolder.ResultData.builder()
                                .fuzzyNumber(FuzzyNumbersHolder.FuzzyNumber.builder()
                                        .dateTime(nowDateTime.plusSeconds(30))
                                        .smallestValue(BigDecimal.valueOf(4.0))
                                        .value(BigDecimal.valueOf(5.0))
                                        .largestValue(BigDecimal.valueOf(7.0))
                                        .build())
                                .termSet(TermSetsDataHolder.TermSetData.builder()
                                        .index(1)
                                        .name("testTermSet1")
                                        .smallestValue(3.0)
                                        .largestValue(6.0)
                                        .importanceWeight(1.0)
                                        .build())
                                .build()
                ))
                .build();

        FuzzNumTermSetHolder fuzzNumTermSetHolder2 = FuzzNumTermSetHolder.builder()
                .paramIndex(1)
                .results(List.of(
                        FuzzNumTermSetHolder.ResultData.builder()
                                .fuzzyNumber(FuzzyNumbersHolder.FuzzyNumber.builder()
                                        .dateTime(nowDateTime)
                                        .smallestValue(BigDecimal.valueOf(4.0))
                                        .value(BigDecimal.valueOf(5.0))
                                        .largestValue(BigDecimal.valueOf(7.0))
                                        .build())
                                .termSet(TermSetsDataHolder.TermSetData.builder()
                                        .index(0)
                                        .name("testTermSet2")
                                        .smallestValue(3.0)
                                        .largestValue(6.0)
                                        .importanceWeight(1.0)
                                        .build())
                                .build(),
                        FuzzNumTermSetHolder.ResultData.builder()
                                .fuzzyNumber(FuzzyNumbersHolder.FuzzyNumber.builder()
                                        .dateTime(nowDateTime.plusSeconds(30))
                                        .smallestValue(BigDecimal.valueOf(4.0))
                                        .value(BigDecimal.valueOf(5.0))
                                        .largestValue(BigDecimal.valueOf(7.0))
                                        .build())
                                .termSet(TermSetsDataHolder.TermSetData.builder()
                                        .index(1)
                                        .name("testTermSet2")
                                        .smallestValue(3.0)
                                        .largestValue(6.0)
                                        .importanceWeight(1.0)
                                        .build())
                                .build()
                ))
                .build();

        FuzzNumTermSetHolder fuzzNumTermSetHolder3 = FuzzNumTermSetHolder.builder()
                .paramIndex(2)
                .results(List.of(
                        FuzzNumTermSetHolder.ResultData.builder()
                                .fuzzyNumber(FuzzyNumbersHolder.FuzzyNumber.builder()
                                        .dateTime(nowDateTime)
                                        .smallestValue(BigDecimal.valueOf(4.0))
                                        .value(BigDecimal.valueOf(5.0))
                                        .largestValue(BigDecimal.valueOf(7.0))
                                        .build())
                                .termSet(TermSetsDataHolder.TermSetData.builder()
                                        .index(0)
                                        .name("testTermSet3")
                                        .smallestValue(3.0)
                                        .largestValue(6.0)
                                        .importanceWeight(1.0)
                                        .build())
                                .build(),
                        FuzzNumTermSetHolder.ResultData.builder()
                                .fuzzyNumber(FuzzyNumbersHolder.FuzzyNumber.builder()
                                        .dateTime(nowDateTime.plusSeconds(30))
                                        .smallestValue(BigDecimal.valueOf(4.0))
                                        .value(BigDecimal.valueOf(5.0))
                                        .largestValue(BigDecimal.valueOf(7.0))
                                        .build())
                                .termSet(TermSetsDataHolder.TermSetData.builder()
                                        .index(1)
                                        .name("testTermSet3")
                                        .smallestValue(3.0)
                                        .largestValue(6.0)
                                        .importanceWeight(1.0)
                                        .build())
                                .build()
                ))
                .build();

        List<FuzzNumTermSetHolder> fuzzNumTermSetHolderList = List.of(fuzzNumTermSetHolder1, fuzzNumTermSetHolder2, fuzzNumTermSetHolder3);
        return fuzzNumTermSetHolderList;
    }

    private static List<ConditionDataHolder> getTestConditionDataHolders() {
        ConditionDataHolder conditionDataHolder1 = ConditionDataHolder.builder()
                .paramTermSetIndexPairs(List.of(
                        ConditionDataHolder.ParamTermSetIndexPair.builder()
                                .paramIndex(0)
                                .termSetIndex(1)
                                .build(),
                        ConditionDataHolder.ParamTermSetIndexPair.builder()
                                .paramIndex(1)
                                .termSetIndex(1)
                                .build(),
                        ConditionDataHolder.ParamTermSetIndexPair.builder()
                                .paramIndex(2)
                                .termSetIndex(1)
                                .build()
                ))
                .result("testResult1")
                .build();

        ConditionDataHolder conditionDataHolder2 = ConditionDataHolder.builder()
                .paramTermSetIndexPairs(List.of(
                        ConditionDataHolder.ParamTermSetIndexPair.builder()
                                .paramIndex(0)
                                .termSetIndex(1)
                                .build(),
                        ConditionDataHolder.ParamTermSetIndexPair.builder()
                                .paramIndex(1)
                                .termSetIndex(1)
                                .build()
                ))
                .result("testResult1")
                .build();

        return List.of(conditionDataHolder1, conditionDataHolder2);
    }

    @Test
    void updateConditionsFile() throws IOException {
        // Arrange
        Path path = Paths.get("src/test/resources/file/conditions-test.xlsx");
        try (InputStream content = Files.newInputStream(path)) {
            MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "conditions-test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", content);
            Resource mockResource = mock(Resource.class);
            when(resourceLoaderMocked.getResource(anyString())).thenReturn(mockResource);
            when(mockResource.getFile()).thenReturn(path.toFile());
            when(mockResource.getInputStream()).thenReturn(Files.newInputStream(path));

            // Act
            excelService.updateConditionsFile(mockMultipartFile);

            // Assert
            verify(mockResource, times(1)).getFile();
        }
    }
}