package ru.merenkov.diploma.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.merenkov.diploma.domain.DeltaHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class ExcelService {

    public Map<Integer, List<Double>> getRawDataFromFile(MultipartFile file) throws IOException {
        Workbook workbook;
        workbook = new XSSFWorkbook(file.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        Map<Integer, List<Double>> paramIndexToValueListMap = new HashMap<>();

        // skip title row
        rows.next();
        while (rows.hasNext()) {
            Row row = rows.next();
            Iterator<Cell> cells = row.iterator();
            // skip time and marker columns
            cells.next();
            cells.next();

            while (cells.hasNext()) {
                Cell cell = cells.next();
                double cellValue = cell.getNumericCellValue();

                List<Double> valueList = paramIndexToValueListMap.get(cell.getColumnIndex() - 2);
                if (CollectionUtils.isNotEmpty(valueList)) {
                    if (valueList.get(valueList.size() - 1) == cellValue) {
                        continue;
                    }
                    paramIndexToValueListMap.get(cell.getColumnIndex() - 2).add(cellValue);
                } else {
                    paramIndexToValueListMap.put(cell.getColumnIndex() - 2, new ArrayList<>(List.of(cellValue)));
                }
            }
        }

        return paramIndexToValueListMap;
    }

    public List<DeltaHolder> getDeltasFromFile(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        ArrayList<DeltaHolder> deltasListList = new ArrayList<>();

        // skip column titles columns
        rows.next();
        while (rows.hasNext()) {
            Row row = rows.next();
            Iterator<Cell> cells = row.iterator();

            // skip row titles columns
            cells.next();

            Cell deltaCell1 = cells.next();
            Cell deltaCell2 = cells.next();
            deltasListList.add(new DeltaHolder(deltaCell1.getNumericCellValue(), deltaCell2.getNumericCellValue()));
        }

        return deltasListList;
    }
}
