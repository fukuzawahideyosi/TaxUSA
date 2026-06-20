package com.panda.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class JCT_jiancha {


	static LinkedHashMap<String, String> dataMap_PDSK_skip = new LinkedHashMap<>();

    private static final String SOURCE_DIR = "D:\\Users\\Administrator\\Downloads\\JCT";
//    private static final String SOURCE_DIR = "C:\\Users\\Administrator\\Desktop\\JCT\\JCTtext";


    private static final String TARGET_DIR = "C:\\Users\\Administrator\\Desktop\\JCT\\2025提取\\";
    private static final String OUTPUT_FILE = "C:\\Users\\Administrator\\Desktop\\JCT\\提取结果.xlsx";

    public static void main(String[] args) throws Exception {

        extractFiles(SOURCE_DIR, TARGET_DIR);
        System.out.println("处理完成！");

        extractExcelData();
        System.out.println("提取完成！");
    }

    public static void extractFiles(String sourceDir, String targetDir) throws IOException {
    	clearDirectory(TARGET_DIR);

        Path targetPath = Paths.get(targetDir);

        if (!Files.exists(targetPath)) {
            Files.createDirectories(targetPath);
        }

        Files.walkFileTree(Paths.get(sourceDir), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

                if (dir.getFileName().toString().equals("1証憑")) {

                    Path parent = dir.getParent();
                    if (parent == null) {
                        return FileVisitResult.CONTINUE;
                    }

                    String parentName = parent.getFileName().toString();

                    AtomicBoolean foundExcel = new AtomicBoolean(false);

                    try {
                        Files.list(dir)
                                .filter(Files::isRegularFile)
                                .forEach(file -> {
                                    String lowerName = file.getFileName().toString().toLowerCase();

                                    if (lowerName.endsWith(".xls") || lowerName.endsWith(".xlsx")) {
                                        foundExcel.set(true);
                                        try {
                                            String newFileName = parentName + "_" + file.getFileName().toString();
                                            Path targetFile = targetPath.resolve(newFileName);

                                            Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);

//                                            System.out.println("复制Excel: " + file + " → " + targetFile);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                        // 如果没有找到Excel
                        if (!foundExcel.get()) {
                            Path xtxFile = targetPath.resolve(parentName + "");

                            Files.deleteIfExists(xtxFile); // 防止已存在
                            Files.createFile(xtxFile);

                            System.out.println("未找到Excel，生成: " + dir.toString());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return FileVisitResult.CONTINUE;
            }
        });
    }


    public static void extractExcelData() throws Exception {

        List<String[]> results = new ArrayList<>();

        Files.walk(Paths.get(TARGET_DIR))
                .filter(Files::isRegularFile)
                .filter(path -> {
                    String name = path.toString().toLowerCase();
                    return name.endsWith(".xls") || name.endsWith(".xlsx");
                })
                .forEach(path -> {
                    try (InputStream is = new FileInputStream(path.toFile());
                         Workbook workbook = WorkbookFactory.create(is)) {

                        Sheet sheet = workbook.getSheetAt(0);

                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

                        System.out.println("读取: " + path);

                        String value33 = "";
                        String value46 = "";

                        for (Row row : sheet) {

                            String col2 = getCellValue(row, 1); // 第二列 B列

                            if (col2 == null) continue;

                            if (col2.contains("课税标准额")) {
                                value33 = getCellValue(row, 2); // 第三列 C列
                            }

                            if (col2.contains("确定申告须缴消费税合计额")) {
                                value46 = getCellValue(row, 2);
                            }
                        }



//                        String value33 = getCellValue(sheet, 32, 2, evaluator);
//                        String value46 = getCellValue(sheet, 45, 2, evaluator);
//                        String value46_1 = getCellValue(sheet, 45, 1, evaluator);

                        String v = path.getFileName().toString().split("_")[0];

//                        if ("确定申告须缴消费税合计额".equals(value46_1) && !dataMap_PDSK_skip.containsKey(v)) {
                        if ((!StringUtils.isEmpty(value33) || !StringUtils.isEmpty(value46)) && !dataMap_PDSK_skip.containsKey(v)) {
                        	results.add(new String[]{
                        			v,
                        			value33,
                        			value46
                        	});

//                        	dataMap_PDSK_skip.put(v, "");
                        }


                    } catch (Exception e) {
                        System.out.println("读取失败: " + path);
                    }
                });

        writeResult(results);
    }

    private static String getCellValue(Sheet sheet, int rowIndex, int colIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) return "";

        Cell cell = row.getCell(colIndex);
        if (cell == null) return "";

        return cell.toString();
    }

    private static void writeResult(List<String[]> data) throws Exception {

        File dir = new File(TARGET_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("提取结果");

        int rowIndex = 0;

        // 表头
        Row header = sheet.createRow(rowIndex++);
        header.createCell(0).setCellValue("文件名");
        header.createCell(1).setCellValue("课税标准额");//C33
        header.createCell(2).setCellValue("确定申告须缴消费税合计额");//C46

        for (String[] rowData : data) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(rowData[0]);
            row.createCell(1).setCellValue(rowData[1]);
            row.createCell(2).setCellValue(rowData[2]);
        }

        try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE)) {
            workbook.write(fos);
        }

        workbook.close();
    }

    private static String getCellValue(Sheet sheet, int rowIndex, int colIndex, FormulaEvaluator evaluator) {

        Row row = sheet.getRow(rowIndex);
        if (row == null) return "";

        Cell cell = row.getCell(colIndex);
        if (cell == null) return "";

        CellType cellType = cell.getCellType();

        if (cellType == CellType.FORMULA) {
            CellValue cellValue = evaluator.evaluate(cell);

            switch (cellValue.getCellType()) {
                case STRING:
                    return cellValue.getStringValue();

                case NUMERIC:
                    return new BigDecimal(cellValue.getNumberValue())
                            .stripTrailingZeros()
                            .toPlainString();

                case BOOLEAN:
                    return String.valueOf(cellValue.getBooleanValue());

                default:
                    return "";
            }
        }

        if (cellType == CellType.NUMERIC) {
            return new BigDecimal(cell.getNumericCellValue())
                    .stripTrailingZeros()
                    .toPlainString();
        }

        if (cellType == CellType.STRING) {
            return cell.getStringCellValue();
        }

        return "";
    }

    private static String getCellValue(Row row, int colIndex) {

        if (row == null) return "";

        Cell cell = row.getCell(colIndex);
        if (cell == null) return "";

        CellType type = cell.getCellType();

        if (type == CellType.FORMULA) {

            // 直接读取缓存结果，不重新计算
            type = cell.getCachedFormulaResultType();
        }

        switch (type) {

            case STRING:
                return cell.getStringCellValue().trim();

            case NUMERIC:
                return new java.math.BigDecimal(cell.getNumericCellValue())
                        .stripTrailingZeros()
                        .toPlainString();

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            default:
                return "";
        }
    }


    public static void clearDirectory(String dirPath) throws IOException {

    Path directory = Paths.get(dirPath);

    if (!Files.exists(directory)) {
        return;
    }

    Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
//            System.out.println("删除文件: " + file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            // 不删除根目录
            if (!dir.equals(directory)) {
                Files.delete(dir);
                System.out.println("删除目录: " + dir);
            }
            return FileVisitResult.CONTINUE;
        }
    });
}

}
