package com.cpsc.efiling.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

public class ProductView {
    private static final Logger log = LogManager.getLogger(ProductView.class);
    private long id;
    private String certifierId;
    private String collectionId;
    private String versionId;
    private String primaryProductId;
    private String primaryProductIdType;
    private String certificateType;
    private String name;
    private String tradeBrandName;
    private String description;
    private String color;
    private String style;
    private String manufactureDate;
    private String productionStartDate;
    private String productionEndDate;
    private String lotNumber;
    private String lotNumberAssignedBy;
    private String lastTestDate;
    private String productUpdate;
    private String versionIdToUpdate;
    private String dataStatus;
    private String remark;

    private String manufacturerAlternateId;
    private String manufacturerName;
    private String manufacturerCountry;
    private String manufacturerEmail;
    private String manufacturerIsNew;

    private String pocCode;
    private String pocType;
    private String pocName;
    private String pocEmail;
    private String pocIsNew;

    private List<LabView> labs = new ArrayList<LabView>();
    private List<String> exemptions = new ArrayList<String>();
    private List<ValidationError> errors = new ArrayList<ValidationError>();

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCertifierId() { return certifierId; }
    public void setCertifierId(String certifierId) { this.certifierId = certifierId; }

    public String getCollectionId() { return collectionId; }
    public void setCollectionId(String collectionId) { this.collectionId = collectionId; }

    public String getVersionId() { return versionId; }
    public void setVersionId(String versionId) { this.versionId = versionId; }

    public String getPrimaryProductId() { return primaryProductId; }
    public void setPrimaryProductId(String primaryProductId) { this.primaryProductId = primaryProductId; }

    public String getPrimaryProductIdType() { return primaryProductIdType; }
    public void setPrimaryProductIdType(String primaryProductIdType) { this.primaryProductIdType = primaryProductIdType; }

    public String getCertificateType() { return certificateType; }
    public void setCertificateType(String certificateType) { this.certificateType = certificateType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTradeBrandName() { return tradeBrandName; }
    public void setTradeBrandName(String tradeBrandName) { this.tradeBrandName = tradeBrandName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }

    public String getManufactureDate() { return manufactureDate; }
    public void setManufactureDate(String manufactureDate) { this.manufactureDate = manufactureDate; }

    public String getProductionStartDate() { return productionStartDate; }
    public void setProductionStartDate(String productionStartDate) { this.productionStartDate = productionStartDate; }

    public String getProductionEndDate() { return productionEndDate; }
    public void setProductionEndDate(String productionEndDate) { this.productionEndDate = productionEndDate; }

    public String getLotNumber() { return lotNumber; }
    public void setLotNumber(String lotNumber) { this.lotNumber = lotNumber; }

    public String getLotNumberAssignedBy() { return lotNumberAssignedBy; }
    public void setLotNumberAssignedBy(String lotNumberAssignedBy) { this.lotNumberAssignedBy = lotNumberAssignedBy; }

    public String getLastTestDate() { return lastTestDate; }
    public void setLastTestDate(String lastTestDate) { this.lastTestDate = lastTestDate; }

    public String getProductUpdate() { return productUpdate; }
    public void setProductUpdate(String productUpdate) { this.productUpdate = productUpdate; }

    public String getVersionIdToUpdate() { return versionIdToUpdate; }
    public void setVersionIdToUpdate(String versionIdToUpdate) { this.versionIdToUpdate = versionIdToUpdate; }

    public String getDataStatus() { return dataStatus; }
    public void setDataStatus(String dataStatus) { this.dataStatus = dataStatus; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getManufacturerAlternateId() { return manufacturerAlternateId; }
    public void setManufacturerAlternateId(String manufacturerAlternateId) { this.manufacturerAlternateId = manufacturerAlternateId; }

    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }

    public String getManufacturerCountry() { return manufacturerCountry; }
    public void setManufacturerCountry(String manufacturerCountry) { this.manufacturerCountry = manufacturerCountry; }

    public String getManufacturerEmail() { return manufacturerEmail; }
    public void setManufacturerEmail(String manufacturerEmail) { this.manufacturerEmail = manufacturerEmail; }

    public String getManufacturerIsNew() { return manufacturerIsNew; }
    public void setManufacturerIsNew(String manufacturerIsNew) { this.manufacturerIsNew = manufacturerIsNew; }

    public String getPocCode() { return pocCode; }
    public void setPocCode(String pocCode) { this.pocCode = pocCode; }

    public String getPocType() { return pocType; }
    public void setPocType(String pocType) { this.pocType = pocType; }

    public String getPocName() { return pocName; }
    public void setPocName(String pocName) { this.pocName = pocName; }

    public String getPocEmail() { return pocEmail; }
    public void setPocEmail(String pocEmail) { this.pocEmail = pocEmail; }

    public String getPocIsNew() { return pocIsNew; }
    public void setPocIsNew(String pocIsNew) { this.pocIsNew = pocIsNew; }

    public List<LabView> getLabs() { return labs; }
    public void setLabs(List<LabView> labs) { this.labs = labs; }

    public List<String> getExemptions() { return exemptions; }
    public void setExemptions(List<String> exemptions) { this.exemptions = exemptions; }

    public List<ValidationError> getErrors() { return errors; }
    public void setErrors(List<ValidationError> errors) { this.errors = errors; }
}
