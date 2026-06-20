package com.cpsc.efiling.service;

import com.cpsc.efiling.model.*;
import com.cpsc.efiling.util.DbUtil;
import com.cpsc.efiling.util.StringUtil;

import java.io.File;
import java.sql.*;

public class ExcelImportService {
    private final ExcelReadService excelReadService = new ExcelReadService();
    private final CpscImportJsonBuilder jsonBuilder = new CpscImportJsonBuilder();

    public ImportResult importExcel(File excelFile, String certifierId, String collectionId, boolean doCertify) throws Exception {
        ImportData importData = excelReadService.read(excelFile);
        String requestJson = jsonBuilder.buildJson(importData);

        try (Connection connection = DbUtil.getConnection()) {
            connection.setAutoCommit(false);
            try {
                long batchId = insertImportBatch(connection, certifierId, collectionId, doCertify, requestJson);

                for (ProductData product : importData.getProducts()) {
                    long manufacturerId = upsertManufacturer(connection, certifierId, product.getManufacturer());
                    Long pocId = null;
                    if (product.getPoc() != null) {
                        pocId = upsertPoc(connection, certifierId, product.getPoc());
                    }

                    long productId = upsertProductCertificate(connection, certifierId, collectionId, product, manufacturerId, pocId);

                    deleteProductChildren(connection, productId);
                    insertIdentifiers(connection, productId, product);
                    insertProductLabs(connection, certifierId, productId, product);
                    insertExemptions(connection, productId, product);
                    insertBatchItem(connection, batchId, productId, product);
                }

                connection.commit();
                return new ImportResult(batchId, importData.getProducts().size(), requestJson);
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private long insertImportBatch(Connection conn, String certifierId, String collectionId, boolean doCertify, String requestJson) throws SQLException {
        String sql = "INSERT INTO CPSC_eFiling_import_batch " +
                "(certifier_id, collection_id, do_certify, import_status, request_json) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, certifierId);
            ps.setString(2, collectionId);
            ps.setInt(3, doCertify ? 1 : 0);
            ps.setString(4, "EXCEL_IMPORTED_JSON_CREATED");
            ps.setString(5, requestJson);
            ps.executeUpdate();
            return getGeneratedId(ps);
        }
    }

    private long upsertManufacturer(Connection conn, String certifierId, ManufacturerData m) throws SQLException {
        String sql = "INSERT INTO CPSC_eFiling_manufacturer " +
                "(certifier_id, gln, alternate_id, sbm_id, name, address_line1, address_line2, apt_number, city, state_province, country, postal_code, phone, email, is_new) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "id = LAST_INSERT_ID(id), gln = VALUES(gln), sbm_id = VALUES(sbm_id), name = VALUES(name), " +
                "address_line1 = VALUES(address_line1), address_line2 = VALUES(address_line2), apt_number = VALUES(apt_number), " +
                "city = VALUES(city), state_province = VALUES(state_province), country = VALUES(country), postal_code = VALUES(postal_code), " +
                "phone = VALUES(phone), email = VALUES(email), is_new = VALUES(is_new), active_flag = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, certifierId);
            ps.setString(2, empty(m.getGln()));
            ps.setString(3, required(m.getAlternateId(), "制造商编号不能为空。"));
            ps.setString(4, empty(m.getSbmId()));
            ps.setString(5, empty(m.getName()));
            ps.setString(6, empty(m.getAddressLine1()));
            ps.setString(7, empty(m.getAddressLine2()));
            ps.setString(8, empty(m.getAptNumber()));
            ps.setString(9, empty(m.getCity()));
            ps.setString(10, empty(m.getStateProvince()));
            ps.setString(11, empty(m.getCountry()));
            ps.setString(12, empty(m.getPostalCode()));
            ps.setString(13, empty(m.getPhone()));
            ps.setString(14, empty(m.getEmail()));
            ps.setString(15, StringUtil.normalizeYN(m.getIsNew(), "N"));
            ps.executeUpdate();
            return getGeneratedId(ps);
        }
    }

    private long upsertPoc(Connection conn, String certifierId, PocData p) throws SQLException {
        String sql = "INSERT INTO CPSC_eFiling_poc " +
                "(certifier_id, poc_code, type, gln, alternate_id, name, address_line1, address_line2, apt_number, city, state_province, country, postal_code, phone, email, is_new) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "id = LAST_INSERT_ID(id), type = VALUES(type), gln = VALUES(gln), alternate_id = VALUES(alternate_id), name = VALUES(name), " +
                "address_line1 = VALUES(address_line1), address_line2 = VALUES(address_line2), apt_number = VALUES(apt_number), " +
                "city = VALUES(city), state_province = VALUES(state_province), country = VALUES(country), postal_code = VALUES(postal_code), " +
                "phone = VALUES(phone), email = VALUES(email), is_new = VALUES(is_new), active_flag = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, certifierId);
            ps.setString(2, required(p.getPocCode(), "POC编号不能为空。"));
            ps.setString(3, StringUtil.defaultIfBlank(p.getType(), "Importer"));
            ps.setString(4, empty(p.getGln()));
            ps.setString(5, empty(p.getAlternateId()));
            ps.setString(6, empty(p.getName()));
            ps.setString(7, empty(p.getAddressLine1()));
            ps.setString(8, empty(p.getAddressLine2()));
            ps.setString(9, empty(p.getAptNumber()));
            ps.setString(10, empty(p.getCity()));
            ps.setString(11, empty(p.getStateProvince()));
            ps.setString(12, empty(p.getCountry()));
            ps.setString(13, empty(p.getPostalCode()));
            ps.setString(14, empty(p.getPhone()));
            ps.setString(15, empty(p.getEmail()));
            ps.setString(16, StringUtil.normalizeYN(p.getIsNew(), "N"));
            ps.executeUpdate();
            return getGeneratedId(ps);
        }
    }

    private long upsertProductCertificate(Connection conn, String certifierId, String collectionId, ProductData p, long manufacturerId, Long pocId) throws SQLException {
        String sql = "INSERT INTO CPSC_eFiling_product_certificate " +
                "(certifier_id, collection_id, version_id, primary_product_id, primary_product_id_type, certificate_type, name, trade_brand_name, description, color, style, " +
                "manufacture_date, production_start_date, production_end_date, last_test_date, lot_number, lot_number_assigned_by, manufacturer_id, poc_id, product_update, version_id_to_update, data_status, remark) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "id = LAST_INSERT_ID(id), primary_product_id_type = VALUES(primary_product_id_type), certificate_type = VALUES(certificate_type), name = VALUES(name), " +
                "trade_brand_name = VALUES(trade_brand_name), description = VALUES(description), color = VALUES(color), style = VALUES(style), " +
                "manufacture_date = VALUES(manufacture_date), production_start_date = VALUES(production_start_date), production_end_date = VALUES(production_end_date), " +
                "last_test_date = VALUES(last_test_date), lot_number = VALUES(lot_number), lot_number_assigned_by = VALUES(lot_number_assigned_by), " +
                "manufacturer_id = VALUES(manufacturer_id), poc_id = VALUES(poc_id), product_update = VALUES(product_update), version_id_to_update = VALUES(version_id_to_update), " +
                "data_status = VALUES(data_status), remark = VALUES(remark)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, certifierId);
            ps.setString(2, collectionId);
            ps.setString(3, StringUtil.defaultIfBlank(p.getVersionId(), "V1"));
            ps.setString(4, required(p.getPrimaryProductId(), "产品唯一ID不能为空。"));
            ps.setString(5, StringUtil.defaultIfBlank(p.getPrimaryProductIdType(), "Model #"));
            ps.setString(6, StringUtil.defaultIfBlank(p.getCertificateType(), "GCC"));
            ps.setString(7, empty(p.getName()));
            ps.setString(8, empty(p.getTradeBrandName()));
            ps.setString(9, empty(p.getDescription()));
            ps.setString(10, empty(p.getColor()));
            ps.setString(11, empty(p.getStyle()));
            ps.setString(12, empty(p.getManufactureDate()));
            ps.setString(13, empty(p.getProductionStartDate()));
            ps.setString(14, empty(p.getProductionEndDate()));
            ps.setString(15, empty(p.getLastTestDate()));
            ps.setString(16, empty(p.getLotNumber()));
            ps.setString(17, empty(p.getLotNumberAssignedBy()));
            ps.setLong(18, manufacturerId);
            if (pocId == null) {
                ps.setNull(19, Types.BIGINT);
            } else {
                ps.setLong(19, pocId);
            }
            ps.setString(20, StringUtil.normalizeYN(p.getProductUpdate(), "N"));
            ps.setString(21, empty(p.getVersionIdToUpdate()));
            ps.setString(22, "READY");
            ps.setString(23, empty(p.getNotes()));
            ps.executeUpdate();
            return getGeneratedId(ps);
        }
    }

    private void deleteProductChildren(Connection conn, long productId) throws SQLException {
        execute(conn, "DELETE FROM CPSC_eFiling_product_identifier WHERE product_certificate_id = ?", productId);
        execute(conn, "DELETE FROM CPSC_eFiling_product_lab WHERE product_certificate_id = ?", productId);
        execute(conn, "DELETE FROM CPSC_eFiling_product_exemption WHERE product_certificate_id = ?", productId);
    }

    private void insertIdentifiers(Connection conn, long productId, ProductData product) throws SQLException {
        String sql = "INSERT INTO CPSC_eFiling_product_identifier (product_certificate_id, ident_type, identifier) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (IdentifierData identifier : product.getIdentifiers()) {
                ps.setLong(1, productId);
                ps.setString(2, identifier.getIdentType());
                ps.setString(3, identifier.getIdentifier());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertProductLabs(Connection conn, String certifierId, long productId, ProductData product) throws SQLException {
        for (LabData labData : product.getLabs()) {
            long labId = upsertLab(conn, certifierId, labData);
            long productLabId = insertProductLab(conn, productId, labId, labData);
            insertCitations(conn, productLabId, labData);
        }
    }

    private long upsertLab(Connection conn, String certifierId, LabData l) throws SQLException {
        String sql = "INSERT INTO CPSC_eFiling_lab " +
                "(certifier_id, type, cpsc_id, gln, alternate_id, name, address_line1, address_line2, apt_number, city, state_province, country, postal_code, phone, email, is_new) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "id = LAST_INSERT_ID(id), type = VALUES(type), cpsc_id = VALUES(cpsc_id), gln = VALUES(gln), name = VALUES(name), " +
                "address_line1 = VALUES(address_line1), address_line2 = VALUES(address_line2), apt_number = VALUES(apt_number), city = VALUES(city), " +
                "state_province = VALUES(state_province), country = VALUES(country), postal_code = VALUES(postal_code), phone = VALUES(phone), email = VALUES(email), is_new = VALUES(is_new), active_flag = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, certifierId);
            ps.setString(2, StringUtil.defaultIfBlank(l.getType(), "LAB"));
            ps.setString(3, empty(l.getCpscId()));
            ps.setString(4, empty(l.getGln()));
            ps.setString(5, required(l.getAlternateId(), "实验室编号不能为空。"));
            ps.setString(6, empty(l.getName()));
            ps.setString(7, empty(l.getAddressLine1()));
            ps.setString(8, empty(l.getAddressLine2()));
            ps.setString(9, empty(l.getAptNumber()));
            ps.setString(10, empty(l.getCity()));
            ps.setString(11, empty(l.getStateProvince()));
            ps.setString(12, empty(l.getCountry()));
            ps.setString(13, empty(l.getPostalCode()));
            ps.setString(14, empty(l.getPhone()));
            ps.setString(15, empty(l.getEmail()));
            ps.setString(16, StringUtil.normalizeYN(l.getIsNew(), "N"));
            ps.executeUpdate();
            return getGeneratedId(ps);
        }
    }

    private long insertProductLab(Connection conn, long productId, long labId, LabData l) throws SQLException {
        String sql = "INSERT INTO CPSC_eFiling_product_lab " +
                "(product_certificate_id, lab_id, test_report_id, test_url, test_report_access_key, is_component, component_description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, productId);
            ps.setLong(2, labId);
            ps.setString(3, empty(l.getTestReportId()));
            ps.setString(4, empty(l.getTestURL()));
            ps.setString(5, empty(l.getTestReportAccessKey()));
            ps.setInt(6, l.isComponent() ? 1 : 0);
            ps.setString(7, empty(l.getComponentDescription()));
            ps.executeUpdate();
            return getGeneratedId(ps);
        }
    }

    private void insertCitations(Connection conn, long productLabId, LabData labData) throws SQLException {
        String sql = "INSERT INTO CPSC_eFiling_product_lab_citation (product_lab_id, citation_code) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String code : labData.getCitationCodes()) {
                if (StringUtil.isBlank(code)) continue;
                ps.setLong(1, productLabId);
                ps.setString(2, code.trim());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertExemptions(Connection conn, long productId, ProductData product) throws SQLException {
        String sql = "INSERT INTO CPSC_eFiling_product_exemption (product_certificate_id, exemption_code) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String exemption : product.getExemptions()) {
                if (StringUtil.isBlank(exemption)) continue;
                ps.setLong(1, productId);
                ps.setString(2, exemption.trim());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertBatchItem(Connection conn, long batchId, long productId, ProductData product) throws SQLException {
        String sql = "INSERT INTO CPSC_eFiling_import_batch_item " +
                "(import_batch_id, product_certificate_id, primary_product_id, version_id, imported_ind, error_detected_ind, error_count) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE primary_product_id = VALUES(primary_product_id), version_id = VALUES(version_id)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, batchId);
            ps.setLong(2, productId);
            ps.setString(3, product.getPrimaryProductId());
            ps.setString(4, StringUtil.defaultIfBlank(product.getVersionId(), "V1"));
            ps.setString(5, null);
            ps.setString(6, null);
            ps.setInt(7, 0);
            ps.executeUpdate();
        }
    }

    private void execute(Connection conn, String sql, long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private long getGeneratedId(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        throw new SQLException("未获取到数据库自动生成ID。 ");
    }

    private String empty(String value) {
        return StringUtil.trimToEmpty(value);
    }

    private String required(String value, String message) {
        if (StringUtil.isBlank(value)) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
