package com.cpsc.efiling.model;

public class BatchView {
    private long id;
    private String certifierId;
    private String collectionId;
    private String importId;
    private String importStatus;
    private String statusMessage;
    private int productCount;
    private String createdAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCertifierId() { return certifierId; }
    public void setCertifierId(String certifierId) { this.certifierId = certifierId; }

    public String getCollectionId() { return collectionId; }
    public void setCollectionId(String collectionId) { this.collectionId = collectionId; }

    public String getImportId() { return importId; }
    public void setImportId(String importId) { this.importId = importId; }

    public String getImportStatus() { return importStatus; }
    public void setImportStatus(String importStatus) { this.importStatus = importStatus; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public int getProductCount() { return productCount; }
    public void setProductCount(int productCount) { this.productCount = productCount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
