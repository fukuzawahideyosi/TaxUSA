package com.cpsc.efiling.service;

import com.cpsc.efiling.util.CpscEfilingDbUtil;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CpscImportCsvService {

    /**
     * CPSC Import CSV 官方字段顺序。
     * 生成 CSV 时必须按这个顺序输出。
     */
    private static final List<String> IMPORT_HEADERS = Arrays.asList(
            "Product Update",
            "Current Version ID",
            "New Version ID",
            "Primary Product ID",
            "Primary Product ID Type",
            "GTIN",
            "UPC",
            "SKU",
            "Model Number",
            "Serial Number",
            "Registered Number",
            "Alternate Identifier",
            "Certificate Type",
            "Product Name (Model)",
            "Trade/Brand Name",
            "Product/Model Description",
            "Product/Model Color",
            "Product/Model Style",
            "Manufacturer Is New?",
            "Manufacturer GLN",
            "Manufacturer Alternate ID",
            "Manufacturer Name",
            "Small Batch Manufacturer CPSC ID",
            "Manufacturer Address Line 1",
            "Manufacturer Address Line 2",
            "Manufacturer Apt/Suite Number",
            "Manufacturer City",
            "Manufacturer State/Province",
            "Manufacturer Country",
            "Manufacturer Zip/Postal Code",
            "Manufacturer Phone",
            "Manufacturer Email",
            "Manufacture Date",
            "Production Start Date",
            "Production End Date",
            "Lot Number",
            "Lot Number Assigned By",
            "Lab 1 Type",
            "Lab 1 Is New?",
            "Lab 1 CPSC-ID",
            "Lab 1 Alternate ID",
            "Lab 1 GLN",
            "Lab 1 Name",
            "Lab 1 Address Line 1",
            "Lab 1 Address Line 2",
            "Lab 1 Apt/Suite Number",
            "Lab 1 City",
            "Lab 1 State/Province",
            "Lab 1 Country",
            "Lab 1 Zip/Postal Code",
            "Lab 1 Phone",
            "Lab 1 Email",
            "Lab 1 Citation Codes",
            "Lab 1 Test Report ID",
            "Lab 1 Test URL",
            "Lab 1 Test Report Access Key",
            "Lab 1 Is Component?",
            "Lab 1 Component Description",
            "Lab 2 Type",
            "Lab 2 Is New?",
            "Lab 2 CPSC-ID",
            "Lab 2 Alternate ID",
            "Lab 2 Citation Codes",
            "Lab 2 Test Report ID",
            "Lab 2 Test URL",
            "Lab 2 Test Report Access Key",
            "Lab 2 Is Component?",
            "Lab 2 Component Description",
            "Last Test Date",
            "Point of Contact (POC) for Test Result Records",
            "POC Is New?",
            "POC Alternate ID",
            "POC GLN",
            "POC Name",
            "POC Address Line 1",
            "POC Address Line 2",
            "POC Apt/Suite Number",
            "POC City",
            "POC State/Province",
            "POC Country",
            "POC Zip/Postal Code",
            "POC Phone",
            "POC Email"
    );

    private static final Set<String> SKIP_ROW_MARKERS = new HashSet<>(Arrays.asList(
            "必填", "条件必填", "可选", "可选/建议填", "填写说明", "说明"
    ));

    public GenerateResult generateCsvAndSaveDb(
            InputStream excelInputStream,
            String originalFileName,
            String certifierId,
            String collectionId,
            File outputDir
    ) throws Exception {

        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("无法创建输出目录：" + outputDir.getAbsolutePath());
        }

        List<Map<String, String>> rows = readExcelRows(excelInputStream);

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("Excel 中没有可导出的数据行。请从第10行开始填写客户数据。");
        }

        List<String> errors = validateRows(rows);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }

        String timeSuffix = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new java.util.Date());
        String fileName = "CPSC_IMPORT_" + timeSuffix + ".csv";
        File csvFile = new File(outputDir, fileName);

        writeCsv(rows, csvFile);

        long batchId = saveCsvRecordToDb(
                certifierId,
                collectionId,
                originalFileName,
                fileName,
                csvFile,
                rows.size()
        );

        GenerateResult result = new GenerateResult();
        result.setBatchId(batchId);
        result.setFileName(fileName);
        result.setRowCount(rows.size());
        result.setFile(csvFile);

        return result;
    }

    /**
     * 读取客户 Excel。
     *
     * 修正版重点：
     * 1. 优先使用“官方英文表头行”，例如 Product Update / Current Version ID；
     * 2. 不再把“中文注释行”“必填行”“填写说明行”当作数据；
     * 3. 从真正客户填写的数据行开始读取；
     * 4. 对 Y/N、GCC/CPC、日期格式做标准化。
     */
    private List<Map<String, String>> readExcelRows(InputStream excelInputStream) throws Exception {
        List<Map<String, String>> result = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(excelInputStream)) {
            Sheet sheet = workbook.getSheet("01_客户填写数据");
            if (sheet == null) {
                sheet = workbook.getSheetAt(0);
            }

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            DataFormatter formatter = new DataFormatter(Locale.US);

            int headerRowIndex = findHeaderRow(sheet, formatter, evaluator);
            if (headerRowIndex < 0) {
                throw new IllegalArgumentException("找不到官方英文字段表头行。请确认 Excel 中包含 Product Update、Primary Product ID、Certificate Type 等字段。");
            }

            Row headerRow = sheet.getRow(headerRowIndex);
            Map<Integer, String> columnFieldMap = new LinkedHashMap<>();

            for (Cell cell : headerRow) {
                String raw = formatter.formatCellValue(cell, evaluator).trim();
                String fieldName = extractEnglishField(raw);

                if (IMPORT_HEADERS.contains(fieldName)) {
                    columnFieldMap.put(cell.getColumnIndex(), fieldName);
                }
            }

            if (!columnFieldMap.containsValue("Primary Product ID")) {
                throw new IllegalArgumentException("表头中缺少 Primary Product ID。");
            }

            int dataStartRowIndex = findDataStartRow(sheet, headerRowIndex + 1, columnFieldMap, formatter, evaluator);

            for (int i = dataStartRowIndex; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                if (isNonDataRow(row, columnFieldMap, formatter, evaluator)) {
                    continue;
                }

                Map<String, String> data = new LinkedHashMap<>();
                boolean hasValue = false;

                for (String h : IMPORT_HEADERS) {
                    data.put(h, "");
                }

                for (Map.Entry<Integer, String> entry : columnFieldMap.entrySet()) {
                    Cell cell = row.getCell(entry.getKey());
                    String value = formatter.formatCellValue(cell, evaluator).trim();

                    if (!value.isEmpty()) {
                        hasValue = true;
                    }

                    data.put(entry.getValue(), value);
                }

                if (!hasValue) {
                    continue;
                }

                normalizeInputValues(data);
                applyDefaultValues(data);
                data.put("_excelRowNo", String.valueOf(i + 1));
                result.add(data);
            }
        }

        return result;
    }

    /**
     * 优先查找官方英文行，避免把“中文注释（Product Update）”那一行误认为数据表头。
     */
    private int findHeaderRow(Sheet sheet, DataFormatter formatter, FormulaEvaluator evaluator) {
        int fallbackHeaderRow = -1;

        for (int i = 0; i <= Math.min(sheet.getLastRowNum(), 30); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            int exactFound = 0;
            int extractedFound = 0;

            for (Cell cell : row) {
                String raw = formatter.formatCellValue(cell, evaluator).trim();
                String extracted = extractEnglishField(raw);

                if ("Product Update".equals(raw)
                        || "Primary Product ID".equals(raw)
                        || "New Version ID".equals(raw)
                        || "Certificate Type".equals(raw)) {
                    exactFound++;
                }

                if ("Product Update".equals(extracted)
                        || "Primary Product ID".equals(extracted)
                        || "New Version ID".equals(extracted)
                        || "Certificate Type".equals(extracted)) {
                    extractedFound++;
                }
            }

            // 官方英文表头行，例如第7行：Product Update, Current Version ID...
            if (exactFound >= 3) {
                return i;
            }

            // 备用：中文注释行，例如：是否更新已有产品（Product Update）
            if (fallbackHeaderRow < 0 && extractedFound >= 3) {
                fallbackHeaderRow = i;
            }
        }

        return fallbackHeaderRow;
    }

    /**
     * 找到第一条真正的数据行。
     * 你的模板中第8行是“必填”，第9行是说明，第10行才是数据。
     */
    private int findDataStartRow(
            Sheet sheet,
            int startRowIndex,
            Map<Integer, String> columnFieldMap,
            DataFormatter formatter,
            FormulaEvaluator evaluator
    ) {
        for (int i = startRowIndex; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            if (isNonDataRow(row, columnFieldMap, formatter, evaluator)) {
                continue;
            }

            String productId = getValueByField(row, columnFieldMap, formatter, evaluator, "Primary Product ID");
            String productName = getValueByField(row, columnFieldMap, formatter, evaluator, "Product Name (Model)");
            String versionId = getValueByField(row, columnFieldMap, formatter, evaluator, "New Version ID");

            if (!isBlank(productId) || !isBlank(productName) || !isBlank(versionId)) {
                return i;
            }
        }

        return startRowIndex;
    }

    private boolean isNonDataRow(
            Row row,
            Map<Integer, String> columnFieldMap,
            DataFormatter formatter,
            FormulaEvaluator evaluator
    ) {
        String firstValue = "";
        if (!columnFieldMap.isEmpty()) {
            Integer firstCol = columnFieldMap.keySet().iterator().next();
            firstValue = formatter.formatCellValue(row.getCell(firstCol), evaluator).trim();
        }

        if (isBlank(firstValue)) {
            return false;
        }

        String extracted = extractEnglishField(firstValue);

        // 跳过官方英文表头行
        if ("Product Update".equals(extracted) || "Product Update".equals(firstValue)) {
            return true;
        }

        // 跳过“必填”“填写说明”等非数据行
        for (String marker : SKIP_ROW_MARKERS) {
            if (firstValue.contains(marker)) {
                return true;
            }
        }

        // 跳过中文说明行，例如：新产品填 N；修改已有产品填 Y。
        if (firstValue.contains("新产品") && firstValue.contains("修改")) {
            return true;
        }

        return false;
    }

    private String getValueByField(
            Row row,
            Map<Integer, String> columnFieldMap,
            DataFormatter formatter,
            FormulaEvaluator evaluator,
            String field
    ) {
        for (Map.Entry<Integer, String> entry : columnFieldMap.entrySet()) {
            if (field.equals(entry.getValue())) {
                return formatter.formatCellValue(row.getCell(entry.getKey()), evaluator).trim();
            }
        }

        return "";
    }

    /**
     * 从 “中文注释（Product Update）” 中提取 Product Update。
     */
    private String extractEnglishField(String raw) {
        if (raw == null) {
            return "";
        }

        String text = raw.trim();

        int left1 = text.lastIndexOf("（");
        int right1 = text.lastIndexOf("）");

        if (left1 >= 0 && right1 > left1) {
            return text.substring(left1 + 1, right1).trim();
        }

        int left2 = text.lastIndexOf("(");
        int right2 = text.lastIndexOf(")");

        if (left2 >= 0 && right2 > left2) {
            return text.substring(left2 + 1, right2).trim();
        }

        return text;
    }

    /**
     * 标准化客户可能填写的中文值、大小写、日期格式。
     */
    private void normalizeInputValues(Map<String, String> row) {
        row.put("Product Update", normalizeYN(row.get("Product Update"), ""));
        row.put("Manufacturer Is New?", normalizeYN(row.get("Manufacturer Is New?"), ""));
        row.put("Lab 1 Is New?", normalizeYN(row.get("Lab 1 Is New?"), ""));
        row.put("Lab 2 Is New?", normalizeYN(row.get("Lab 2 Is New?"), ""));
        row.put("POC Is New?", normalizeYN(row.get("POC Is New?"), ""));

        row.put("Certificate Type", normalizeCertificateType(row.get("Certificate Type")));

        String idType = row.get("Primary Product ID Type");
        if ("Model #".equalsIgnoreCase(trim(idType))) {
            row.put("Primary Product ID Type", "Model Number");
        }

        row.put("Lab 1 Is Component?", normalizeYesNo(row.get("Lab 1 Is Component?"), ""));
        row.put("Lab 2 Is Component?", normalizeYesNo(row.get("Lab 2 Is Component?"), ""));

        row.put("Manufacture Date", normalizeMonthYear(row.get("Manufacture Date")));
        row.put("Production Start Date", normalizeFullDate(row.get("Production Start Date")));
        row.put("Production End Date", normalizeFullDate(row.get("Production End Date")));
        row.put("Last Test Date", normalizeFullDate(row.get("Last Test Date")));

        row.put("Lab 1 Citation Codes", normalizeMultiValue(row.get("Lab 1 Citation Codes")));
        row.put("Lab 2 Citation Codes", normalizeMultiValue(row.get("Lab 2 Citation Codes")));
    }

    /**
     * 给客户没有填写的字段设置合理默认值。
     */
    private void applyDefaultValues(Map<String, String> row) {
        putDefault(row, "Product Update", "N");
        putDefault(row, "Primary Product ID Type", "Model Number");
        putDefault(row, "Certificate Type", "GCC");
        putDefault(row, "Manufacturer Is New?", "Y");
        putDefault(row, "Lab 1 Type", "LAB");
        putDefault(row, "Lab 1 Is New?", "N");
        putDefault(row, "Lab 1 Is Component?", "No");
        putDefault(row, "Point of Contact (POC) for Test Result Records", "Importer");
        putDefault(row, "POC Is New?", "N");

        String productId = row.get("Primary Product ID");
        String newVersionId = row.get("New Version ID");

        if (isBlank(newVersionId) && !isBlank(productId)) {
            String clean = productId.replaceAll("[^A-Za-z0-9]", "");
            row.put("New Version ID", "V" + clean);
        }

        if (isBlank(row.get("Model Number"))) {
            row.put("Model Number", productId);
        }

        if (!isBlank(row.get("Lot Number"))) {
            putDefault(row, "Lot Number Assigned By", "Manufacturer");
        }
    }

    private void putDefault(Map<String, String> row, String key, String value) {
        if (isBlank(row.get(key))) {
            row.put(key, value);
        }
    }

    private String normalizeYN(String value, String defaultValue) {
        String v = trim(value);
        if (v.isEmpty()) {
            return defaultValue;
        }

        String upper = v.toUpperCase(Locale.ROOT);

        if ("Y".equals(upper)
                || "YES".equals(upper)
                || "TRUE".equals(upper)
                || "是".equals(v)
                || "更新".equals(v)
                || "修改".equals(v)
                || v.contains("更新已有")) {
            return "Y";
        }

        if ("N".equals(upper)
                || "NO".equals(upper)
                || "FALSE".equals(upper)
                || "否".equals(v)
                || "新产品".equals(v)
                || "新增".equals(v)
                || v.contains("新建")
                || v.contains("不更新")) {
            return "N";
        }

        return v;
    }

    private String normalizeYesNo(String value, String defaultValue) {
        String yn = normalizeYN(value, defaultValue);
        if ("Y".equalsIgnoreCase(yn)) {
            return "Yes";
        }
        if ("N".equalsIgnoreCase(yn)) {
            return "No";
        }
        return yn;
    }

    private String normalizeCertificateType(String value) {
        String v = trim(value);
        if (v.isEmpty()) {
            return v;
        }

        String upper = v.toUpperCase(Locale.ROOT);

        if (upper.contains("GCC") || v.contains("普通")) {
            return "GCC";
        }

        if (upper.contains("CPC") || v.contains("儿童")) {
            return "CPC";
        }

        return upper;
    }

    private String normalizeMonthYear(String value) {
        String v = trim(value);
        if (v.isEmpty()) {
            return v;
        }

        // 已经是 06/2026
        if (v.matches("^(0[1-9]|1[0-2])/\\d{4}$")) {
            return v;
        }

        // 6/2026 -> 06/2026
        if (v.matches("^([1-9]|1[0-2])/\\d{4}$")) {
            String[] p = v.split("/");
            return String.format("%02d/%s", Integer.parseInt(p[0]), p[1]);
        }

        // 2026-06 或 2026/06 -> 06/2026
        if (v.matches("^\\d{4}[-/](0?[1-9]|1[0-2])$")) {
            String[] p = v.split("[-/]");
            return String.format("%02d/%s", Integer.parseInt(p[1]), p[0]);
        }

        return v;
    }

    private String normalizeFullDate(String value) {
        String v = trim(value);
        if (v.isEmpty()) {
            return v;
        }

        // 06/19/2026
        if (v.matches("^(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])/\\d{4}$")) {
            return v;
        }

        // 6/19/2026 -> 06/19/2026
        if (v.matches("^([1-9]|1[0-2])/([1-9]|[12][0-9]|3[01])/\\d{4}$")) {
            String[] p = v.split("/");
            return String.format("%02d/%02d/%s", Integer.parseInt(p[0]), Integer.parseInt(p[1]), p[2]);
        }

        // 2026-06-19 或 2026/06/19 -> 06/19/2026
        if (v.matches("^\\d{4}[-/](0?[1-9]|1[0-2])[-/]([0-2]?[0-9]|3[01])$")) {
            String[] p = v.split("[-/]");
            return String.format("%02d/%02d/%s", Integer.parseInt(p[1]), Integer.parseInt(p[2]), p[0]);
        }

        return v;
    }

    private String normalizeMultiValue(String value) {
        String v = trim(value);
        if (v.isEmpty()) {
            return v;
        }

        // CPSC 模板多个值建议用分号
        return v.replace("，", ";")
                .replace(",", ";")
                .replace("\n", ";")
                .replace("；", ";");
    }

    private List<String> validateRows(List<Map<String, String>> rows) {
        List<String> errors = new ArrayList<>();

        for (Map<String, String> row : rows) {
            String excelRowNo = row.getOrDefault("_excelRowNo", "?");
            String prefix = "Excel 第 " + excelRowNo + " 行：";

            required(errors, prefix, row, "Product Update");
            required(errors, prefix, row, "New Version ID");
            required(errors, prefix, row, "Primary Product ID");
            required(errors, prefix, row, "Primary Product ID Type");
            required(errors, prefix, row, "Certificate Type");
            required(errors, prefix, row, "Product Name (Model)");
            required(errors, prefix, row, "Manufacturer Is New?");
            required(errors, prefix, row, "Manufacturer Alternate ID");
            required(errors, prefix, row, "Manufacturer Name");
            required(errors, prefix, row, "Manufacturer Address Line 1");
            required(errors, prefix, row, "Manufacturer City");
            required(errors, prefix, row, "Manufacturer Country");
            required(errors, prefix, row, "Manufacturer Zip/Postal Code");
            required(errors, prefix, row, "Manufacturer Phone");
            required(errors, prefix, row, "Manufacturer Email");
            required(errors, prefix, row, "Manufacture Date");
            required(errors, prefix, row, "Last Test Date");

            if (!"N".equalsIgnoreCase(row.get("Product Update"))
                    && !"Y".equalsIgnoreCase(row.get("Product Update"))) {
                errors.add(prefix + "Product Update 只能填写 Y 或 N。");
            }

            if ("Y".equalsIgnoreCase(row.get("Product Update"))
                    && isBlank(row.get("Current Version ID"))) {
                errors.add(prefix + "Product Update=Y 时，Current Version ID 不能为空。");
            }

            if (!"GCC".equalsIgnoreCase(row.get("Certificate Type"))
                    && !"CPC".equalsIgnoreCase(row.get("Certificate Type"))) {
                errors.add(prefix + "Certificate Type 只能填写 GCC 或 CPC。");
            }

            if (!isBlank(row.get("Lot Number")) && isBlank(row.get("Lot Number Assigned By"))) {
                errors.add(prefix + "填写了 Lot Number 时，Lot Number Assigned By 也必须填写 Manufacturer 或 Seller。");
            }

            boolean hasLab = !isBlank(row.get("Lab 1 Citation Codes"))
                    || !isBlank(row.get("Lab 1 Alternate ID"))
                    || !isBlank(row.get("Lab 1 CPSC-ID"))
                    || !isBlank(row.get("Lab 1 Name"));

            if (hasLab) {
                required(errors, prefix, row, "Lab 1 Type");
                required(errors, prefix, row, "Lab 1 Is New?");
                required(errors, prefix, row, "Lab 1 Citation Codes");

                if ("LAB".equalsIgnoreCase(row.get("Lab 1 Type"))
                        && isBlank(row.get("Lab 1 Alternate ID"))
                        && isBlank(row.get("Lab 1 GLN"))) {
                    errors.add(prefix + "Lab 1 Type=LAB 时，Lab 1 Alternate ID 或 Lab 1 GLN 至少填写一个。");
                }

                if ("ITL".equalsIgnoreCase(row.get("Lab 1 Type"))
                        && isBlank(row.get("Lab 1 CPSC-ID"))) {
                    errors.add(prefix + "Lab 1 Type=ITL 时，Lab 1 CPSC-ID 不能为空。");
                }
            }

            if (!isDateMmYyyy(row.get("Manufacture Date"))) {
                errors.add(prefix + "Manufacture Date 格式应为 MM/YYYY，例如 06/2026。");
            }

            if (!isBlank(row.get("Production Start Date")) && !isDateMmDdYyyy(row.get("Production Start Date"))) {
                errors.add(prefix + "Production Start Date 格式应为 MM/DD/YYYY，例如 06/01/2026。");
            }

            if (!isBlank(row.get("Production End Date")) && !isDateMmDdYyyy(row.get("Production End Date"))) {
                errors.add(prefix + "Production End Date 格式应为 MM/DD/YYYY，例如 06/30/2026。");
            }

            if (!isDateMmDdYyyy(row.get("Last Test Date"))) {
                errors.add(prefix + "Last Test Date 格式应为 MM/DD/YYYY，例如 06/19/2026。");
            }
        }

        return errors;
    }

    private void required(List<String> errors, String prefix, Map<String, String> row, String field) {
        if (isBlank(row.get(field))) {
            errors.add(prefix + field + " 不能为空。");
        }
    }

    private boolean isDateMmYyyy(String value) {
        return !isBlank(value) && value.matches("^(0[1-9]|1[0-2])/\\d{4}$");
    }

    private boolean isDateMmDdYyyy(String value) {
        return !isBlank(value) && value.matches("^(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])/\\d{4}$");
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void writeCsv(List<Map<String, String>> rows, File csvFile) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(csvFile), StandardCharsets.UTF_8)) {
            writeCsvLine(writer, IMPORT_HEADERS);

            for (Map<String, String> row : rows) {
                List<String> values = new ArrayList<>();
                for (String header : IMPORT_HEADERS) {
                    values.add(row.getOrDefault(header, ""));
                }
                writeCsvLine(writer, values);
            }
        }
    }

    private void writeCsvLine(Writer writer, List<String> values) throws IOException {
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                writer.write(",");
            }

            writer.write(escapeCsv(values.get(i)));
        }

        writer.write("\r\n");
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        boolean needQuote = value.contains(",")
                || value.contains("\"")
                || value.contains("\r")
                || value.contains("\n");

        String escaped = value.replace("\"", "\"\"");

        return needQuote ? "\"" + escaped + "\"" : escaped;
    }

    /**
     * 登录数据库，并保存生成记录。
     * 使用你前面创建的 CPSC_eFiling_import_batch 表。
     */
    private long saveCsvRecordToDb(
            String certifierId,
            String collectionId,
            String originalFileName,
            String generatedFileName,
            File csvFile,
            int rowCount
    ) throws SQLException, IOException {

        String csvText = readFile(csvFile);

        String sql = "INSERT INTO CPSC_eFiling_import_batch "
                + "(certifier_id, collection_id, do_certify, import_status, request_json, response_json, status_message) "
                + "VALUES (?, ?, 0, ?, ?, ?, ?)";

        try (Connection conn = CpscEfilingDbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, certifierId);
            ps.setString(2, collectionId);
            ps.setString(3, "CSV_CREATED");
            ps.setString(4, csvText);
            ps.setString(5, "{\"generatedFileName\":\"" + generatedFileName + "\",\"rowCount\":" + rowCount + "}");
            ps.setString(6, "Excel文件 " + originalFileName + " 已生成 Import CSV：" + generatedFileName);

            int count = ps.executeUpdate();
            if (count == 0) {
                throw new SQLException("数据库未插入任何记录。");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }

            throw new SQLException("插入成功，但未取得自增ID。");
        }
    }

    private String readFile(File file) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static class GenerateResult {
        private long batchId;
        private String fileName;
        private int rowCount;
        private File file;

        public long getBatchId() {
            return batchId;
        }

        public void setBatchId(long batchId) {
            this.batchId = batchId;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getRowCount() {
            return rowCount;
        }

        public void setRowCount(int rowCount) {
            this.rowCount = rowCount;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }
}
