package com.cpsc.efiling.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cpsc.efiling.model.LabView;
import com.cpsc.efiling.model.ProductView;
import com.cpsc.efiling.model.ValidationError;
import com.cpsc.efiling.util.StringUtil;

public class ApiValidationService {
    private static final Logger log = LogManager.getLogger(ApiValidationService.class);
    private static final Pattern MONTH_YEAR = Pattern.compile("^(0[1-9]|1[0-2])/[0-9]{4}$");
    private static final Pattern DATE = Pattern.compile("^(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])/[0-9]{4}$");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private static final Set<String> PRODUCT_ID_TYPES = new HashSet<String>(Arrays.asList(
            "GTIN", "UPC", "SKU",
            "Model #", "Model Number",
            "Serial #", "Serial Number",
            "Registered #", "Registered Number",
            "Alternate ID", "Alternate Identifier"
    ));
    private static final Set<String> CERT_TYPES = new HashSet<String>(Arrays.asList("GCC", "CPC"));
    private static final Set<String> LOT_ASSIGNED_BY = new HashSet<String>(Arrays.asList("Manufacturer", "Seller"));
    private static final Set<String> LAB_TYPES = new HashSet<String>(Arrays.asList("LAB", "ITL"));
    private static final Set<String> POC_TYPES = new HashSet<String>(Arrays.asList("Importer", "Manufacturer", "Laboratory", "Broker", "Other"));

    public List<ValidationError> validate(ProductView p) {
        log.debug("开始校验产品。productId={}, versionId={}", p == null ? null : p.getPrimaryProductId(), p == null ? null : p.getVersionId());
        List<ValidationError> errors = new ArrayList<ValidationError>();

        required(errors, "coreProduct.versionId", p.getVersionId(), 19, "证书版本号必填，最长19字符。 ");
        required(errors, "coreProduct.primaryProductId", p.getPrimaryProductId(), 19, "产品唯一ID必填，最长19字符。 ");
        required(errors, "coreProduct.primaryProductIdType", p.getPrimaryProductIdType(), 30, "产品ID类型必填。 ");
        enumValue(errors, "coreProduct.primaryProductIdType", p.getPrimaryProductIdType(), PRODUCT_ID_TYPES, "产品ID类型只能是 GTIN、UPC、SKU、Model #/Model Number、Serial #/Serial Number、Registered #/Registered Number、Alternate ID/Alternate Identifier。 ");
        required(errors, "coreProduct.certificateType", p.getCertificateType(), 10, "证书类型必填。 ");
        enumValue(errors, "coreProduct.certificateType", p.getCertificateType(), CERT_TYPES, "证书类型只能是 GCC 或 CPC。 ");
        required(errors, "coreProduct.name", p.getName(), 250, "产品名称必填，最长250字符。 ");
        max(errors, "coreProduct.tradeBrandName", p.getTradeBrandName(), 50, "品牌名称最长50字符。 ");
        max(errors, "coreProduct.description", p.getDescription(), 250, "产品描述最长250字符。 ");
        max(errors, "coreProduct.color", p.getColor(), 50, "颜色最长50字符。 ");
        max(errors, "coreProduct.style", p.getStyle(), 50, "款式最长50字符。 ");

        required(errors, "coreProduct.manufactureDate", p.getManufactureDate(), 10, "生产月份必填，格式 MM/YYYY，例如 06/2026。 ");
        pattern(errors, "coreProduct.manufactureDate", p.getManufactureDate(), MONTH_YEAR, "生产月份格式错误，应为 MM/YYYY，例如 06/2026。 ");
        if (!StringUtil.isBlank(p.getProductionStartDate())) {
            pattern(errors, "coreProduct.productionStartDate", p.getProductionStartDate(), DATE, "批次生产开始日期格式错误，应为 MM/DD/YYYY。 ");
        }
        if (!StringUtil.isBlank(p.getProductionEndDate())) {
            pattern(errors, "coreProduct.productionEndDate", p.getProductionEndDate(), DATE, "批次生产结束日期格式错误，应为 MM/DD/YYYY。 ");
        }
        required(errors, "coreProduct.lastTestDate", p.getLastTestDate(), 10, "最近测试日期必填，格式 MM/DD/YYYY，例如 06/19/2026。 ");
        pattern(errors, "coreProduct.lastTestDate", p.getLastTestDate(), DATE, "最近测试日期格式错误，应为 MM/DD/YYYY，例如 06/19/2026。 ");

        max(errors, "coreProduct.lotNumber", p.getLotNumber(), 20, "批次号最长20字符。 ");
        if (!StringUtil.isBlank(p.getLotNumber()) && StringUtil.isBlank(p.getLotNumberAssignedBy())) {
            errors.add(new ValidationError("coreProduct.lotNumberAssignedBy", "已填写批次号时，批号分配方 lotNumberAssignedBy 必填。 "));
        }
        if (!StringUtil.isBlank(p.getLotNumberAssignedBy())) {
            enumValue(errors, "coreProduct.lotNumberAssignedBy", p.getLotNumberAssignedBy(), LOT_ASSIGNED_BY, "批号分配方只能是 Manufacturer 或 Seller。 ");
        }

        required(errors, "coreProduct.manufacturer.alternateId", p.getManufacturerAlternateId(), 50, "制造商编号必填，最长50字符。 ");
        required(errors, "coreProduct.manufacturer.name", p.getManufacturerName(), 100, "制造商名称必填，最长100字符。 ");
        max(errors, "coreProduct.manufacturer.country", p.getManufacturerCountry(), 50, "制造商国家最长50字符，建议填写英文国家名。 ");
        if (!StringUtil.isBlank(p.getManufacturerEmail())) {
            pattern(errors, "coreProduct.manufacturer.email", p.getManufacturerEmail(), EMAIL, "制造商邮箱格式不正确。 ");
        }

        if ("Y".equalsIgnoreCase(p.getProductUpdate()) && StringUtil.isBlank(p.getVersionIdToUpdate())) {
            errors.add(new ValidationError("directives.versionIdToUpdate", "productUpdate=Y 时，更新目标版本号 versionIdToUpdate 必填。 "));
        }

        if (p.getLabs().isEmpty() && p.getExemptions().isEmpty()) {
            errors.add(new ValidationError("coreProduct.labs / coreProduct.exemptions", "产品至少需要填写实验室检测信息或法规豁免信息，否则可能触发 2026 错误。 "));
        }

        for (int i = 0; i < p.getLabs().size(); i++) {
            LabView lab = p.getLabs().get(i);
            String prefix = "coreProduct.labs[" + i + "].";
            required(errors, prefix + "type", lab.getLabType(), 10, "实验室类型必填，只能是 LAB 或 ITL。 ");
            enumValue(errors, prefix + "type", lab.getLabType(), LAB_TYPES, "实验室类型只能是 LAB 或 ITL。 ");
            if ("ITL".equalsIgnoreCase(lab.getLabType()) && StringUtil.isBlank(lab.getCpscId())) {
                errors.add(new ValidationError(prefix + "cpscId", "实验室类型为 ITL 时，CPSC Lab ID 必填。 "));
            }
            if ("LAB".equalsIgnoreCase(lab.getLabType()) && StringUtil.isBlank(lab.getLabAlternateId())) {
                errors.add(new ValidationError(prefix + "alternateId", "实验室类型为 LAB 时，实验室编号 alternateId 建议必填。 "));
            }
            required(errors, prefix + "citationCodes", lab.getCitationCodes(), 200, "每条实验室记录至少需要一个 citationCodes。 ");
            max(errors, prefix + "testReportId", lab.getTestReportId(), 400, "测试报告编号最长400字符。 ");
            max(errors, prefix + "testURL", lab.getTestUrl(), 400, "测试报告URL最长400字符。 ");
            max(errors, prefix + "componentDescription", lab.getComponentDescription(), 250, "部件说明最长250字符。 ");
        }

        if (!StringUtil.isBlank(p.getPocType())) {
            enumValue(errors, "coreProduct.poc.type", p.getPocType(), POC_TYPES, "POC类型只能是 Importer、Manufacturer、Laboratory、Broker 或 Other。 ");
        }
        if (!StringUtil.isBlank(p.getPocEmail())) {
            pattern(errors, "coreProduct.poc.email", p.getPocEmail(), EMAIL, "POC邮箱格式不正确。 ");
        }

        log.debug("产品校验结束。productId={}, errorCount={}", p == null ? null : p.getPrimaryProductId(), errors.size());
        return errors;
    }

    private void required(List<ValidationError> errors, String field, String value, int maxLength, String message) {
        if (StringUtil.isBlank(value)) {
            errors.add(new ValidationError(field, message));
            return;
        }
        max(errors, field, value, maxLength, message);
    }

    private void max(List<ValidationError> errors, String field, String value, int maxLength, String message) {
        if (!StringUtil.isBlank(value) && value.length() > maxLength) {
            errors.add(new ValidationError(field, message));
        }
    }

    private void enumValue(List<ValidationError> errors, String field, String value, Set<String> allowed, String message) {
        if (!StringUtil.isBlank(value) && !allowed.contains(value)) {
            errors.add(new ValidationError(field, message));
        }
    }

    private void pattern(List<ValidationError> errors, String field, String value, Pattern pattern, String message) {
        if (!StringUtil.isBlank(value) && !pattern.matcher(value).matches()) {
            errors.add(new ValidationError(field, message));
        }
    }
}
