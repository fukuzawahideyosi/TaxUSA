package com.cpsc.efiling.servlet;

import com.cpsc.efiling.model.BatchView;
import com.cpsc.efiling.model.ProductView;
import com.cpsc.efiling.service.ProductQueryService;
import com.cpsc.efiling.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class ProductListServlet extends HttpServlet {
    private final ProductQueryService queryService = new ProductQueryService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
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

            request.getRequestDispatcher("/products.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("productsJson", "[]");
            request.setAttribute("batchesJson", "[]");
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/products.jsp").forward(request, response);
        }
    }
}
