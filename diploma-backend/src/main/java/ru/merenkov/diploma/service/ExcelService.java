package ru.merenkov.diploma.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.util.Pair;
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
import ru.merenkov.diploma.domain.ConditionDataHolder;
import ru.merenkov.diploma.domain.DeltaDataHolder;
import ru.merenkov.diploma.domain.FuzzNumTermSetHolder;
import ru.merenkov.diploma.domain.ResultDataHolder;
import ru.merenkov.diploma.domain.TermSetsDataHolder;
import ru.merenkov.diploma.domain.ValuesDataHolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelService {

    public static final int ROWS_SKIP_COUNT = 1;
    public static final int RAW_VALUES_BEFORE_PARAM_NAMES_COLUMNS_COUNT = 2;
    public static final int DELTAS_COLUMNS_SKIP_COUNT = 1;

    private final ResourceLoader resourceLoader;
    private final CalculateService calculateService;

    public List<ValuesDataHolder> extractValuesDataFromFile() throws IOException {
        Resource rawValuesFile = resourceLoader.getResource("classpath:file/raw_values.xlsx");
        try (Workbook workbook = new XSSFWorkbook(rawValuesFile.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Map<Integer, Pair<String, List<ValuesDataHolder.ValueData>>> paramIndexToValueListMap = new LinkedHashMap<>();

            Iterator<Row> rows = sheet.iterator();
            Row titleRow = rows.next();
            Iterator<Cell> titleCells = titleRow.iterator();
            skipColumns(RAW_VALUES_BEFORE_PARAM_NAMES_COLUMNS_COUNT, titleCells);
            while (titleCells.hasNext()) {
                Cell cell = titleCells.next();
                String paramName = cell.getStringCellValue();
                paramIndexToValueListMap.put(cell.getColumnIndex() - RAW_VALUES_BEFORE_PARAM_NAMES_COLUMNS_COUNT,
                        Pair.create(paramName, new ArrayList<>()));
            }

            while (rows.hasNext()) {
                Row row = rows.next();
                Iterator<Cell> cells = row.iterator();

                Cell dateTimeCell = cells.next();
                LocalDateTime dateTimeCellValue = LocalDateTime.parse(dateTimeCell.getStringCellValue(),
                        DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm:ss"));
                skipColumns(1, cells);

                while (cells.hasNext()) {
                    Cell cell = cells.next();
                    paramIndexToValueListMap
                            .get(cell.getColumnIndex() - RAW_VALUES_BEFORE_PARAM_NAMES_COLUMNS_COUNT)
                            .getSecond()
                            .add(new ValuesDataHolder.ValueData(
                                    dateTimeCellValue,
                                    cell.getNumericCellValue()
                            ));
                }
            }

            return paramIndexToValueListMap.entrySet().stream()
                    .map(entry -> ValuesDataHolder.builder()
                            .paramIndex(entry.getKey())
                            .paramName(entry.getValue().getFirst())
                            .values(entry.getValue().getSecond())
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
                int termSetIndex = 0;
                while (cells.hasNext()) {
                    Cell smallestValueCell = cells.next();
                    Cell largestValueCell = cells.next();
                    Cell nameCell = cells.next();
                    Cell importanceWeightCell = cells.next();

                    termSetsDataHolder.termSets().add(
                            TermSetsDataHolder.TermSetData.builder()
                                    .index(termSetIndex)
                                    .name(nameCell.getStringCellValue())
                                    .importanceWeight(importanceWeightCell.getNumericCellValue())
                                    .smallestValue(smallestValueCell.getNumericCellValue())
                                    .largestValue(largestValueCell.getNumericCellValue())
                                    .build());
                    termSetIndex++;
                }

                termSetsDataHolders.add(termSetsDataHolder);
            }

            return termSetsDataHolders;
        }
    }

    public List<ConditionDataHolder> extractConditionDataFromFile() throws IOException {
        return extractConditionDataFromFile(resourceLoader.getResource("classpath:file/conditions.xlsx"));
    }

    public List<ConditionDataHolder> extractConditionDataFromFile(Resource conditionFile) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(conditionFile.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rows = sheet.iterator();
            List<ConditionDataHolder> conditionDataHolders = new ArrayList<>();
            while (rows.hasNext()) {
                Row row = rows.next();
                Iterator<Cell> paramIndexCells = row.iterator();
                skipColumns(DELTAS_COLUMNS_SKIP_COUNT, paramIndexCells);
                List<Integer> paramIndexes = new ArrayList<>();
                while (paramIndexCells.hasNext()) {
                    Cell cell = paramIndexCells.next();
                    paramIndexes.add((int) cell.getNumericCellValue());
                }

                row = rows.next();
                Iterator<Cell> termSetIndexCells = row.iterator();
                skipColumns(DELTAS_COLUMNS_SKIP_COUNT, termSetIndexCells);
                List<Integer> termSetIndexes = new ArrayList<>();
                while (termSetIndexCells.hasNext()) {
                    Cell cell = termSetIndexCells.next();
                    termSetIndexes.add((int) cell.getNumericCellValue());
                }

                List<ConditionDataHolder.ParamTermSetIndexPair> paramTermSetIndexPairs = new ArrayList<>();
                for (int i = 0; i < paramIndexes.size(); i++) {
                    paramTermSetIndexPairs.add(new ConditionDataHolder.ParamTermSetIndexPair(paramIndexes.get(i),
                            termSetIndexes.get(i)));
                }

                row = rows.next();
                Iterator<Cell> resultCells = row.iterator();
                skipColumns(DELTAS_COLUMNS_SKIP_COUNT, resultCells);
                Cell resultCell = resultCells.next();

                conditionDataHolders.add(new ConditionDataHolder(paramTermSetIndexPairs,
                        resultCell.getStringCellValue()));
            }

            return conditionDataHolders;
        }
    }

    public ByteArrayResource convertToResultExcelFile(ResultDataHolder resultDataHolder) throws IOException {
        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.createSheet("Results");

            int rowIndex = 0;
            Row row = sheet.createRow(rowIndex++);
            Cell cell = row.createCell(0);
            cell.setCellValue("Результат условия:");
            cell = row.createCell(1);
            cell.setCellValue("Пары значений в виде: Индекс параметра - Индекс терм-множества");

            for (ConditionDataHolder conditionDataHolder : resultDataHolder.conditionDataHolder()) {
                row = sheet.createRow(rowIndex++);
                cell = row.createCell(0);
                cell.setCellValue(conditionDataHolder.result());
                int cellIndex = 0;
                for (ConditionDataHolder.ParamTermSetIndexPair paramTermSetIndexPair : conditionDataHolder.paramTermSetIndexPairs()) {
                    cell = row.createCell(++cellIndex);
                    cell.setCellValue(String.format("%s - %s", paramTermSetIndexPair.paramIndex(), paramTermSetIndexPair.termSetIndex()));
                }
            }

            sheet.createRow(rowIndex++);

            row = sheet.createRow(rowIndex++);
            row = sheet.createRow(rowIndex++);
            cell = row.createCell(0);
            cell.setCellValue("Время");
            cell = row.createCell(1);
            cell.setCellValue("Параметры в виде: | индекс параметра | значение параметра | индекс терм-множества | название терм-множества | вес терм-множества |");
            for (Map.Entry<LocalDateTime, List<javafx.util.Pair<Integer, FuzzNumTermSetHolder.ResultData>>> localDateTimeListEntry : resultDataHolder.timeListMap().entrySet()) {
                row = sheet.createRow(rowIndex++);
                int cellIndex = 0;
                cell = row.createCell(cellIndex);
                cell.setCellValue(localDateTimeListEntry.getKey().format(DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm:ss")));

                for (javafx.util.Pair<Integer, FuzzNumTermSetHolder.ResultData> integerResultDataPair : localDateTimeListEntry.getValue()) {
                    cell = row.createCell(++cellIndex);
                    cell.setCellValue(integerResultDataPair.getKey());
                    cell = row.createCell(++cellIndex);
                    cell.setCellValue(String.format("[%s, %s, %s]",
                            integerResultDataPair.getValue().fuzzyNumber().smallestValue(),
                            integerResultDataPair.getValue().fuzzyNumber().value(),
                            integerResultDataPair.getValue().fuzzyNumber().largestValue()));
                    cell = row.createCell(++cellIndex);
                    cell.setCellValue(integerResultDataPair.getValue().termSet().index());
                    cell = row.createCell(++cellIndex);
                    cell.setCellValue(integerResultDataPair.getValue().termSet().name());
                    cell = row.createCell(++cellIndex);
                    cell.setCellValue(integerResultDataPair.getValue().termSet().importanceWeight());
                    row.createCell(++cellIndex);
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

    public ByteArrayResource getConditionsFile() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:file/conditions.xlsx");
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

    public void updateConditionsFile(MultipartFile conditionsFile) throws IOException {
        Resource resource = conditionsFile.getResource();
        List<ConditionDataHolder> conditionDataHolders = extractConditionDataFromFile(resource);
        // проверка на уникальность paramIndex в правилах
        conditionDataHolders.stream().flatMap(conditionDataHolder -> conditionDataHolder.paramTermSetIndexPairs().stream())
                        .map(ConditionDataHolder.ParamTermSetIndexPair::paramIndex)
                        .reduce((paramIndex1, paramIndex2) -> {
                            if (paramIndex1.equals(paramIndex2)) {
                                throw new IllegalArgumentException("Неправильно составлены правила!");
                            }
                            return paramIndex2;
                        });


        Files.write(resource.getFile().toPath(), conditionsFile.getBytes());
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
