package com.cpsc.efiling.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class LabView {
    private static final Logger log = LogManager.getLogger(LabView.class);
    private long productLabId;
    private String labAlternateId;
    private String labName;
    private String labType;
    private String cpscId;
    private String citationCodes;
    private String testReportId;
    private String testUrl;
    private boolean component;
    private String componentDescription;

    public long getProductLabId() { return productLabId; }
    public void setProductLabId(long productLabId) { this.productLabId = productLabId; }

    public String getLabAlternateId() { return labAlternateId; }
    public void setLabAlternateId(String labAlternateId) { this.labAlternateId = labAlternateId; }

    public String getLabName() { return labName; }
    public void setLabName(String labName) { this.labName = labName; }

    public String getLabType() { return labType; }
    public void setLabType(String labType) { this.labType = labType; }

    public String getCpscId() { return cpscId; }
    public void setCpscId(String cpscId) { this.cpscId = cpscId; }

    public String getCitationCodes() { return citationCodes; }
    public void setCitationCodes(String citationCodes) { this.citationCodes = citationCodes; }

    public String getTestReportId() { return testReportId; }
    public void setTestReportId(String testReportId) { this.testReportId = testReportId; }

    public String getTestUrl() { return testUrl; }
    public void setTestUrl(String testUrl) { this.testUrl = testUrl; }

    public boolean isComponent() { return component; }
    public void setComponent(boolean component) { this.component = component; }

    public String getComponentDescription() { return componentDescription; }
    public void setComponentDescription(String componentDescription) { this.componentDescription = componentDescription; }
}
