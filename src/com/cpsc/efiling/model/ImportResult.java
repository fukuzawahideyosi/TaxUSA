package com.cpsc.efiling.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class ImportResult {
    private static final Logger log = LogManager.getLogger(ImportResult.class);
    private long batchId;
    private int productCount;
    private String requestJson;

    public ImportResult(long batchId, int productCount, String requestJson) {
        this.batchId = batchId;
        this.productCount = productCount;
        this.requestJson = requestJson;
    }

    public long getBatchId() { return batchId; }
    public int getProductCount() { return productCount; }
    public String getRequestJson() { return requestJson; }
}
