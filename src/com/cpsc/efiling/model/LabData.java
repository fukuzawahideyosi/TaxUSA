package com.cpsc.efiling.model;

import java.util.ArrayList;
import java.util.List;

public class LabData {
    private String productId;
    private String alternateId;
    private String isNew;
    private String type;
    private String cpscId;
    private String gln;
    private String name;
    private String addressLine1;
    private String addressLine2;
    private String aptNumber;
    private String city;
    private String stateProvince;
    private String country;
    private String postalCode;
    private String phone;
    private String email;
    private List<String> citationCodes = new ArrayList<String>();
    private String testReportId;
    private String testURL;
    private String testReportAccessKey;
    private boolean component;
    private String componentDescription;

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getAlternateId() { return alternateId; }
    public void setAlternateId(String alternateId) { this.alternateId = alternateId; }

    public String getIsNew() { return isNew; }
    public void setIsNew(String isNew) { this.isNew = isNew; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCpscId() { return cpscId; }
    public void setCpscId(String cpscId) { this.cpscId = cpscId; }

    public String getGln() { return gln; }
    public void setGln(String gln) { this.gln = gln; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getAptNumber() { return aptNumber; }
    public void setAptNumber(String aptNumber) { this.aptNumber = aptNumber; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStateProvince() { return stateProvince; }
    public void setStateProvince(String stateProvince) { this.stateProvince = stateProvince; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getCitationCodes() { return citationCodes; }
    public void setCitationCodes(List<String> citationCodes) { this.citationCodes = citationCodes; }

    public String getTestReportId() { return testReportId; }
    public void setTestReportId(String testReportId) { this.testReportId = testReportId; }

    public String getTestURL() { return testURL; }
    public void setTestURL(String testURL) { this.testURL = testURL; }

    public String getTestReportAccessKey() { return testReportAccessKey; }
    public void setTestReportAccessKey(String testReportAccessKey) { this.testReportAccessKey = testReportAccessKey; }

    public boolean isComponent() { return component; }
    public void setComponent(boolean component) { this.component = component; }

    public String getComponentDescription() { return componentDescription; }
    public void setComponentDescription(String componentDescription) { this.componentDescription = componentDescription; }
}
