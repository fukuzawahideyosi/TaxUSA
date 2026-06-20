package com.cpsc.efiling.servlet;

import com.cpsc.efiling.service.CpscImportCsvService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@WebServlet("/import-csv-generate")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 20 * 1024 * 1024,
        maxRequestSize = 30 * 1024 * 1024
)
public class ImportCsvGenerateServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(ImportCsvGenerateServlet.class);

    private final CpscImportCsvService service = new CpscImportCsvService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try {
            log.info("收到Import CSV生成请求。remoteAddr={}", request.getRemoteAddr());
            String certifierId = request.getParameter("certifierId");
            String collectionId = request.getParameter("collectionId");
            Part excelPart = request.getPart("excelFile");

            if (isBlank(certifierId)) {
                throw new IllegalArgumentException("Certifier ID 不能为空。");
            }

            if (isBlank(collectionId)) {
                throw new IllegalArgumentException("Collection ID 不能为空。");
            }

            if (excelPart == null || excelPart.getSize() == 0) {
                throw new IllegalArgumentException("请上传 Excel 文件。");
            }

            String originalFileName = getSubmittedFileName(excelPart);
            log.info("上传文件信息。originalFileName={}, size={} bytes, certifierId={}, collectionId={}",
                    originalFileName, excelPart.getSize(), certifierId, collectionId);

            File outputDir = new File(getServletContext().getRealPath("/generated-import-csv"));

            CpscImportCsvService.GenerateResult result;
            try (InputStream in = excelPart.getInputStream()) {
                result = service.generateCsvAndSaveDb(
                        in,
                        originalFileName,
                        certifierId,
                        collectionId,
                        outputDir
                );
            }

            String downloadUrl = request.getContextPath()
                    + "/generated-import-csv/"
                    + result.getFileName();

            String json = "{"
                    + "\"success\":true,"
                    + "\"fileName\":\"" + escapeJson(result.getFileName()) + "\","
                    + "\"downloadUrl\":\"" + escapeJson(downloadUrl) + "\","
                    + "\"batchId\":" + result.getBatchId() + ","
                    + "\"rowCount\":" + result.getRowCount()
                    + "}";

            log.info("请求处理成功。fileName={}, batchId={}, rowCount={}", result.getFileName(), result.getBatchId(), result.getRowCount());
            response.getWriter().write(json);

        } catch (Exception e) {
            log.error("Import CSV生成失败。message={}", e.getMessage(), e);
            response.setStatus(400);

            String json = "{"
                    + "\"success\":false,"
                    + "\"message\":\"" + escapeJson(e.getMessage()) + "\""
                    + "}";

            response.getWriter().write(json);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String getSubmittedFileName(Part part) {
        String header = part.getHeader("content-disposition");

        if (header == null) {
            return "upload.xlsx";
        }

        for (String item : header.split(";")) {
            item = item.trim();

            if (item.startsWith("filename")) {
                String fileName = item.substring(item.indexOf('=') + 1).trim().replace("\"", "");
                return new File(fileName).getName();
            }
        }

        return "upload.xlsx";
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
