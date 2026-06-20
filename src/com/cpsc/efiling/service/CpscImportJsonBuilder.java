package com.cpsc.efiling.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.cpsc.efiling.model.*;
import com.cpsc.efiling.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.util.*;

public class CpscImportJsonBuilder {
    private static final Logger log = LogManager.getLogger(CpscImportJsonBuilder.class);

    /**
     * 兼容你前面提到的方法名：直接从 Excel 文件生成 /import JSON。
     */
    public String buildImportJsonFromExcel(String excelPath) throws Exception {
        ImportData data = new ExcelReadService().read(new File(excelPath));
        return buildJson(data);
    }

    public String buildJson(ImportData importData) throws Exception {
        Map<String, Object> root = new LinkedHashMap<String, Object>();
        List<Map<String, Object>> productList = new ArrayList<Map<String, Object>>();

        for (ProductData product : importData.getProducts()) {
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("coreProduct", buildCoreProduct(product));
            item.put("directives", buildDirectives(product));
            productList.add(item);
        }

        root.put("productList", productList);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsString(root);
    }

    private Map<String, Object> buildCoreProduct(ProductData p) {
        Map<String, Object> core = new LinkedHashMap<String, Object>();

        core.put("versionId", StringUtil.defaultIfBlank(p.getVersionId(), "V1"));
        core.put("primaryProductId", p.getPrimaryProductId());
        core.put("primaryProductIdType", StringUtil.defaultIfBlank(p.getPrimaryProductIdType(), "Model #"));
        core.put("certificateType", StringUtil.defaultIfBlank(p.getCertificateType(), "GCC"));

        putIfNotBlank(core, "name", p.getName());
        putIfNotBlank(core, "tradeBrandName", p.getTradeBrandName());
        putIfNotBlank(core, "description", p.getDescription());
        putIfNotBlank(core, "color", p.getColor());
        putIfNotBlank(core, "style", p.getStyle());

        if (p.getIdentifiers() != null && !p.getIdentifiers().isEmpty()) {
            List<Map<String, Object>> identifiers = new ArrayList<Map<String, Object>>();
            for (IdentifierData identifier : p.getIdentifiers()) {
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("identifier", identifier.getIdentifier());
                map.put("identType", identifier.getIdentType());
                identifiers.add(map);
            }
            core.put("identifiers", identifiers);
        }

        core.put("manufacturer", buildManufacturer(p.getManufacturer()));
        putIfNotBlank(core, "manufactureDate", p.getManufactureDate());
        putIfNotBlank(core, "productionStartDate", p.getProductionStartDate());
        putIfNotBlank(core, "productionEndDate", p.getProductionEndDate());
        putIfNotBlank(core, "lotNumber", p.getLotNumber());
        putIfNotBlank(core, "lotNumberAssignedBy", p.getLotNumberAssignedBy());
        putIfNotBlank(core, "lastTestDate", p.getLastTestDate());

        if (p.getLabs() != null && !p.getLabs().isEmpty()) {
            List<Map<String, Object>> labs = new ArrayList<Map<String, Object>>();
            for (LabData lab : p.getLabs()) {
                labs.add(buildLab(lab));
            }
            core.put("labs", labs);
        }

        if (p.getExemptions() != null && !p.getExemptions().isEmpty()) {
            core.put("exemptions", p.getExemptions());
        }

        if (p.getPoc() != null) {
            core.put("poc", buildPoc(p.getPoc()));
        } else {
            Map<String, Object> poc = new LinkedHashMap<String, Object>();
            poc.put("type", "Importer");
            core.put("poc", poc);
        }

        return core;
    }

    private Map<String, Object> buildManufacturer(ManufacturerData m) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        if (m == null) {
            return map;
        }
        putIfNotBlank(map, "gln", m.getGln());
        putIfNotBlank(map, "alternateId", m.getAlternateId());
        putIfNotBlank(map, "sbmId", m.getSbmId());
        putIfNotBlank(map, "name", m.getName());
        map.put("addressLine1", StringUtil.trimToEmpty(m.getAddressLine1()));
        map.put("addressLine2", StringUtil.trimToEmpty(m.getAddressLine2()));
        map.put("aptNumber", StringUtil.trimToEmpty(m.getAptNumber()));
        map.put("city", StringUtil.trimToEmpty(m.getCity()));
        map.put("stateProvince", StringUtil.trimToEmpty(m.getStateProvince()));
        map.put("country", StringUtil.trimToEmpty(m.getCountry()));
        map.put("postalCode", StringUtil.trimToEmpty(m.getPostalCode()));
        map.put("phone", StringUtil.trimToEmpty(m.getPhone()));
        map.put("email", StringUtil.trimToEmpty(m.getEmail()));
        return map;
    }

    private Map<String, Object> buildLab(LabData l) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("type", StringUtil.defaultIfBlank(l.getType(), "LAB"));
        putIfNotBlank(map, "cpscId", l.getCpscId());
        putIfNotBlank(map, "gln", l.getGln());
        putIfNotBlank(map, "alternateId", l.getAlternateId());
        putIfNotBlank(map, "name", l.getName());
        map.put("addressLine1", StringUtil.trimToEmpty(l.getAddressLine1()));
        map.put("addressLine2", StringUtil.trimToEmpty(l.getAddressLine2()));
        map.put("aptNumber", StringUtil.trimToEmpty(l.getAptNumber()));
        map.put("city", StringUtil.trimToEmpty(l.getCity()));
        map.put("stateProvince", StringUtil.trimToEmpty(l.getStateProvince()));
        map.put("country", StringUtil.trimToEmpty(l.getCountry()));
        map.put("postalCode", StringUtil.trimToEmpty(l.getPostalCode()));
        map.put("phone", StringUtil.trimToEmpty(l.getPhone()));
        map.put("email", StringUtil.trimToEmpty(l.getEmail()));
        map.put("citationCodes", l.getCitationCodes());
        putIfNotBlank(map, "testReportId", l.getTestReportId());
        putIfNotBlank(map, "testURL", l.getTestURL());
        putIfNotBlank(map, "testReportAccessKey", l.getTestReportAccessKey());
        map.put("isComponent", l.isComponent());
        map.put("componentDescription", StringUtil.trimToEmpty(l.getComponentDescription()));
        return map;
    }

    private Map<String, Object> buildPoc(PocData p) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("type", StringUtil.defaultIfBlank(p.getType(), "Importer"));
        putIfNotBlank(map, "gln", p.getGln());
        putIfNotBlank(map, "alternateId", p.getAlternateId());
        putIfNotBlank(map, "name", p.getName());
        if ("Other".equalsIgnoreCase(p.getType())) {
            map.put("addressLine1", StringUtil.trimToEmpty(p.getAddressLine1()));
            map.put("addressLine2", StringUtil.trimToEmpty(p.getAddressLine2()));
            map.put("aptNumber", StringUtil.trimToEmpty(p.getAptNumber()));
            map.put("city", StringUtil.trimToEmpty(p.getCity()));
            map.put("stateProvince", StringUtil.trimToEmpty(p.getStateProvince()));
            map.put("country", StringUtil.trimToEmpty(p.getCountry()));
            map.put("postalCode", StringUtil.trimToEmpty(p.getPostalCode()));
        }
        putIfNotBlank(map, "phone", p.getPhone());
        putIfNotBlank(map, "email", p.getEmail());
        return map;
    }

    private Map<String, Object> buildDirectives(ProductData p) {
        Map<String, Object> directives = new LinkedHashMap<String, Object>();
        String update = StringUtil.normalizeYN(p.getProductUpdate(), "N");
        directives.put("productUpdate", update);
        if ("Y".equals(update)) {
            putIfNotBlank(directives, "versionIdToUpdate", p.getVersionIdToUpdate());
        }

        Map<String, Object> manufacturer = new LinkedHashMap<String, Object>();
        ManufacturerData m = p.getManufacturer();
        manufacturer.put("isNew", m == null ? "N" : StringUtil.normalizeYN(m.getIsNew(), "N"));
        if (m != null) {
            putIfNotBlank(manufacturer, "gln", m.getGln());
            putIfNotBlank(manufacturer, "alternateId", m.getAlternateId());
        }
        directives.put("manufacturer", manufacturer);

        if (p.getLabs() != null && !p.getLabs().isEmpty()) {
            List<Map<String, Object>> labDirectives = new ArrayList<Map<String, Object>>();
            for (LabData l : p.getLabs()) {
                Map<String, Object> lab = new LinkedHashMap<String, Object>();
                lab.put("isNew", StringUtil.normalizeYN(l.getIsNew(), "N"));
                putIfNotBlank(lab, "gln", l.getGln());
                putIfNotBlank(lab, "alternateId", l.getAlternateId());
                labDirectives.add(lab);
            }
            directives.put("labs", labDirectives);
        }

        Map<String, Object> poc = new LinkedHashMap<String, Object>();
        if (p.getPoc() == null) {
            poc.put("isNew", "N");
        } else {
            poc.put("isNew", StringUtil.normalizeYN(p.getPoc().getIsNew(), "N"));
            putIfNotBlank(poc, "gln", p.getPoc().getGln());
            putIfNotBlank(poc, "alternateId", p.getPoc().getAlternateId());
        }
        directives.put("poc", poc);

        return directives;
    }

    private void putIfNotBlank(Map<String, Object> map, String key, String value) {
        if (!StringUtil.isBlank(value)) {
            map.put(key, value.trim());
        }
    }
}
