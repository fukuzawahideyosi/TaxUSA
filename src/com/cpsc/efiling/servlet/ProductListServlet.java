package com.cpsc.efiling.servlet;

import com.cpsc.efiling.model.BatchView;
import com.cpsc.efiling.model.ProductView;
import com.cpsc.efiling.service.ProductQueryService;
import com.cpsc.efiling.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/products")
public class ProductListServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(ProductListServlet.class);
    private final ProductQueryService queryService = new ProductQueryService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            log.info("打开products.jsp。remoteAddr={}, queryString={}", request.getRemoteAddr(), request.getQueryString());
            Long batchId = null;
            String batchIdText = request.getParameter("batchId");
            if (!StringUtil.isBlank(batchIdText)) {
                batchId = Long.parseLong(batchIdText);
            }

            List<ProductView> products = queryService.listProducts(batchId);
            List<BatchView> batches = queryService.listBatches();

            String productsJson = objectMapper.writeValueAsString(products).replace("</", "<\\/");
            String batchesJson = objectMapper.writeValueAsString(batches).replace("</", "<\\/");

            request.setAttribute("productsJson", productsJson);
            request.setAttribute("batchesJson", batchesJson);
            request.setAttribute("selectedBatchId", batchId == null ? "" : String.valueOf(batchId));
            request.setAttribute("success", request.getParameter("success"));
            request.setAttribute("error", request.getParameter("error"));

            log.info("products.jsp数据准备完成。batchId={}, products={}, batches={}", batchId, products.size(), batches.size());
            request.getRequestDispatcher("/products.jsp").forward(request, response);
        } catch (Exception e) {
            log.error("products.jsp加载失败。message={}", e.getMessage(), e);
            request.setAttribute("productsJson", "[]");
            request.setAttribute("batchesJson", "[]");
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/products.jsp").forward(request, response);
        }
    }
}
