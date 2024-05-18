package ru.merenkov.diploma.controller;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.merenkov.diploma.service.CalculateService;
import ru.merenkov.diploma.service.ExcelService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/excel")
@CrossOrigin(origins = "http://localhost:8080")
@AllArgsConstructor
public class MainController {
    private final ExcelService excelService;
    private final CalculateService calculateService;

    @PostMapping
    public ResponseEntity<ByteArrayResource> getExcelContent(
            @RequestParam("rawValuesFile") MultipartFile rawValuesFile,
            @RequestParam("deltasFile") MultipartFile deltasFile,
            @RequestParam("termSetsFile") MultipartFile termSetsFile
    ) throws IOException {
        ByteArrayResource resource = excelService.resultToExcelFile(calculateService.calculateResultData(
                calculateService.calculateFuzzyNumbers(
                        excelService.extractValuesDataFromFile(rawValuesFile),
                        excelService.extractDeltasDataFromFile(deltasFile)
                ),
                excelService.extractTermSetsDataFromFile(termSetsFile)
        ));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=results.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
