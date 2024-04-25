package ru.merenkov.diploma.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {

    public List<List<Double>> readExcel(MultipartFile file) throws IOException {
        Workbook workbook;
        workbook = new XSSFWorkbook(file.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        ArrayList<List<Double>> paramListList = new ArrayList<>();

        // skip title row
        rows.next();
        while (rows.hasNext()) {
            Row row = rows.next();
            Iterator<Cell> cells = row.iterator();

            ArrayList<Double> paramList = new ArrayList<>();

            // skip time and marker columns
            cells.next();
            cells.next();
            while (cells.hasNext()) {
                Cell cell = cells.next();
                double cellValue = cell.getNumericCellValue();
                if (paramList.size() > 0
                        && paramList.get(paramList.size() - 1).equals(cellValue)) {
                    break;
                }
                paramList.add(cellValue);
            }

            paramListList.add(paramList);
        }

        return paramListList;
    }
}
