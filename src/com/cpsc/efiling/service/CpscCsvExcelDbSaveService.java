package com.cpsc.efiling.service;

import com.cpsc.efiling.util.DbUtil;
import com.cpsc.efiling.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

/**
 * 将 importCsv.jsp 读取到的客户Excel数据，同时保存到本地数据库。
 *
 * 保存范围：
 * 1. CPSC_eFiling_import_batch
 * 2. CPSC_eFiling_import_batch_item
 * 3. CPSC_eFiling_product_certificate
 * 4. CPSC_eFiling_manufacturer
 * 5. CPSC_eFiling_lab / CPSC_eFiling_product_lab / CPSC_eFiling_product_lab_citation
 * 6. CPSC_eFiling_poc（客户有填写POC信息时才保存）
 * 7. CPSC_eFiling_product_identifier
 */
public class CpscCsvExcelDbSaveService {

    private static final Logger log = LogManager.getLogger(CpscCsvExcelDbSaveService.class);

    public long saveCsvAndProductDetails(
            String certifierId,
            String collectionId,
            String originalFileName,
            String generatedFileName,
            File csvFile,
            List<Map<String, String>> rows
    ) throws SQLException, IOException {

        log.info("开始保存CSV和产品明细到DB。certifierId={}, collectionId={}, originalFileName={}, generatedFileName={}, rowCount={}",
                certifierId, collectionId, originalFileName, generatedFileName, rows == null ? 0 : rows.size());

        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("没有可保存到数据库的产品数据。 ");
        }

        String csvText = readFile(csvFile);

        try (Connection conn = DbUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long batchId = insertImportBatch(conn, certifierId, collectionId, originalFileName, generatedFileName, csvText, rows.size());
                log.info("导入批次保存成功。batchId={}", batchId);

                int index = 0;
                for (Map<String, String> row : rows) {
                    index++;
                    String productIdText = v(row, "Primary Product ID");
                    String versionId = v(row, "New Version ID");
                    String excelRowNo = v(row, "_excelRowNo");

                    log.debug("开始保存第{}条产品。excelRowNo={}, primaryProductId={}, versionId={}", index, excelRowNo, productIdText, versionId);

                    long manufacturerId = upsertManufacturer(conn, certifierId, row);
                    Long pocId = upsertPocIfPresent(conn, certifierId, row);
                    long productCertificateId = upsertProductCertificate(conn, certifierId, collectionId, row, manufacturerId, pocId);

                    deleteProductChildren(conn, productCertificateId);
                    insertIdentifiers(conn, productCertificateId, row);
                    insertLabIfPresent(conn, certifierId, productCertificateId, row, 1);
                    insertLabIfPresent(conn, certifierId, productCertificateId, row, 2);
                    insertBatchItem(conn, batchId, productCertificateId, productIdText, versionId);

                    log.info("产品明细保存完成。batchId={}, productCertificateId={}, primaryProductId={}, versionId={}",
                            batchId, productCertificateId, productIdText, versionId);
                }

                conn.commit();
                log.info("CSV和产品明细全部保存完成。batchId={}, rowCount={}", batchId, rows.size());
                return batchId;
            } catch (Exception e) {
                conn.rollback();
                log.error("保存CSV和产品明细失败，已回滚。message={}", e.getMessage(), e);
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }
                if (e instanceof IOException) {
                    throw (IOException) e;
                }
                throw new SQLException("保存CSV和产品明细失败：" + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private long insertImportBatch(Connection conn, String certifierId, String collectionId,
                                   String originalFileName, String generatedFileName,
                                   String csvText, int rowCount) throws SQLException {
        String sql = "INSERT INTO CPSC_eFiling_import_batch "
                + "(certifier_id, collection_id, do_certify, import_status, request_json, response_json, status_message) "
                + "VALUES (?, ?, 0, ?, ?, ?, ?)";

        String responseJson = "{\"generatedFileName\":\"" + json(generatedFileName) + "\","
                + "\"originalFileName\":\"" + json(originalFileName) + "\","
                + "\"rowCount\":" + rowCount + "}";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, certifierId);
            ps.setString(2, collectionId);
            ps.setString(3, "CSV_CREATED_DB_SAVED");
            ps.setString(4, csvText);
            ps.setString(5, responseJson);
            ps.setString(6, "客户Excel已生成Import CSV，并已同步保存产品明细到本地DB：" + generatedFileName);
            ps.executeUpdate();
            return generatedId(ps);
        }
    }

    private long upsertManufacturer(Connection conn, String certifierId, Map<String, String> row) throws SQLException {
        String altId = required(v(row, "Manufacturer Alternate ID"), "Manufacturer Alternate ID 不能为空。 ");
        String name = defaultIfBlank(v(row, "Manufacturer Name"), altId);

        String sql = "INSERT INTO CPSC_eFiling_manufacturer "
                + "(certifier_id, gln, alternate_id, sbm_id, name, address_line1, address_line2, apt_number, city, state_province, country, postal_code, phone, email, is_new) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "id = LAST_INSERT_ID(id), gln = VALUES(gln), sbm_id = VALUES(sbm_id), name = VALUES(name), "
                + "address_line1 = VALUES(address_line1), address_line2 = VALUES(address_line2), apt_number = VALUES(apt_number), "
                + "city = VALUES(city), state_province = VALUES(state_province), country = VALUES(country), postal_code = VALUES(postal_code), "
                + "phone = VALUES(phone), email = VALUES(email), is_new = VALUES(is_new), active_flag = 1";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, certifierId);
            ps.setString(2, v(row, "Manufacturer GLN"));
            ps.setString(3, altId);
            ps.setString(4, v(row, "Small Batch Manufacturer CPSC ID"));
            ps.setString(5, name);
            ps.setString(6, defaultIfBlank(v(row, "Manufacturer Address Line 1"), "N/A"));
            ps.setString(7, v(row, "Manufacturer Address Line 2"));
            ps.setString(8, v(row, "Manufacturer Apt/Suite Number"));
            ps.setString(9, defaultIfBlank(v(row, "Manufacturer City"), "N/A"));
            ps.setString(10, v(row, "Manufacturer State/Province"));
            ps.setString(11, defaultIfBlank(v(row, "Manufacturer Country"), "N/A"));
            ps.setString(12, defaultIfBlank(v(row, "Manufacturer Zip/Postal Code"), ""));
            ps.setString(13, defaultIfBlank(v(row, "Manufacturer Phone"), ""));
            ps.setString(14, defaultIfBlank(v(row, "Manufacturer Email"), ""));
            ps.setString(15, normalizeYN(v(row, "Manufacturer Is New?"), "Y"));
            ps.executeUpdate();
            long id = generatedId(ps);
            log.debug("制造商保存完成。id={}, altId={}, name={}", id, altId, name);
            return id;
        }
    }

    private Long upsertPocIfPresent(Connection conn, String certifierId, Map<String, String> row) throws SQLException {
        String pocType = v(row, "Point of Contact (POC) for Test Result Records");
        String pocAltId = v(row, "POC Alternate ID");
        String pocGln = v(row, "POC GLN");
        String pocName = v(row, "POC Name");
        String pocEmail = v(row, "POC Email");

        if (StringUtil.isBlank(pocAltId) && StringUtil.isBlank(pocGln) && StringUtil.isBlank(pocName) && StringUtil.isBlank(pocEmail)) {
            log.debug("POC未填写详细信息，跳过POC保存。pocType={}", pocType);
            return null;
        }

        String pocCode = defaultIfBlank(pocAltId, defaultIfBlank(pocGln, defaultIfBlank(pocEmail, pocName)));
        String sql = "INSERT INTO CPSC_eFiling_poc "
                + "(certifier_id, poc_code, type, gln, alternate_id, name, address_line1, address_line2, apt_number, city, state_province, country, postal_code, phone, email, is_new) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "id = LAST_INSERT_ID(id), type = VALUES(type), gln = VALUES(gln), alternate_id = VALUES(alternate_id), name = VALUES(name), "
                + "address_line1 = VALUES(address_line1), address_line2 = VALUES(address_line2), apt_number = VALUES(apt_number), "
                + "city = VALUES(city), state_province = VALUES(state_province), country = VALUES(country), postal_code = VALUES(postal_code), "
                + "phone = VALUES(phone), email = VALUES(email), is_new = VALUES(is_new), active_flag = 1";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, certifierId);
            ps.setString(2, pocCode);
            ps.setString(3, defaultIfBlank(pocType, "Importer"));
            ps.setString(4, pocGln);
            ps.setString(5, pocAltId);
            ps.setString(6, pocName);
            ps.setString(7, v(row, "POC Address Line 1"));
            ps.setString(8, v(row, "POC Address Line 2"));
            ps.setString(9, v(row, "POC Apt/Suite Number"));
            ps.setString(10, v(row, "POC City"));
            ps.setString(11, v(row, "POC State/Province"));
            ps.setString(12, v(row, "POC Country"));
            ps.setString(13, v(row, "POC Zip/Postal Code"));
            ps.setString(14, v(row, "POC Phone"));
            ps.setString(15, pocEmail);
            ps.setString(16, normalizeYN(v(row, "POC Is New?"), "N"));
            ps.executeUpdate();
            long id = generatedId(ps);
            log.debug("POC保存完成。id={}, pocCode={}, type={}", id, pocCode, pocType);
            return id;
        }
    }

    private long upsertProductCertificate(Connection conn, String certifierId, String collectionId, Map<String, String> row,
                                          long manufacturerId, Long pocId) throws SQLException {
        String productId = required(v(row, "Primary Product ID"), "Primary Product ID 不能为空。 ");
        String versionId = required(v(row, "New Version ID"), "New Version ID 不能为空。 ");
        String productName = defaultIfBlank(v(row, "Product Name (Model)"), productId);

        String sql = "INSERT INTO CPSC_eFiling_product_certificate "
                + "(certifier_id, collection_id, version_id, primary_product_id, primary_product_id_type, certificate_type, name, trade_brand_name, description, color, style, "
                + "manufacture_date, production_start_date, production_end_date, last_test_date, lot_number, lot_number_assigned_by, manufacturer_id, poc_id, product_update, version_id_to_update, data_status, remark) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "id = LAST_INSERT_ID(id), primary_product_id_type = VALUES(primary_product_id_type), certificate_type = VALUES(certificate_type), name = VALUES(name), "
                + "trade_brand_name = VALUES(trade_brand_name), description = VALUES(description), color = VALUES(color), style = VALUES(style), "
                + "manufacture_date = VALUES(manufacture_date), production_start_date = VALUES(production_start_date), production_end_date = VALUES(production_end_date), "
                + "last_test_date = VALUES(last_test_date), lot_number = VALUES(lot_number), lot_number_assigned_by = VALUES(lot_number_assigned_by), "
                + "manufacturer_id = VALUES(manufacturer_id), poc_id = VALUES(poc_id), product_update = VALUES(product_update), version_id_to_update = VALUES(version_id_to_update), "
                + "data_status = VALUES(data_status), remark = VALUES(remark)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, certifierId);
            ps.setString(2, collectionId);
            ps.setString(3, versionId);
            ps.setString(4, productId);
            ps.setString(5, defaultIfBlank(v(row, "Primary Product ID Type"), "Model Number"));
            ps.setString(6, defaultIfBlank(v(row, "Certificate Type"), "GCC"));
            ps.setString(7, productName);
            ps.setString(8, v(row, "Trade/Brand Name"));
            ps.setString(9, v(row, "Product/Model Description"));
            ps.setString(10, v(row, "Product/Model Color"));
            ps.setString(11, v(row, "Product/Model Style"));
            ps.setString(12, v(row, "Manufacture Date"));
            ps.setString(13, v(row, "Production Start Date"));
            ps.setString(14, v(row, "Production End Date"));
            ps.setString(15, v(row, "Last Test Date"));
            ps.setString(16, v(row, "Lot Number"));
            ps.setString(17, v(row, "Lot Number Assigned By"));
            ps.setLong(18, manufacturerId);
            if (pocId == null) {
                ps.setNull(19, Types.BIGINT);
            } else {
                ps.setLong(19, pocId);
            }
            ps.setString(20, normalizeYN(v(row, "Product Update"), "N"));
            ps.setString(21, v(row, "Current Version ID"));
            ps.setString(22, "CSV_CREATED");
            ps.setString(23, "importCsv.jsp读取客户Excel生成CSV时同步保存；Excel行号=" + v(row, "_excelRowNo"));
            ps.executeUpdate();
            long id = generatedId(ps);
            log.debug("产品证书保存完成。id={}, productId={}, versionId={}, name={}", id, productId, versionId, productName);
            return id;
        }
    }

    private void deleteProductChildren(Connection conn, long productCertificateId) throws SQLException {
        log.debug("删除产品旧子表数据。productCertificateId={}", productCertificateId);
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM CPSC_eFiling_product_identifier WHERE product_certificate_id = ?")) {
            ps.setLong(1, productCertificateId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM CPSC_eFiling_product_lab WHERE product_certificate_id = ?")) {
            ps.setLong(1, productCertificateId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM CPSC_eFiling_product_exemption WHERE product_certificate_id = ?")) {
            ps.setLong(1, productCertificateId);
            ps.executeUpdate();
        }
    }

    private void insertIdentifiers(Connection conn, long productCertificateId, Map<String, String> row) throws SQLException {
        insertIdentifier(conn, productCertificateId, "GTIN", v(row, "GTIN"));
        insertIdentifier(conn, productCertificateId, "UPC", v(row, "UPC"));
        insertIdentifier(conn, productCertificateId, "SKU", v(row, "SKU"));
        insertIdentifier(conn, productCertificateId, "Model Number", v(row, "Model Number"));
        insertIdentifier(conn, productCertificateId, "Serial Number", v(row, "Serial Number"));
        insertIdentifier(conn, productCertificateId, "Registered Number", v(row, "Registered Number"));
        insertIdentifier(conn, productCertificateId, "Alternate Identifier", v(row, "Alternate Identifier"));
    }

    private void insertIdentifier(Connection conn, long productCertificateId, String type, String value) throws SQLException {
        if (StringUtil.isBlank(value)) {
            return;
        }
        String sql = "INSERT IGNORE INTO CPSC_eFiling_product_identifier (product_certificate_id, ident_type, identifier) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productCertificateId);
            ps.setString(2, type);
            ps.setString(3, value);
            ps.executeUpdate();
        }
    }

    private void insertLabIfPresent(Connection conn, String certifierId, long productCertificateId, Map<String, String> row, int no) throws SQLException {
        String prefix = "Lab " + no + " ";
        String altId = v(row, prefix + "Alternate ID");
        String cpscId = v(row, prefix + "CPSC-ID");
        String gln = v(row, prefix + "GLN");
        String name = v(row, prefix + "Name");
        String citations = v(row, prefix + "Citation Codes");
        String reportId = v(row, prefix + "Test Report ID");

        if (StringUtil.isBlank(altId) && StringUtil.isBlank(cpscId) && StringUtil.isBlank(gln)
                && StringUtil.isBlank(name) && StringUtil.isBlank(citations) && StringUtil.isBlank(reportId)) {
            log.debug("Lab{}未填写，跳过。productCertificateId={}", no, productCertificateId);
            return;
        }

        String labType = defaultIfBlank(v(row, prefix + "Type"), "LAB");
        String labAltId = defaultIfBlank(altId, defaultIfBlank(cpscId, defaultIfBlank(gln, "LAB_" + productCertificateId + "_" + no)));
        String labName = defaultIfBlank(name, labAltId);

        long labId = upsertLab(conn, certifierId, row, no, labType, labAltId, labName);
        long productLabId = insertProductLab(conn, productCertificateId, labId, row, no);
        insertCitationCodes(conn, productLabId, citations);

        log.debug("Lab{}保存完成。labId={}, productLabId={}, altId={}, citationCodes={}", no, labId, productLabId, labAltId, citations);
    }

    private long upsertLab(Connection conn, String certifierId, Map<String, String> row, int no,
                           String labType, String labAltId, String labName) throws SQLException {
        String prefix = "Lab " + no + " ";
        String sql = "INSERT INTO CPSC_eFiling_lab "
                + "(certifier_id, type, cpsc_id, gln, alternate_id, name, address_line1, address_line2, apt_number, city, state_province, country, postal_code, phone, email, is_new) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "id = LAST_INSERT_ID(id), type = VALUES(type), cpsc_id = VALUES(cpsc_id), gln = VALUES(gln), name = VALUES(name), "
                + "address_line1 = VALUES(address_line1), address_line2 = VALUES(address_line2), apt_number = VALUES(apt_number), "
                + "city = VALUES(city), state_province = VALUES(state_province), country = VALUES(country), postal_code = VALUES(postal_code), "
                + "phone = VALUES(phone), email = VALUES(email), is_new = VALUES(is_new), active_flag = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, certifierId);
            ps.setString(2, labType);
            ps.setString(3, v(row, prefix + "CPSC-ID"));
            ps.setString(4, v(row, prefix + "GLN"));
            ps.setString(5, labAltId);
            ps.setString(6, labName);
            ps.setString(7, defaultIfBlank(v(row, prefix + "Address Line 1"), "N/A"));
            ps.setString(8, v(row, prefix + "Address Line 2"));
            ps.setString(9, v(row, prefix + "Apt/Suite Number"));
            ps.setString(10, defaultIfBlank(v(row, prefix + "City"), "N/A"));
            ps.setString(11, v(row, prefix + "State/Province"));
            ps.setString(12, defaultIfBlank(v(row, prefix + "Country"), "N/A"));
            ps.setString(13, defaultIfBlank(v(row, prefix + "Zip/Postal Code"), ""));
            ps.setString(14, defaultIfBlank(v(row, prefix + "Phone"), ""));
            ps.setString(15, defaultIfBlank(v(row, prefix + "Email"), ""));
            ps.setString(16, normalizeYN(v(row, prefix + "Is New?"), "N"));
            ps.executeUpdate();
            return generatedId(ps);
        }
    }

    private long insertProductLab(Connection conn, long productCertificateId, long labId, Map<String, String> row, int no) throws SQLException {
        String prefix = "Lab " + no + " ";
        String sql = "INSERT INTO CPSC_eFiling_product_lab "
                + "(product_certificate_id, lab_id, test_report_id, test_url, test_report_access_key, is_component, component_description) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, productCertificateId);
            ps.setLong(2, labId);
            ps.setString(3, v(row, prefix + "Test Report ID"));
            ps.setString(4, v(row, prefix + "Test URL"));
            ps.setString(5, v(row, prefix + "Test Report Access Key"));
            ps.setInt(6, yesNoToInt(v(row, prefix + "Is Component?")));
            ps.setString(7, v(row, prefix + "Component Description"));
            ps.executeUpdate();
            return generatedId(ps);
        }
    }

    private void insertCitationCodes(Connection conn, long productLabId, String citationCodes) throws SQLException {
        if (StringUtil.isBlank(citationCodes)) {
            return;
        }
        String sql = "INSERT IGNORE INTO CPSC_eFiling_product_lab_citation (product_lab_id, citation_code) VALUES (?, ?)";
        for (String code : StringUtil.splitList(citationCodes)) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, productLabId);
                ps.setString(2, code);
                ps.executeUpdate();
            }
        }
    }

    private void insertBatchItem(Connection conn, long batchId, long productCertificateId, String primaryProductId, String versionId) throws SQLException {
        String sql = "INSERT IGNORE INTO CPSC_eFiling_import_batch_item "
                + "(import_batch_id, product_certificate_id, primary_product_id, version_id, imported_ind, error_detected_ind, error_count) "
                + "VALUES (?, ?, ?, ?, 'Y', 'N', 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, batchId);
            ps.setLong(2, productCertificateId);
            ps.setString(3, primaryProductId);
            ps.setString(4, versionId);
            ps.executeUpdate();
        }
    }

    private long generatedId(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        throw new SQLException("未取得数据库自增ID。 ");
    }

    private String readFile(File file) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        try (InputStream in = new FileInputStream(file)) {
            int len;
            while ((len = in.read(buf)) >= 0) {
                out.write(buf, 0, len);
            }
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    private int yesNoToInt(String value) {
        if (StringUtil.isBlank(value)) {
            return 0;
        }
        String v = value.trim().toLowerCase(Locale.ROOT);
        return ("y".equals(v) || "yes".equals(v) || "true".equals(v) || "1".equals(v) || "是".equals(value.trim())) ? 1 : 0;
    }

    private String normalizeYN(String value, String defaultValue) {
        if (StringUtil.isBlank(value)) {
            return defaultValue;
        }
        String v = value.trim();
        String upper = v.toUpperCase(Locale.ROOT);
        if ("Y".equals(upper) || "YES".equals(upper) || "TRUE".equals(upper) || "是".equals(v) || v.contains("更新") || v.contains("新建")) {
            return "Y";
        }
        if ("N".equals(upper) || "NO".equals(upper) || "FALSE".equals(upper) || "否".equals(v) || v.contains("新增") || v.contains("已存在")) {
            return "N";
        }
        return v;
    }

    private String v(Map<String, String> row, String key) {
        if (row == null || key == null) {
            return "";
        }
        String value = row.get(key);
        return value == null ? "" : value.trim();
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StringUtil.isBlank(value) ? defaultValue : value.trim();
    }

    private String required(String value, String message) {
        if (StringUtil.isBlank(value)) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private String json(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }
}
