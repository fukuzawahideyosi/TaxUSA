package com.cpsc.efiling.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

public class ProductData {
    private static final Logger log = LogManager.getLogger(ProductData.class);
    private String productUpdate;
    private String versionId;
    private String versionIdToUpdate;
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
    private String manufacturerAlternateId;
    private String pocCode;
    private String notes;
    private List<IdentifierData> identifiers = new ArrayList<IdentifierData>();
    private ManufacturerData manufacturer;
    private PocData poc;
    private List<LabData> labs = new ArrayList<LabData>();
    private List<String> exemptions = new ArrayList<String>();

    public String getProductUpdate() { return productUpdate; }
    public void setProductUpdate(String productUpdate) { this.productUpdate = productUpdate; }

    public String getVersionId() { return versionId; }
    public void setVersionId(String versionId) { this.versionId = versionId; }

    public String getVersionIdToUpdate() { return versionIdToUpdate; }
    public void setVersionIdToUpdate(String versionIdToUpdate) { this.versionIdToUpdate = versionIdToUpdate; }

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

    public String getManufacturerAlternateId() { return manufacturerAlternateId; }
    public void setManufacturerAlternateId(String manufacturerAlternateId) { this.manufacturerAlternateId = manufacturerAlternateId; }

    public String getPocCode() { return pocCode; }
    public void setPocCode(String pocCode) { this.pocCode = pocCode; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<IdentifierData> getIdentifiers() { return identifiers; }
    public void setIdentifiers(List<IdentifierData> identifiers) { this.identifiers = identifiers; }

    public ManufacturerData getManufacturer() { return manufacturer; }
    public void setManufacturer(ManufacturerData manufacturer) { this.manufacturer = manufacturer; }

    public PocData getPoc() { return poc; }
    public void setPoc(PocData poc) { this.poc = poc; }

    public List<LabData> getLabs() { return labs; }
    public void setLabs(List<LabData> labs) { this.labs = labs; }

    public List<String> getExemptions() { return exemptions; }
    public void setExemptions(List<String> exemptions) { this.exemptions = exemptions; }
}
