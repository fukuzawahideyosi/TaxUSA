package com.panda.efiling.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class CpscImportJsonBuilder {

    // Excel 第4行：API字段路径，0-based index = 3
    private static final int API_HEADER_ROW_INDEX = 3;

    // Excel 第6行开始填写数据，0-based index = 5
    private static final int DATA_START_ROW_INDEX = 5;

    /**
     * 读取 CPSC eFiling Product Registry 产品证书资料收集表，
     * 生成 /import 接口需要的 JSON 字符串。
     */
    public static String buildImportJsonFromExcel(String excelPath) throws IOException {
        try (InputStream inputStream = new FileInputStream(excelPath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            Sheet productSheet = getRequiredSheet(workbook, "01_产品基本信息");
            Sheet manufacturerSheet = getRequiredSheet(workbook, "02_制造商信息");
            Sheet labSheet = getRequiredSheet(workbook, "03_实验室与测试报告");
            Sheet pocSheet = getRequiredSheet(workbook, "04_联系人POC");
            Sheet exemptionSheet = getRequiredSheet(workbook, "05_法规豁免");

            Map<String, List<Integer>> productHeaders = buildHeaderMap(productSheet, evaluator);
            Map<String, List<Integer>> manufacturerHeaders = buildHeaderMap(manufacturerSheet, evaluator);
            Map<String, List<Integer>> labHeaders = buildHeaderMap(labSheet, evaluator);
            Map<String, List<Integer>> pocHeaders = buildHeaderMap(pocSheet, evaluator);
            Map<String, List<Integer>> exemptionHeaders = buildHeaderMap(exemptionSheet, evaluator);

            Map<String, Row> manufacturerRows = indexRowsByKey(
                    manufacturerSheet,
                    manufacturerHeaders,
                    evaluator,
                    "coreProduct.manufacturer.alternateId"
            );

            Map<String, Row> pocRows = indexRowsByKey(
                    pocSheet,
                    pocHeaders,
                    evaluator,
                    "coreProduct.poc.alternateId"
            );

            Map<String, List<Row>> labRowsByProductId = groupRowsByKey(
                    labSheet,
                    labHeaders,
                    evaluator,
                    "coreProduct.primaryProductId"
            );

            Map<String, List<String>> exemptionsByProductId = readExemptions(
                    exemptionSheet,
                    exemptionHeaders,
                    evaluator
            );

            List<Map<String, Object>> productList = new ArrayList<>();

            for (int i = DATA_START_ROW_INDEX; i <= productSheet.getLastRowNum(); i++) {
                Row productRow = productSheet.getRow(i);
                if (productRow == null) {
                    continue;
                }

                String productId = getCellValue(
                        productRow,
                        productHeaders,
                        evaluator,
                        "coreProduct.primaryProductId"
                );

                if (isBlank(productId)) {
                    continue;
                }

                Map<String, Object> productItem = new LinkedHashMap<>();
                Map<String, Object> coreProduct = new LinkedHashMap<>();

                String versionId = defaultIfBlank(
                        getCellValue(productRow, productHeaders, evaluator, "coreProduct.versionId"),
                        "V1"
                );

                String primaryProductIdType = defaultIfBlank(
                        getCellValue(productRow, productHeaders, evaluator, "coreProduct.primaryProductIdType"),
                        "Model #"
                );

                String certificateType = defaultIfBlank(
                        getCellValue(productRow, productHeaders, evaluator, "coreProduct.certificateType"),
                        "GCC"
                );

                coreProduct.put("versionId", versionId);
                coreProduct.put("primaryProductId", productId);
                coreProduct.put("primaryProductIdType", primaryProductIdType);
                coreProduct.put("certificateType", certificateType);

                putRequiredString(coreProduct, "name",
                        getCellValue(productRow, productHeaders, evaluator, "coreProduct.name"));

                putIfNotBlank(coreProduct, "tradeBrandName",
                        getCellValue(productRow, productHeaders, evaluator, "coreProduct.tradeBrandName"));

                putIfNotBlank(coreProduct, "description",
                        getCellValue(productRow, productHeaders, evaluator, "coreProduct.description"));

                putIfNotBlank(coreProduct, "color",
                        getCellValue(productRow, productHeaders, evaluator, "coreProduct.color"));

                putIfNotBlank(coreProduct, "style",
                        getCellValue(productRow, productHeaders, evaluator, "coreProduct.style"));

                List<Map<String, Object>> identifiers = buildIdentifiers(productRow, productHeaders, evaluator);
                if (!identifiers.isEmpty()) {
                    coreProduct.put("identifiers", identifiers);
                }

                String manufacturerAltId = getCellValue(
                        productRow,
                        productHeaders,
                        evaluator,
                        "coreProduct.manufacturer.alternateId"
                );

                if (isBlank(manufacturerAltId)) {
                    throw new IllegalArgumentException("产品 " + productId + " 未填写制造商编号。");
                }

                Row manufacturerRow = manufacturerRows.get(manufacturerAltId);
                if (manufacturerRow == null) {
                    throw new IllegalArgumentException("产品 " + productId + " 找不到制造商编号：" + manufacturerAltId);
                }

                Map<String, Object> manufacturer = buildManufacturer(
                        manufacturerRow,
                        manufacturerHeaders,
                        evaluator,
                        manufacturerAltId
                );

                coreProduct.put("manufacturer", manufacturer);

                putRequiredString(coreProduct, "manufactureDate",
                        getCellValue(productRow, productHeaders, evaluator, "coreProduct.manufactureDate"));

                putIfNotBlank(coreProduct, "productionStartDate",
                        getCellValue(productRow, productHeaders, evaluator, "coreProduct.productionStartDate"));

                putIfNotBlank(coreProduct, "productionEndDate",
                        getCellValue(productRow, productHeaders, evaluator, "coreProduct.productionEndDate"));

                String lotNumber = getCellValue(productRow, productHeaders, evaluator, "coreProduct.lotNumber");
                String lotNumberAssignedBy = getCellValue(productRow, productHeaders, evaluator, "coreProduct.lotNumberAssignedBy");

                if (!isBlank(lotNumber)) {
                    if (isBlank(lotNumberAssignedBy)) {
                        throw new IllegalArgumentException("产品 " + productId + " 已填写批次号，但未填写批号分配方 lotNumberAssignedBy。");
                    }
                    coreProduct.put("lotNumber", lotNumber);
                    coreProduct.put("lotNumberAssignedBy", lotNumberAssignedBy);
                }

                putRequiredString(coreProduct, "lastTestDate",
                        getCellValue(productRow, productHeaders, evaluator, "coreProduct.lastTestDate"));

                List<Row> labRows = labRowsByProductId.getOrDefault(productId, Collections.emptyList());
                List<Map<String, Object>> labs = new ArrayList<>();

                for (Row labRow : labRows) {
                    labs.add(buildLab(labRow, labHeaders, evaluator));
                }

                List<String> exemptions = exemptionsByProductId.getOrDefault(productId, Collections.emptyList());

                if (labs.isEmpty() && exemptions.isEmpty()) {
                    throw new IllegalArgumentException("产品 " + productId + " 没有实验室信息，也没有法规豁免信息。CPSC 可能返回 2026 错误。");
                }

                if (!labs.isEmpty()) {
                    coreProduct.put("labs", labs);
                }

                if (!exemptions.isEmpty()) {
                    coreProduct.put("exemptions", exemptions);
                }

                String pocAltId = getCellValue(productRow, productHeaders, evaluator, "coreProduct.poc.alternateId");

                Map<String, Object> poc;
                Row pocRow = null;

                if (!isBlank(pocAltId)) {
                    pocRow = pocRows.get(pocAltId);
                    if (pocRow == null) {
                        throw new IllegalArgumentException("产品 " + productId + " 找不到 POC 编号：" + pocAltId);
                    }
                    poc = buildPoc(pocRow, pocHeaders, evaluator, pocAltId);
                } else {
                    poc = new LinkedHashMap<>();
                    poc.put("type", "Importer");
                }

                coreProduct.put("poc", poc);

                Map<String, Object> directives = new LinkedHashMap<>();

                String productUpdateRaw = getCellValue(
                        productRow,
                        productHeaders,
                        evaluator,
                        "directives.productUpdate"
                );

                String productUpdate = normalizeYN(productUpdateRaw, "N");
                directives.put("productUpdate", productUpdate);

                if ("Y".equals(productUpdate)) {
                    String versionIdToUpdate = getCellValue(
                            productRow,
                            productHeaders,
                            evaluator,
                            "directives.versionIdToUpdate"
                    );

                    if (isBlank(versionIdToUpdate)) {
                        throw new IllegalArgumentException("产品 " + productId + " 设置为更新已有产品，但未填写 versionIdToUpdate。");
                    }

                    directives.put("versionIdToUpdate", versionIdToUpdate);
                }

                directives.put("manufacturer", buildManufacturerDirective(
                        manufacturerRow,
                        manufacturerHeaders,
                        evaluator,
                        manufacturerAltId
                ));

                List<Map<String, Object>> labDirectives = new ArrayList<>();
                for (Row labRow : labRows) {
                    labDirectives.add(buildLabDirective(labRow, labHeaders, evaluator));
                }

                if (!labDirectives.isEmpty()) {
                    directives.put("labs", labDirectives);
                }

                directives.put("poc", buildPocDirective(
                        pocRow,
                        pocHeaders,
                        evaluator,
                        pocAltId
                ));

                productItem.put("coreProduct", coreProduct);
                productItem.put("directives", directives);

                productList.add(productItem);
            }

            Map<String, Object> root = new LinkedHashMap<>();
            root.put("productList", productList);

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            return mapper.writeValueAsString(root);
        }
    }

    private static Map<String, Object> buildManufacturer(
            Row row,
            Map<String, List<Integer>> headers,
            FormulaEvaluator evaluator,
            String fallbackAltId
    ) {
        Map<String, Object> manufacturer = new LinkedHashMap<>();

        putIfNotBlank(manufacturer, "gln",
                getCellValue(row, headers, evaluator, "coreProduct.manufacturer.gln"));

        manufacturer.put("alternateId", defaultIfBlank(
                getCellValue(row, headers, evaluator, "coreProduct.manufacturer.alternateId"),
                fallbackAltId
        ));

        putIfNotBlank(manufacturer, "sbmId",
                getCellValue(row, headers, evaluator, "coreProduct.manufacturer.sbmId"));

        putRequiredString(manufacturer, "name",
                getCellValue(row, headers, evaluator, "coreProduct.manufacturer.name"));

        manufacturer.put("addressLine1",
                getCellValue(row, headers, evaluator, "coreProduct.manufacturer.addressLine1"));

        manufacturer.put("addressLine2",
                getCellValue(row, headers, evaluator, "coreProduct.manufacturer.addressLine2"));

        manufacturer.put("aptNumber",
                getCellValue(row, headers, evaluator, "coreProduct.manufacturer.aptNumber"));

        manufacturer.put("city",
                getCellValue(row, headers, evaluator, "coreProduct.manufacturer.city"));

        manufacturer.put("stateProvince",
                getCellValue(row, headers, evaluator, "coreProduct.manufacturer.stateProvince"));

        manufacturer.put("country",
                getCellValue(row, headers, evaluator, "coreProduct.manufacturer.country"));

        manufacturer.put("postalCode",
                getCellValue(row, headers, evaluator, "coreProduct.manufacturer.postalCode"));

        manufacturer.put("phone",
                getCellValue(row, headers, evaluator, "coreProduct.manufacturer.phone"));

        manufacturer.put("email",
                getCellValue(row, headers, evaluator, "coreProduct.manufacturer.email"));

        return manufacturer;
    }

    private static Map<String, Object> buildLab(
            Row row,
            Map<String, List<Integer>> headers,
            FormulaEvaluator evaluator
    ) {
        Map<String, Object> lab = new LinkedHashMap<>();

        lab.put("type", defaultIfBlank(
                getCellValue(row, headers, evaluator, "coreProduct.labs[].type"),
                "LAB"
        ));

        putIfNotBlank(lab, "cpscId",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].cpscId"));

        putIfNotBlank(lab, "gln",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].gln"));

        lab.put("alternateId",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].alternateId"));

        lab.put("name",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].name"));

        lab.put("addressLine1",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].addressLine1"));

        lab.put("addressLine2",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].addressLine2"));

        lab.put("aptNumber",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].aptNumber"));

        lab.put("city",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].city"));

        lab.put("stateProvince",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].stateProvince"));

        lab.put("country",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].country"));

        lab.put("postalCode",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].postalCode"));

        lab.put("phone",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].phone"));

        lab.put("email",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].email"));

        String citationCodesRaw = getCellValue(row, headers, evaluator, "coreProduct.labs[].citationCodes");
        lab.put("citationCodes", splitToList(citationCodesRaw));

        putIfNotBlank(lab, "testReportId",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].testReportId"));

        putIfNotBlank(lab, "testURL",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].testURL"));

        putIfNotBlank(lab, "testReportAccessKey",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].testReportAccessKey"));

        lab.put("isComponent", toBoolean(
                getCellValue(row, headers, evaluator, "coreProduct.labs[].isComponent")
        ));

        lab.put("componentDescription",
                getCellValue(row, headers, evaluator, "coreProduct.labs[].componentDescription"));

        return lab;
    }

    private static Map<String, Object> buildPoc(
            Row row,
            Map<String, List<Integer>> headers,
            FormulaEvaluator evaluator,
            String fallbackAltId
    ) {
        Map<String, Object> poc = new LinkedHashMap<>();

        String type = defaultIfBlank(
                getCellValue(row, headers, evaluator, "coreProduct.poc.type"),
                "Importer"
        );

        poc.put("type", type);

        putIfNotBlank(poc, "gln",
                getCellValue(row, headers, evaluator, "coreProduct.poc.gln"));

        putIfNotBlank(poc, "alternateId", defaultIfBlank(
                getCellValue(row, headers, evaluator, "coreProduct.poc.alternateId"),
                fallbackAltId
        ));

        putIfNotBlank(poc, "name",
                getCellValue(row, headers, evaluator, "coreProduct.poc.name"));

        if ("Other".equalsIgnoreCase(type)) {
            poc.put("addressLine1",
                    getCellValue(row, headers, evaluator, "coreProduct.poc.addressLine1"));

            poc.put("addressLine2",
                    getCellValue(row, headers, evaluator, "coreProduct.poc.addressLine2"));

            poc.put("aptNumber",
                    getCellValue(row, headers, evaluator, "coreProduct.poc.aptNumber"));

            poc.put("city",
                    getCellValue(row, headers, evaluator, "coreProduct.poc.city"));

            poc.put("stateProvince",
                    getCellValue(row, headers, evaluator, "coreProduct.poc.stateProvince"));

            poc.put("country",
                    getCellValue(row, headers, evaluator, "coreProduct.poc.country"));

            poc.put("postalCode",
                    getCellValue(row, headers, evaluator, "coreProduct.poc.postalCode"));
        }

        putIfNotBlank(poc, "phone",
                getCellValue(row, headers, evaluator, "coreProduct.poc.phone"));

        putIfNotBlank(poc, "email",
                getCellValue(row, headers, evaluator, "coreProduct.poc.email"));

        return poc;
    }

    private static Map<String, Object> buildManufacturerDirective(
            Row row,
            Map<String, List<Integer>> headers,
            FormulaEvaluator evaluator,
            String fallbackAltId
    ) {
        Map<String, Object> manufacturer = new LinkedHashMap<>();

        manufacturer.put("isNew", normalizeYN(
                getCellValue(row, headers, evaluator, "directives.manufacturer.isNew"),
                "N"
        ));

        putIfNotBlank(manufacturer, "gln",
                getCellValue(row, headers, evaluator, "directives.manufacturer.gln"));

        manufacturer.put("alternateId", defaultIfBlank(
                getCellValue(row, headers, evaluator, "directives.manufacturer.alternateId"),
                fallbackAltId
        ));

        return manufacturer;
    }

    private static Map<String, Object> buildLabDirective(
            Row row,
            Map<String, List<Integer>> headers,
            FormulaEvaluator evaluator
    ) {
        Map<String, Object> lab = new LinkedHashMap<>();

        lab.put("isNew", normalizeYN(
                getCellValue(row, headers, evaluator, "directives.labs[].isNew"),
                "N"
        ));

        putIfNotBlank(lab, "gln",
                getCellValue(row, headers, evaluator, "directives.labs[].gln"));

        lab.put("alternateId",
                getCellValue(row, headers, evaluator, "directives.labs[].alternateId"));

        return lab;
    }

    private static Map<String, Object> buildPocDirective(
            Row row,
            Map<String, List<Integer>> headers,
            FormulaEvaluator evaluator,
            String fallbackAltId
    ) {
        Map<String, Object> poc = new LinkedHashMap<>();

        if (row == null) {
            poc.put("isNew", "N");
            return poc;
        }

        poc.put("isNew", normalizeYN(
                getCellValue(row, headers, evaluator, "directives.poc.isNew"),
                "N"
        ));

        putIfNotBlank(poc, "gln",
                getCellValue(row, headers, evaluator, "directives.poc.gln"));

        putIfNotBlank(poc, "alternateId", defaultIfBlank(
                getCellValue(row, headers, evaluator, "directives.poc.alternateId"),
                fallbackAltId
        ));

        return poc;
    }

    private static List<Map<String, Object>> buildIdentifiers(
            Row row,
            Map<String, List<Integer>> headers,
            FormulaEvaluator evaluator
    ) {
        String[] types = {
                "GTIN",
                "UPC",
                "SKU",
                "Model #",
                "Serial #",
                "Registered #",
                "Alternate ID"
        };

        List<Map<String, Object>> identifiers = new ArrayList<>();

        for (String type : types) {
            String path = "coreProduct.identifiers[" + type + "].identifier";
            String value = getCellValue(row, headers, evaluator, path);

            if (!isBlank(value)) {
                Map<String, Object> identifier = new LinkedHashMap<>();
                identifier.put("identifier", value);
                identifier.put("identType", type);
                identifiers.add(identifier);
            }
        }

        return identifiers;
    }

    private static Map<String, List<String>> readExemptions(
            Sheet sheet,
            Map<String, List<Integer>> headers,
            FormulaEvaluator evaluator
    ) {
        Map<String, List<String>> result = new LinkedHashMap<>();

        for (int i = DATA_START_ROW_INDEX; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            String productId = getCellValue(row, headers, evaluator, "coreProduct.primaryProductId");
            String exemption = getCellValue(row, headers, evaluator, "coreProduct.exemptions[]");

            if (isBlank(productId) || isBlank(exemption)) {
                continue;
            }

            result.computeIfAbsent(productId, k -> new ArrayList<>())
                    .add(exemption);
        }

        return result;
    }

    private static Map<String, Row> indexRowsByKey(
            Sheet sheet,
            Map<String, List<Integer>> headers,
            FormulaEvaluator evaluator,
            String keyPath
    ) {
        Map<String, Row> result = new LinkedHashMap<>();

        for (int i = DATA_START_ROW_INDEX; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            String key = getCellValue(row, headers, evaluator, keyPath);

            if (!isBlank(key)) {
                result.put(key, row);
            }
        }

        return result;
    }

    private static Map<String, List<Row>> groupRowsByKey(
            Sheet sheet,
            Map<String, List<Integer>> headers,
            FormulaEvaluator evaluator,
            String keyPath
    ) {
        Map<String, List<Row>> result = new LinkedHashMap<>();

        for (int i = DATA_START_ROW_INDEX; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            String key = getCellValue(row, headers, evaluator, keyPath);

            if (!isBlank(key)) {
                result.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
            }
        }

        return result;
    }

    private static Map<String, List<Integer>> buildHeaderMap(
            Sheet sheet,
            FormulaEvaluator evaluator
    ) {
        Map<String, List<Integer>> headers = new LinkedHashMap<>();

        Row headerRow = sheet.getRow(API_HEADER_ROW_INDEX);
        if (headerRow == null) {
            throw new IllegalArgumentException("Sheet " + sheet.getSheetName() + " 第4行未找到 API 字段路径。");
        }

        for (Cell cell : headerRow) {
            String header = cellToString(cell, evaluator);

            if (!isBlank(header)) {
                headers.computeIfAbsent(header.trim(), k -> new ArrayList<>())
                        .add(cell.getColumnIndex());
            }
        }

        return headers;
    }

    private static String getCellValue(
            Row row,
            Map<String, List<Integer>> headers,
            FormulaEvaluator evaluator,
            String apiPath
    ) {
        List<Integer> exactColumns = headers.get(apiPath);

        if (exactColumns != null) {
            for (Integer columnIndex : exactColumns) {
                String value = cellToString(row.getCell(columnIndex), evaluator);
                if (!isBlank(value)) {
                    return value;
                }
            }
        }

        for (Map.Entry<String, List<Integer>> entry : headers.entrySet()) {
            String header = entry.getKey();

            if (header.contains(apiPath)) {
                for (Integer columnIndex : entry.getValue()) {
                    String value = cellToString(row.getCell(columnIndex), evaluator);
                    if (!isBlank(value)) {
                        return value;
                    }
                }
            }
        }

        return "";
    }

    private static String cellToString(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) {
            return "";
        }

        DataFormatter formatter = new DataFormatter(Locale.US);
        return formatter.formatCellValue(cell, evaluator).trim();
    }

    private static Sheet getRequiredSheet(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);

        if (sheet == null) {
            throw new IllegalArgumentException("Excel 中找不到 Sheet：" + sheetName);
        }

        return sheet;
    }

    private static void putRequiredString(Map<String, Object> map, String key, String value) {
        map.put(key, defaultIfBlank(value, ""));
    }

    private static void putIfNotBlank(Map<String, Object> map, String key, String value) {
        if (!isBlank(value)) {
            map.put(key, value);
        }
    }

    private static List<String> splitToList(String value) {
        List<String> result = new ArrayList<>();

        if (isBlank(value)) {
            return result;
        }

        String[] parts = value.split("[,，\\n]+");

        for (String part : parts) {
            String item = part.trim();

            if (!item.isEmpty()) {
                result.add(item);
            }
        }

        return result;
    }

    private static boolean toBoolean(String value) {
        if (isBlank(value)) {
            return false;
        }

        String v = value.trim().toLowerCase(Locale.ROOT);

        return v.equals("true")
                || v.equals("yes")
                || v.equals("y")
                || v.equals("1")
                || v.equals("是");
    }

    private static String normalizeYN(String value, String defaultValue) {
        if (isBlank(value)) {
            return defaultValue;
        }

        String v = value.trim();
        String upper = v.toUpperCase(Locale.ROOT);

        if (upper.equals("Y")
                || upper.equals("YES")
                || upper.equals("TRUE")
                || v.equals("是")
                || v.contains("更新")
                || v.contains("新建")) {
            return "Y";
        }

        if (upper.equals("N")
                || upper.equals("NO")
                || upper.equals("FALSE")
                || v.equals("否")
                || v.contains("新增")
                || v.contains("已存在")) {
            return "N";
        }

        return v;
    }

    private static String defaultIfBlank(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}