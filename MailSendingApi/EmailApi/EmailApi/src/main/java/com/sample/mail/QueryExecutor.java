package com.sample.mail;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.json.JSONObject; // Import JSONObject from org.json package

public class QueryExecutor {

    public static ResultSet executeQuery(String jdbcUrl, String user, String password, String query) throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static JSONObject writeResultSetToExcel(ResultSet resultSet, String destinationDirectoryPath, String excelFileName) throws IOException, SQLException {
        JSONObject result = new JSONObject();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Query Results");

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Create a font for the header
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeight((short) 270);

        // Create a cell style with the header font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setWrapText(true); // Wrap text in the cells
        headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(headerCellStyle);

        // Create a header row with filter enabled
        Row headerRow = sheet.createRow(5);
        for (int i = 1; i <= columnCount; i++) {
            Cell cell = headerRow.createCell(i - 1);
            cell.setCellValue(metaData.getColumnLabel(i));
            cell.setCellStyle(headerCellStyle);
            // Set column width to approximately 2cm (567 units)
            sheet.setColumnWidth(i - 1, 6000);
        }
        sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(5, 5, 0, columnCount - 1));

        // Freeze the header row
        sheet.createFreezePane(0, 6); // Freeze the first row and the first six columns

        // Set the row height a bit larger than default
        headerRow.setHeightInPoints(25);

        // Write data rows with alternating colors
        int rowIndex = 6;
        boolean colorFlag = true; // to alternate row colors
        CellStyle dataCellStyle1 = createDataCellStyle(workbook, IndexedColors.WHITE.getIndex());
        CellStyle dataCellStyle2 = createDataCellStyle(workbook, IndexedColors.GREY_25_PERCENT.getIndex());

        while (resultSet.next()) {
            Row row = sheet.createRow(rowIndex++);
            for (int i = 1; i <= columnCount; i++) {
                Cell cell = row.createCell(i - 1);
                cell.setCellValue(resultSet.getString(i));
                if (colorFlag) {
                    cell.setCellStyle(dataCellStyle1);
                } else {
                    cell.setCellStyle(dataCellStyle2);
                }
            }
            colorFlag = !colorFlag; // flip the color flag
        }

        // Auto-size all columns
        // for (int i = 0; i < columnCount; i++) {
        //     sheet.autoSizeColumn(i);
        // }
        
        // Create the directory if it doesn't exist
        Path directoryPath = Paths.get(destinationDirectoryPath);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // Combine directory path and file name
        Path excelFilePath = Paths.get(destinationDirectoryPath, excelFileName);

        // Write the workbook to a file
        try (FileOutputStream fileOut = new FileOutputStream(excelFilePath.toString())) {
            workbook.write(fileOut);
        }
        workbook.close();

        // Prepare the result JSON object
        result.put("filePath", excelFilePath.toString());
        result.put("url", "http://localhost:8080/" + excelFileName); // Replace with your actual domain

        return result;
    }

    private static void setBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private static CellStyle createDataCellStyle(Workbook workbook, short fillColorIndex) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(fillColorIndex);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(style);
        return style;
    }

    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/jforce";
        String user = "root";
        String password = "ABHI";
        String query = "SELECT * FROM jforce.registereduserdetails"; // Or call your stored procedure here
        String destinationDirectoryPath = "C:\\Users\\Abhishek\\OneDrive\\Desktop\\PDF";
        String excelFileName = "query_results.xlsx";

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            ResultSet resultSet = executeQuery(jdbcUrl, user, password, query);
            JSONObject jsonResult = writeResultSetToExcel(resultSet, destinationDirectoryPath, excelFileName);
            
            // Print out the JSON result containing the file path and URL
            System.out.println("File Path: " + jsonResult.getString("filePath"));
            System.out.println("URL: " + jsonResult.getString("url"));

            System.out.println("Data has been written to Excel file successfully.");
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
