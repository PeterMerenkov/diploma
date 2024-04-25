package ru.merenkov.diploma.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.merenkov.diploma.domain.FuzzyNumber;
import ru.merenkov.diploma.service.CalculateService;
import ru.merenkov.diploma.service.ExcelService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/excel")
@CrossOrigin(origins = "http://localhost:8080")
@AllArgsConstructor
public class MainController {
    private final ExcelService excelService;
    private final CalculateService calculateService;

    @PostMapping
    public List<List<FuzzyNumber>> getExcelContent(@RequestParam("file") MultipartFile file) throws IOException {
        return calculateService.convertToFuzzyNumberList(
                excelService.getRawDataFromFile(file)
        );
    }
}
