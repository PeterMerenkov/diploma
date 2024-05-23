package ru.merenkov.diploma.controller;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    public ResponseEntity<ByteArrayResource> getExcelContent() throws IOException {
        ByteArrayResource resource = excelService.convertToResultExcelFile(
                calculateService.calculateResultData(
                        calculateService.calculateFuzzyNumbers(
                                excelService.extractValuesDataFromFile(),
                                excelService.extractDeltasDataFromFile()
                        ),
                        excelService.extractTermSetsDataFromFile()
                ));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=results.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/raw-values-file")
    public ResponseEntity<ByteArrayResource> getRawValuesFile() throws IOException {
        ByteArrayResource resource = excelService.getRawValuesFile();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=raw-values.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/deltas-file")
    public ResponseEntity<ByteArrayResource> getDeltasFile() throws IOException {
        ByteArrayResource resource = excelService.getDeltasFile();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=deltas.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/term-sets-file")
    public ResponseEntity<ByteArrayResource> getTermSetsFile() throws IOException {
        ByteArrayResource resource = excelService.getTermSetsFile();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=term-sets.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @PostMapping("/update-raw-values-file")
    public void updateRawValuesFile(
            @RequestParam("rawValuesFile") MultipartFile rawValuesFile
    ) throws IOException {
        excelService.updateRawValuesFile(rawValuesFile);
    }

    @PostMapping("/update-deltas-file")
    public void updateDeltasFile(
            @RequestParam("deltasFile") MultipartFile deltasFile
    ) throws IOException {
        excelService.updateDeltasFile(deltasFile);
    }

    @PostMapping("/update-term-sets-file")
    public void updateTermSetsFile(
            @RequestParam("termSetsFile") MultipartFile termSetsFile
    ) throws IOException {
        excelService.updateTermSetsFile(termSetsFile);
    }
}
