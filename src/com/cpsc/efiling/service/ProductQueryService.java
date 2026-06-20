package com.cpsc.efiling.service;

import com.cpsc.efiling.model.*;
import com.cpsc.efiling.util.DbUtil;
import com.cpsc.efiling.util.StringUtil;

import java.sql.*;
import java.util.*;

public class ProductQueryService {
    private final ApiValidationService validationService = new ApiValidationService();

    public List<ProductView> listProducts(Long batchId) throws SQLException {
        Map<Long, ProductView> map = new LinkedHashMap<Long, ProductView>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.*, ");
        sql.append("m.alternate_id AS m_alternate_id, m.name AS m_name, m.country AS m_country, m.email AS m_email, m.is_new AS m_is_new, ");
        sql.append("po.poc_code AS poc_code, po.type AS poc_type, po.name AS poc_name, po.email AS poc_email, po.is_new AS poc_is_new ");
        sql.append("FROM CPSC_eFiling_product_certificate p ");
        sql.append("LEFT JOIN CPSC_eFiling_manufacturer m ON p.manufacturer_id = m.id ");
        sql.append("LEFT JOIN CPSC_eFiling_poc po ON p.poc_id = po.id ");
        if (batchId != null) {
            sql.append("INNER JOIN CPSC_eFiling_import_batch_item bi ON bi.product_certificate_id = p.id AND bi.import_batch_id = ? ");
        }
        sql.append("ORDER BY p.id DESC LIMIT 500");

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (batchId != null) {
                ps.setLong(1, batchId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductView p = new ProductView();
                    p.setId(rs.getLong("id"));
                    p.setCertifierId(rs.getString("certifier_id"));
                    p.setCollectionId(rs.getString("collection_id"));
                    p.setVersionId(rs.getString("version_id"));
                    p.setPrimaryProductId(rs.getString("primary_product_id"));
                    p.setPrimaryProductIdType(rs.getString("primary_product_id_type"));
                    p.setCertificateType(rs.getString("certificate_type"));
                    p.setName(rs.getString("name"));
                    p.setTradeBrandName(rs.getString("trade_brand_name"));
                    p.setDescription(rs.getString("description"));
                    p.setColor(rs.getString("color"));
                    p.setStyle(rs.getString("style"));
                    p.setManufactureDate(rs.getString("manufacture_date"));
                    p.setProductionStartDate(rs.getString("production_start_date"));
                    p.setProductionEndDate(rs.getString("production_end_date"));
                    p.setLotNumber(rs.getString("lot_number"));
                    p.setLotNumberAssignedBy(rs.getString("lot_number_assigned_by"));
                    p.setLastTestDate(rs.getString("last_test_date"));
                    p.setProductUpdate(rs.getString("product_update"));
                    p.setVersionIdToUpdate(rs.getString("version_id_to_update"));
                    p.setDataStatus(rs.getString("data_status"));
                    p.setRemark(rs.getString("remark"));

                    p.setManufacturerAlternateId(rs.getString("m_alternate_id"));
                    p.setManufacturerName(rs.getString("m_name"));
                    p.setManufacturerCountry(rs.getString("m_country"));
                    p.setManufacturerEmail(rs.getString("m_email"));
                    p.setManufacturerIsNew(rs.getString("m_is_new"));

                    p.setPocCode(rs.getString("poc_code"));
                    p.setPocType(rs.getString("poc_type"));
                    p.setPocName(rs.getString("poc_name"));
                    p.setPocEmail(rs.getString("poc_email"));
                    p.setPocIsNew(rs.getString("poc_is_new"));

                    map.put(p.getId(), p);
                }
            }
        }

        if (!map.isEmpty()) {
            loadLabs(map);
            loadExemptions(map);
            for (ProductView p : map.values()) {
                p.setErrors(validationService.validate(p));
            }
        }

        return new ArrayList<ProductView>(map.values());
    }

    public List<BatchView> listBatches() throws SQLException {
        List<BatchView> list = new ArrayList<BatchView>();
        String sql = "SELECT b.id, b.certifier_id, b.collection_id, b.import_id, b.import_status, b.status_message, " +
                "DATE_FORMAT(b.created_at, '%Y-%m-%d %H:%i:%s') AS created_at, COUNT(i.id) AS product_count " +
                "FROM CPSC_eFiling_import_batch b " +
                "LEFT JOIN CPSC_eFiling_import_batch_item i ON b.id = i.import_batch_id " +
                "GROUP BY b.id, b.certifier_id, b.collection_id, b.import_id, b.import_status, b.status_message, b.created_at " +
                "ORDER BY b.id DESC LIMIT 50";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BatchView b = new BatchView();
                b.setId(rs.getLong("id"));
                b.setCertifierId(rs.getString("certifier_id"));
                b.setCollectionId(rs.getString("collection_id"));
                b.setImportId(rs.getString("import_id"));
                b.setImportStatus(rs.getString("import_status"));
                b.setStatusMessage(rs.getString("status_message"));
                b.setCreatedAt(rs.getString("created_at"));
                b.setProductCount(rs.getInt("product_count"));
                list.add(b);
            }
        }
        return list;
    }

    private void loadLabs(Map<Long, ProductView> products) throws SQLException {
        String ids = joinIds(products.keySet());
        if (StringUtil.isBlank(ids)) return;
        String sql = "SELECT pl.id AS product_lab_id, pl.product_certificate_id, l.alternate_id, l.name, l.type, l.cpsc_id, " +
                "pl.test_report_id, pl.test_url, pl.is_component, pl.component_description, " +
                "GROUP_CONCAT(c.citation_code ORDER BY c.citation_code SEPARATOR ', ') AS citation_codes " +
                "FROM CPSC_eFiling_product_lab pl " +
                "JOIN CPSC_eFiling_lab l ON pl.lab_id = l.id " +
                "LEFT JOIN CPSC_eFiling_product_lab_citation c ON c.product_lab_id = pl.id " +
                "WHERE pl.product_certificate_id IN (" + ids + ") " +
                "GROUP BY pl.id, pl.product_certificate_id, l.alternate_id, l.name, l.type, l.cpsc_id, pl.test_report_id, pl.test_url, pl.is_component, pl.component_description " +
                "ORDER BY pl.id";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long productId = rs.getLong("product_certificate_id");
                ProductView p = products.get(productId);
                if (p == null) continue;
                LabView lab = new LabView();
                lab.setProductLabId(rs.getLong("product_lab_id"));
                lab.setLabAlternateId(rs.getString("alternate_id"));
                lab.setLabName(rs.getString("name"));
                lab.setLabType(rs.getString("type"));
                lab.setCpscId(rs.getString("cpsc_id"));
                lab.setTestReportId(rs.getString("test_report_id"));
                lab.setTestUrl(rs.getString("test_url"));
                lab.setComponent(rs.getInt("is_component") == 1);
                lab.setComponentDescription(rs.getString("component_description"));
                lab.setCitationCodes(rs.getString("citation_codes"));
                p.getLabs().add(lab);
            }
        }
    }

    private void loadExemptions(Map<Long, ProductView> products) throws SQLException {
        String ids = joinIds(products.keySet());
        if (StringUtil.isBlank(ids)) return;
        String sql = "SELECT product_certificate_id, exemption_code FROM CPSC_eFiling_product_exemption WHERE product_certificate_id IN (" + ids + ") ORDER BY id";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long productId = rs.getLong("product_certificate_id");
                ProductView p = products.get(productId);
                if (p != null) {
                    p.getExemptions().add(rs.getString("exemption_code"));
                }
            }
        }
    }

    private String joinIds(Set<Long> ids) {
        StringBuilder sb = new StringBuilder();
        for (Long id : ids) {
            if (sb.length() > 0) sb.append(',');
            sb.append(id.longValue());
        }
        return sb.toString();
    }
}
