package ru.merenkov.diploma.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import ru.merenkov.diploma.domain.ValuesDataHolder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
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
}