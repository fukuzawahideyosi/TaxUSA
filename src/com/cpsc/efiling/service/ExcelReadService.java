package com.cpsc.efiling.service;

import com.cpsc.efiling.model.*;
import com.cpsc.efiling.util.StringUtil;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class ExcelReadService {
    private static final int API_HEADER_ROW_INDEX = 3;   // Excel 第4行是 API字段路径
    private static final int DATA_START_ROW_INDEX = 5;   // Excel 第6行开始是数据

    public ImportData read(File excelFile) throws Exception {
        try (InputStream in = new FileInputStream(excelFile);
             Workbook workbook = WorkbookFactory.create(in)) {

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

            Map<String, ManufacturerData> manufacturers = readManufacturers(manufacturerSheet, manufacturerHeaders, evaluator);
            Map<String, PocData> pocs = readPocs(pocSheet, pocHeaders, evaluator);
            Map<String, List<LabData>> labsByProduct = readLabs(labSheet, labHeaders, evaluator);
            Map<String, List<String>> exemptionsByProduct = readExemptions(exemptionSheet, exemptionHeaders, evaluator);

            ImportData importData = new ImportData();

            for (int i = DATA_START_ROW_INDEX; i <= productSheet.getLastRowNum(); i++) {
                Row row = productSheet.getRow(i);
                if (row == null) {
                    continue;
                }

                String productId = getCellValue(row, productHeaders, evaluator, "coreProduct.primaryProductId");
                if (StringUtil.isBlank(productId)) {
                    continue;
                }

                ProductData product = new ProductData();
                product.setProductUpdate(StringUtil.normalizeYN(getCellValue(row, productHeaders, evaluator, "directives.productUpdate"), "N"));
                product.setVersionId(StringUtil.defaultIfBlank(getCellValue(row, productHeaders, evaluator, "coreProduct.versionId"), "V1"));
                product.setVersionIdToUpdate(getCellValue(row, productHeaders, evaluator, "directives.versionIdToUpdate"));
                product.setPrimaryProductId(productId);
                product.setPrimaryProductIdType(StringUtil.defaultIfBlank(getCellValue(row, productHeaders, evaluator, "coreProduct.primaryProductIdType"), "Model #"));
                product.setCertificateType(StringUtil.defaultIfBlank(getCellValue(row, productHeaders, evaluator, "coreProduct.certificateType"), "GCC"));
                product.setName(getCellValue(row, productHeaders, evaluator, "coreProduct.name"));
                product.setTradeBrandName(getCellValue(row, productHeaders, evaluator, "coreProduct.tradeBrandName"));
                product.setDescription(getCellValue(row, productHeaders, evaluator, "coreProduct.description"));
                product.setColor(getCellValue(row, productHeaders, evaluator, "coreProduct.color"));
                product.setStyle(getCellValue(row, productHeaders, evaluator, "coreProduct.style"));
                product.setManufactureDate(getCellValue(row, productHeaders, evaluator, "coreProduct.manufactureDate"));
                product.setProductionStartDate(getCellValue(row, productHeaders, evaluator, "coreProduct.productionStartDate"));
                product.setProductionEndDate(getCellValue(row, productHeaders, evaluator, "coreProduct.productionEndDate"));
                product.setLotNumber(getCellValue(row, productHeaders, evaluator, "coreProduct.lotNumber"));
                product.setLotNumberAssignedBy(getCellValue(row, productHeaders, evaluator, "coreProduct.lotNumberAssignedBy"));
                product.setLastTestDate(getCellValue(row, productHeaders, evaluator, "coreProduct.lastTestDate"));
                product.setManufacturerAlternateId(getCellValue(row, productHeaders, evaluator, "coreProduct.manufacturer.alternateId"));
                product.setPocCode(getCellValue(row, productHeaders, evaluator, "coreProduct.poc.alternateId"));
                product.setNotes(getCellValue(row, productHeaders, evaluator, "internal.notes"));
                product.setIdentifiers(readIdentifiers(row, productHeaders, evaluator));

                ManufacturerData manufacturer = manufacturers.get(product.getManufacturerAlternateId());
                if (manufacturer == null) {
                    throw new IllegalArgumentException("产品 " + productId + " 引用了制造商编号 " + product.getManufacturerAlternateId() + "，但 02_制造商信息 中找不到。 ");
                }
                product.setManufacturer(manufacturer);

                if (!StringUtil.isBlank(product.getPocCode())) {
                    PocData poc = pocs.get(product.getPocCode());
                    if (poc == null) {
                        throw new IllegalArgumentException("产品 " + productId + " 引用了 POC 编号 " + product.getPocCode() + "，但 04_联系人POC 中找不到。 ");
                    }
                    product.setPoc(poc);
                }

                product.setLabs(labsByProduct.getOrDefault(productId, new ArrayList<LabData>()));
                product.setExemptions(exemptionsByProduct.getOrDefault(productId, new ArrayList<String>()));

                importData.getProducts().add(product);
            }

            if (importData.getProducts().isEmpty()) {
                throw new IllegalArgumentException("Excel 中没有可导入的产品数据。请检查 01_产品基本信息 第6行以后是否填写了产品唯一ID。 ");
            }

            return importData;
        }
    }

    private Map<String, ManufacturerData> readManufacturers(Sheet sheet, Map<String, List<Integer>> headers, FormulaEvaluator evaluator) {
        Map<String, ManufacturerData> map = new LinkedHashMap<String, ManufacturerData>();
        for (int i = DATA_START_ROW_INDEX; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String alternateId = getCellValue(row, headers, evaluator, "coreProduct.manufacturer.alternateId");
            if (StringUtil.isBlank(alternateId)) continue;

            ManufacturerData m = new ManufacturerData();
            m.setAlternateId(alternateId);
            m.setIsNew(StringUtil.normalizeYN(getCellValue(row, headers, evaluator, "directives.manufacturer.isNew"), "N"));
            m.setGln(getCellValue(row, headers, evaluator, "coreProduct.manufacturer.gln"));
            m.setSbmId(getCellValue(row, headers, evaluator, "coreProduct.manufacturer.sbmId"));
            m.setName(getCellValue(row, headers, evaluator, "coreProduct.manufacturer.name"));
            m.setAddressLine1(getCellValue(row, headers, evaluator, "coreProduct.manufacturer.addressLine1"));
            m.setAddressLine2(getCellValue(row, headers, evaluator, "coreProduct.manufacturer.addressLine2"));
            m.setAptNumber(getCellValue(row, headers, evaluator, "coreProduct.manufacturer.aptNumber"));
            m.setCity(getCellValue(row, headers, evaluator, "coreProduct.manufacturer.city"));
            m.setStateProvince(getCellValue(row, headers, evaluator, "coreProduct.manufacturer.stateProvince"));
            m.setCountry(getCellValue(row, headers, evaluator, "coreProduct.manufacturer.country"));
            m.setPostalCode(getCellValue(row, headers, evaluator, "coreProduct.manufacturer.postalCode"));
            m.setPhone(getCellValue(row, headers, evaluator, "coreProduct.manufacturer.phone"));
            m.setEmail(getCellValue(row, headers, evaluator, "coreProduct.manufacturer.email"));
            map.put(alternateId, m);
        }
        return map;
    }

    private Map<String, PocData> readPocs(Sheet sheet, Map<String, List<Integer>> headers, FormulaEvaluator evaluator) {
        Map<String, PocData> map = new LinkedHashMap<String, PocData>();
        for (int i = DATA_START_ROW_INDEX; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String pocCode = getCellValue(row, headers, evaluator, "coreProduct.poc.alternateId");
            if (StringUtil.isBlank(pocCode)) continue;

            PocData p = new PocData();
            p.setPocCode(pocCode);
            p.setAlternateId(pocCode);
            p.setIsNew(StringUtil.normalizeYN(getCellValue(row, headers, evaluator, "directives.poc.isNew"), "N"));
            p.setType(StringUtil.defaultIfBlank(getCellValue(row, headers, evaluator, "coreProduct.poc.type"), "Importer"));
            p.setGln(getCellValue(row, headers, evaluator, "coreProduct.poc.gln"));
            p.setName(getCellValue(row, headers, evaluator, "coreProduct.poc.name"));
            p.setAddressLine1(getCellValue(row, headers, evaluator, "coreProduct.poc.addressLine1"));
            p.setAddressLine2(getCellValue(row, headers, evaluator, "coreProduct.poc.addressLine2"));
            p.setAptNumber(getCellValue(row, headers, evaluator, "coreProduct.poc.aptNumber"));
            p.setCity(getCellValue(row, headers, evaluator, "coreProduct.poc.city"));
            p.setStateProvince(getCellValue(row, headers, evaluator, "coreProduct.poc.stateProvince"));
            p.setCountry(getCellValue(row, headers, evaluator, "coreProduct.poc.country"));
            p.setPostalCode(getCellValue(row, headers, evaluator, "coreProduct.poc.postalCode"));
            p.setPhone(getCellValue(row, headers, evaluator, "coreProduct.poc.phone"));
            p.setEmail(getCellValue(row, headers, evaluator, "coreProduct.poc.email"));
            map.put(pocCode, p);
        }
        return map;
    }

    private Map<String, List<LabData>> readLabs(Sheet sheet, Map<String, List<Integer>> headers, FormulaEvaluator evaluator) {
        Map<String, List<LabData>> map = new LinkedHashMap<String, List<LabData>>();
        for (int i = DATA_START_ROW_INDEX; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String productId = getCellValue(row, headers, evaluator, "coreProduct.primaryProductId");
            if (StringUtil.isBlank(productId)) continue;

            LabData lab = new LabData();
            lab.setProductId(productId);
            lab.setAlternateId(getCellValue(row, headers, evaluator, "coreProduct.labs[].alternateId"));
            lab.setIsNew(StringUtil.normalizeYN(getCellValue(row, headers, evaluator, "directives.labs[].isNew"), "N"));
            lab.setType(StringUtil.defaultIfBlank(getCellValue(row, headers, evaluator, "coreProduct.labs[].type"), "LAB"));
            lab.setCpscId(getCellValue(row, headers, evaluator, "coreProduct.labs[].cpscId"));
            lab.setGln(getCellValue(row, headers, evaluator, "coreProduct.labs[].gln"));
            lab.setName(getCellValue(row, headers, evaluator, "coreProduct.labs[].name"));
            lab.setAddressLine1(getCellValue(row, headers, evaluator, "coreProduct.labs[].addressLine1"));
            lab.setAddressLine2(getCellValue(row, headers, evaluator, "coreProduct.labs[].addressLine2"));
            lab.setAptNumber(getCellValue(row, headers, evaluator, "coreProduct.labs[].aptNumber"));
            lab.setCity(getCellValue(row, headers, evaluator, "coreProduct.labs[].city"));
            lab.setStateProvince(getCellValue(row, headers, evaluator, "coreProduct.labs[].stateProvince"));
            lab.setCountry(getCellValue(row, headers, evaluator, "coreProduct.labs[].country"));
            lab.setPostalCode(getCellValue(row, headers, evaluator, "coreProduct.labs[].postalCode"));
            lab.setPhone(getCellValue(row, headers, evaluator, "coreProduct.labs[].phone"));
            lab.setEmail(getCellValue(row, headers, evaluator, "coreProduct.labs[].email"));
            lab.setCitationCodes(StringUtil.splitList(getCellValue(row, headers, evaluator, "coreProduct.labs[].citationCodes")));
            lab.setTestReportId(getCellValue(row, headers, evaluator, "coreProduct.labs[].testReportId"));
            lab.setTestURL(getCellValue(row, headers, evaluator, "coreProduct.labs[].testURL"));
            lab.setTestReportAccessKey(getCellValue(row, headers, evaluator, "coreProduct.labs[].testReportAccessKey"));
            lab.setComponent(StringUtil.toBoolean(getCellValue(row, headers, evaluator, "coreProduct.labs[].isComponent")));
            lab.setComponentDescription(getCellValue(row, headers, evaluator, "coreProduct.labs[].componentDescription"));
            map.computeIfAbsent(productId, k -> new ArrayList<LabData>()).add(lab);
        }
        return map;
    }

    private Map<String, List<String>> readExemptions(Sheet sheet, Map<String, List<Integer>> headers, FormulaEvaluator evaluator) {
        Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
        for (int i = DATA_START_ROW_INDEX; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String productId = getCellValue(row, headers, evaluator, "coreProduct.primaryProductId");
            String exemption = getCellValue(row, headers, evaluator, "coreProduct.exemptions[]");
            if (StringUtil.isBlank(productId) || StringUtil.isBlank(exemption)) continue;
            map.computeIfAbsent(productId, k -> new ArrayList<String>()).add(exemption);
        }
        return map;
    }

    private List<IdentifierData> readIdentifiers(Row row, Map<String, List<Integer>> headers, FormulaEvaluator evaluator) {
        String[] types = {"GTIN", "UPC", "SKU", "Model #", "Serial #", "Registered #", "Alternate ID"};
        List<IdentifierData> identifiers = new ArrayList<IdentifierData>();
        for (String type : types) {
            String path = "coreProduct.identifiers[" + type + "].identifier";
            String value = getCellValue(row, headers, evaluator, path);
            if (!StringUtil.isBlank(value)) {
                identifiers.add(new IdentifierData(type, value));
            }
        }
        return identifiers;
    }

    private Map<String, List<Integer>> buildHeaderMap(Sheet sheet, FormulaEvaluator evaluator) {
        Map<String, List<Integer>> headers = new LinkedHashMap<String, List<Integer>>();
        Row headerRow = sheet.getRow(API_HEADER_ROW_INDEX);
        if (headerRow == null) {
            throw new IllegalArgumentException("Sheet " + sheet.getSheetName() + " 第4行未找到 API 字段路径。 ");
        }
        for (Cell cell : headerRow) {
            String header = cellToString(cell, evaluator);
            if (!StringUtil.isBlank(header)) {
                headers.computeIfAbsent(header.trim(), k -> new ArrayList<Integer>()).add(cell.getColumnIndex());
            }
        }
        return headers;
    }

    private String getCellValue(Row row, Map<String, List<Integer>> headers, FormulaEvaluator evaluator, String apiPath) {
        List<Integer> exactColumns = headers.get(apiPath);
        if (exactColumns != null) {
            for (Integer columnIndex : exactColumns) {
                String value = cellToString(row.getCell(columnIndex), evaluator);
                if (!StringUtil.isBlank(value)) {
                    return value;
                }
            }
        }
        for (Map.Entry<String, List<Integer>> entry : headers.entrySet()) {
            String header = entry.getKey();
            if (header.contains(apiPath)) {
                for (Integer columnIndex : entry.getValue()) {
                    String value = cellToString(row.getCell(columnIndex), evaluator);
                    if (!StringUtil.isBlank(value)) {
                        return value;
                    }
                }
            }
        }
        return "";
    }

    private String cellToString(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) {
            return "";
        }
        DataFormatter formatter = new DataFormatter(Locale.US);
        return formatter.formatCellValue(cell, evaluator).trim();
    }

    private Sheet getRequiredSheet(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new IllegalArgumentException("Excel 中找不到 Sheet：" + sheetName);
        }
        return sheet;
    }
}
