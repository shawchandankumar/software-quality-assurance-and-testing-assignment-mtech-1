package org.selenium.utils;

import java.io.*;
import java.util.*;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

public class ExcelUtils {

    public static Object[][] readExcelSheetTestData(String filepath, String sheetName) throws IOException {
        File testDataFile = new File(filepath);
        FileInputStream stream= new FileInputStream(testDataFile);

        XSSFWorkbook testDataWB= new XSSFWorkbook(stream);
        XSSFSheet sheet = testDataWB.getSheet(sheetName); // sheet index starts from 0

        DataFormatter formatter = new DataFormatter();

        int count = sheet.getLastRowNum(); // row count from 0 to n-1
        System.out.println("Total row count:: "+count);

        Object[][] allTestData = new Object[(count-9)/2][4];
        for (int i=0; i+10<=count; i+=2) {
            Row currentRow = sheet.getRow(i+10);
            for (int j = 5; j <= 8; j++) {
                Cell currentRowCell = currentRow.getCell(j);
                if (currentRow != null && currentRowCell != null) {
                    allTestData[i/2][j-5] = formatter.formatCellValue(currentRowCell);
                }
            }
        }
        testDataWB.close();
        return allTestData;
    }
}
