package com.cpsc.efiling.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class PocData {
    private static final Logger log = LogManager.getLogger(PocData.class);
    private String pocCode;
    private String isNew;
    private String type;
    private String gln;
    private String alternateId;
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

    public String getPocCode() { return pocCode; }
    public void setPocCode(String pocCode) { this.pocCode = pocCode; }

    public String getIsNew() { return isNew; }
    public void setIsNew(String isNew) { this.isNew = isNew; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getGln() { return gln; }
    public void setGln(String gln) { this.gln = gln; }

    public String getAlternateId() { return alternateId; }
    public void setAlternateId(String alternateId) { this.alternateId = alternateId; }

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
}
