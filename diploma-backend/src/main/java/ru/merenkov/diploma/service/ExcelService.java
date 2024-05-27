package ru.merenkov.diploma.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.merenkov.diploma.domain.DeltaDataHolder;
import ru.merenkov.diploma.domain.FuzzNumTermSetHolder;
import ru.merenkov.diploma.domain.TermSetsDataHolder;
import ru.merenkov.diploma.domain.ValuesDataHolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelService {

    public static final int ROWS_SKIP_COUNT = 1;
    public static final int RAW_VALUES_COLUMNS_SKIP_COUNT = 2;
    public static final int DELTAS_COLUMNS_SKIP_COUNT = 1;

    private final ResourceLoader resourceLoader;

    public List<ValuesDataHolder> extractValuesDataFromFile() throws IOException {
        Resource rawValuesFile = resourceLoader.getResource("classpath:file/raw_values.xlsx");
        try (Workbook workbook = new XSSFWorkbook(rawValuesFile.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rows = sheet.iterator();
            skipRows(ROWS_SKIP_COUNT, rows);
            Map<Integer, List<Double>> paramIndexToValueListMap = new LinkedHashMap<>();
            while (rows.hasNext()) {
                Row row = rows.next();
                Iterator<Cell> cells = row.iterator();
                skipColumns(RAW_VALUES_COLUMNS_SKIP_COUNT, cells);

                while (cells.hasNext()) {
                    Cell cell = cells.next();
                    paramIndexToValueListMap
                            .computeIfAbsent(
                                    cell.getColumnIndex() - RAW_VALUES_COLUMNS_SKIP_COUNT,
                                    k -> new ArrayList<>()
                            )
                            .add(cell.getNumericCellValue());
                }
            }

            return paramIndexToValueListMap.entrySet().stream()
                    .map(entry -> ValuesDataHolder.builder()
                            .paramIndex(entry.getKey())
                            .values(entry.getValue())
                            .build())
                    .toList();
        }
    }

    public List<DeltaDataHolder> extractDeltasDataFromFile() throws IOException {
        Resource deltasFile = resourceLoader.getResource("classpath:file/deltas.xlsx");
        try (Workbook workbook = new XSSFWorkbook(deltasFile.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rows = sheet.iterator();
            skipRows(ROWS_SKIP_COUNT, rows);
            ArrayList<DeltaDataHolder> deltasListList = new ArrayList<>();
            while (rows.hasNext()) {
                Row row = rows.next();
                Iterator<Cell> cells = row.iterator();
                skipColumns(DELTAS_COLUMNS_SKIP_COUNT, cells);

                Cell deltaCell1 = cells.next();
                Cell deltaCell2 = cells.next();
                deltasListList.add(DeltaDataHolder.builder()
                        .paramIndex(row.getRowNum() - ROWS_SKIP_COUNT)
                        .delta1(deltaCell1.getNumericCellValue())
                        .delta2(deltaCell2.getNumericCellValue())
                        .build());
            }

            return deltasListList;
        }
    }

    public List<TermSetsDataHolder> extractTermSetsDataFromFile() throws IOException {
        Resource termSetsFile = resourceLoader.getResource("classpath:file/term_sets.xlsx");
        try (Workbook workbook = new XSSFWorkbook(termSetsFile.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rows = sheet.iterator();
            skipRows(ROWS_SKIP_COUNT, rows);
            List<TermSetsDataHolder> termSetsDataHolders = new ArrayList<>();
            while (rows.hasNext()) {
                Row row = rows.next();
                Iterator<Cell> cells = row.iterator();
                skipColumns(DELTAS_COLUMNS_SKIP_COUNT, cells);

                TermSetsDataHolder termSetsDataHolder = TermSetsDataHolder.builder()
                        .paramIndex(row.getRowNum() - ROWS_SKIP_COUNT)
                        .termSets(new ArrayList<>())
                        .build();
                while (cells.hasNext()) {
                    Cell smallestValueCell = cells.next();
                    Cell largestValueCell = cells.next();
                    Cell nameCell = cells.next();
                    Cell importanceWeightCell = cells.next();

                    termSetsDataHolder.termSets().add(
                            TermSetsDataHolder.TermSetData.builder()
                                    .name(nameCell.getStringCellValue())
                                    .smallestValue(smallestValueCell.getNumericCellValue())
                                    .largestValue(largestValueCell.getNumericCellValue())
                                    .importanceWeight(importanceWeightCell.getNumericCellValue())
                                    .build());
                }

                termSetsDataHolders.add(termSetsDataHolder);
            }

            return termSetsDataHolders;
        }
    }

    public ByteArrayResource convertToResultExcelFile(List<FuzzNumTermSetHolder> calculateResultData) throws IOException {
        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.createSheet("Results");

            int rowIndex = 0;
            for (FuzzNumTermSetHolder fuzzNumTermSetHolder : calculateResultData) {
                for (FuzzNumTermSetHolder.ResultData resultData : fuzzNumTermSetHolder.results()) {
                    Row row = sheet.createRow(rowIndex++);

                    row.createCell(0).setCellValue(fuzzNumTermSetHolder.paramIndex());
                    row.createCell(1).setCellValue(resultData.fuzzyNumber().value().doubleValue());
                    row.createCell(2).setCellValue(resultData.termSet().name());
                    row.createCell(3).setCellValue(resultData.termSet().importanceWeight());
                }
            }

            workbook.write(outputStream);

            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

    public ByteArrayResource getRawValuesFile() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:file/raw_values.xlsx");
        byte[] data = Files.readAllBytes(resource.getFile().toPath());
        return new ByteArrayResource(data);
    }

    public ByteArrayResource getDeltasFile() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:file/deltas.xlsx");
        byte[] data = Files.readAllBytes(resource.getFile().toPath());
        return new ByteArrayResource(data);
    }

    public ByteArrayResource getTermSetsFile() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:file/term_sets.xlsx");
        byte[] data = Files.readAllBytes(resource.getFile().toPath());
        return new ByteArrayResource(data);
    }

    public void updateRawValuesFile(MultipartFile rawValuesFile) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:file/raw_values.xlsx");
        Files.write(resource.getFile().toPath(), rawValuesFile.getBytes());
    }

    public void updateDeltasFile(MultipartFile deltasFile) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:file/deltas.xlsx");
        Files.write(resource.getFile().toPath(), deltasFile.getBytes());
    }

    public void updateTermSetsFile(MultipartFile termSetsFile) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:file/term_sets.xlsx");
        Files.write(resource.getFile().toPath(), termSetsFile.getBytes());
    }

    private static void skipRows(int skipCount, Iterator<Row> rows) {
        for (int i = 0; i < skipCount; i++) {
            rows.next();
        }
    }

    private static void skipColumns(int skipCount, Iterator<Cell> cells) {
        for (int i = 0; i < skipCount; i++) {
            cells.next();
        }
    }
}
