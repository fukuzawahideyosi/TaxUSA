package com.cpsc.efiling.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.cpsc.efiling.model.ImportResult;
import com.cpsc.efiling.service.ExcelImportService;
import com.cpsc.efiling.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

public class UploadServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(UploadServlet.class);
    private final ExcelImportService importService = new ExcelImportService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        File tempFile = null;
        try {
            log.info("UploadServlet收到上传请求。remoteAddr={}", request.getRemoteAddr());
            String certifierId = request.getParameter("certifierId");
            String collectionId = request.getParameter("collectionId");
            boolean doCertify = "true".equalsIgnoreCase(request.getParameter("doCertify"));

            if (StringUtil.isBlank(certifierId)) {
                throw new IllegalArgumentException("Certifier ID 不能为空。 ");
            }
            if (StringUtil.isBlank(collectionId)) {
                throw new IllegalArgumentException("Collection ID 不能为空。 ");
            }

            Part part = request.getPart("excelFile");
            if (part == null || part.getSize() == 0) {
                throw new IllegalArgumentException("请选择要上传的 Excel 文件。 ");
            }

            String submitted = part.getSubmittedFileName();
            if (submitted == null || !(submitted.endsWith(".xlsx") || submitted.endsWith(".xls"))) {
                throw new IllegalArgumentException("只支持上传 .xlsx 或 .xls 文件。 ");
            }

            tempFile = File.createTempFile("cpsc_efiling_upload_", submitted.endsWith(".xls") ? ".xls" : ".xlsx");
            part.write(tempFile.getAbsolutePath());

            log.info("UploadServlet上传文件。fileName={}, size={}, certifierId={}, collectionId={}", submitted, part.getSize(), certifierId, collectionId);
            ImportResult result = importService.importExcel(tempFile, certifierId.trim(), collectionId.trim(), doCertify);

            String msg = URLEncoder.encode("上传成功：已读取 " + result.getProductCount() + " 个产品，并生成 /import JSON 保存到数据库。", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/products?batchId=" + result.getBatchId() + "&success=" + msg);
        } catch (Exception e) {
            log.error("UploadServlet处理失败。message={}", e.getMessage(), e);
            String msg = URLEncoder.encode(e.getMessage() == null ? "上传失败" : e.getMessage(), "UTF-8");
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=" + msg);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
